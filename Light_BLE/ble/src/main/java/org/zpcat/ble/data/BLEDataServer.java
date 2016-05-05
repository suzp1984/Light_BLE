package org.zpcat.ble.data;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;

import org.zpcat.ble.injector.ApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by jacobsu on 4/27/16.
 */
public class BLEDataServer {

    private Context mContext;
    private BluetoothLeScanner mLeScanner;

    private Subscriber<BluetoothDevice> mLEScanSubscriber;

    // BlueGatt -> BLEData
    // BlueGatt -> subscriber
    private List<BLEData> mBLEDatas = new ArrayList<>();
    private Map<Subscriber<BLEData>, BluetoothGatt> mGattMap = new HashMap<>();

    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            if (mLEScanSubscriber != null) {
                mLEScanSubscriber.onNext(result.getDevice());
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            if (mLEScanSubscriber != null) {
                for (ScanResult r : results) {
                    mLEScanSubscriber.onNext(r.getDevice());
                }
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            if (mLEScanSubscriber != null) {
                mLEScanSubscriber.onError(new Throwable("scan failed with errorCode: " + errorCode));
                mLEScanSubscriber.onCompleted();
            }
        }
    };

    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            BLEData d = findBLEData(gatt);
            List<Subscriber<BLEData>> subscribers = findSubscriber(gatt);

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                d.connectedState = true;
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                d.connectedState = false;
            }

            for (Subscriber<BLEData> s : subscribers) {
                s.onNext(d);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            BLEData d = findBLEData(gatt);
            List<Subscriber<BLEData>> subscribers = findSubscriber(gatt);

            if (status == BluetoothGatt.GATT_SUCCESS) {
                d.services = gatt.getServices();

                for (Subscriber<BLEData> s : subscribers) {
                    s.onNext(d);
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            BLEData d = findBLEData(gatt);
            List<Subscriber<BLEData>> subscribers = findSubscriber(gatt);

            if (status == BluetoothGatt.GATT_SUCCESS) {
                d.rssi = rssi;

                for (Subscriber<BLEData> s : subscribers) {
                    s.onNext(d);
                }
            }
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
        }
    };

    @Inject
    public BLEDataServer(@ApplicationContext Context context, BluetoothLeScanner leScanner) {
        mContext = context;
        mLeScanner = leScanner;
    }

    Observable<BluetoothDevice> scanBLEPeripheral(final boolean enabled) {
        return Observable.create(new Observable.OnSubscribe<BluetoothDevice>() {
            @Override
            public void call(Subscriber<? super BluetoothDevice> subscriber) {
                if (enabled) {
                    mLEScanSubscriber = (Subscriber<BluetoothDevice>) subscriber;
                    subscriber.onStart();

                    mLeScanner.startScan(mScanCallback);
                } else {
                    // maybe mLEScanSubscriber should stop too.
                    subscriber.onCompleted();
                    mLeScanner.stopScan(mScanCallback);
                }
            }
        });
    }

    Observable<BLEData> connect(final BluetoothDevice device) {
        // if device is already connected,

        return Observable.create(new Observable.OnSubscribe<BLEData>() {
            @Override
            public void call(Subscriber<? super BLEData> subscriber) {

                subscriber.onStart();
                BluetoothGatt gatt = findBluetoothGatt(device);

                if (gatt == null) {
                    gatt = device.connectGatt(mContext, false, mGattCallback);
                } else {
                    subscriber.onNext(findBLEData(gatt));
                }

                mGattMap.put((Subscriber<BLEData>) subscriber, gatt);
            }
        });
    }

    public List<BluetoothDevice> getRemoteDevices() {
        List<BluetoothDevice> devices = new ArrayList<>();

        for (BLEData data : mBLEDatas) {
            devices.add(data.device);
        }

        return devices;
    }

    public boolean readRemoteRssi(BluetoothDevice device) {
        BluetoothGatt gatt = findBluetoothGatt(device);

        if (gatt != null) {
            return gatt.readRemoteRssi();
        }

        return false;
    }

    private BluetoothGatt findBluetoothGatt(BluetoothDevice device) {
        for (BluetoothGatt d : mGattMap.values()) {
            if (d.getDevice() == device) {
                return d;
            }
        }

        return null;
    }

    private List<Subscriber<BLEData>> findSubscriber(BluetoothGatt gatt) {
        List<Subscriber<BLEData>> subscribers = new ArrayList<>();

        for(Subscriber<BLEData> s : mGattMap.keySet()) {
            if (mGattMap.get(s) == gatt) {
                subscribers.add(s);
            }
        }

        return subscribers;
    }

    private BLEData findBLEData(BluetoothGatt gatt) {
        for (BLEData d : mBLEDatas) {
            if (gatt.getDevice() == d.device) {
                return d;
            }
        }

        BLEData d = new BLEData(gatt.getDevice());
        mBLEDatas.add(d);

        return d;
    }

    public class BLEData {
        public BluetoothDevice device;
        public int rssi;
        public String data;
        public boolean connectedState;
        public List<BluetoothGattService> services;

        public BLEData(BluetoothDevice device) {
            this.device = device;
        }
    }
}
