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

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * Created by jacobsu on 4/27/16.
 */
public class BLEDataServer {

    private Context mContext;
    private BluetoothLeScanner mLeScanner;
    private BLEPeripheralServer mPeripheralServer;

    private ObservableEmitter<BluetoothDevice> mLEScanEmitter;

    // BlueGatt -> BLEData
    // BlueGatt -> ObservableEmitter
    private List<BLEData> mBLEDatas = new ArrayList<>();
    private Map<ObservableEmitter<BLEData>, BluetoothGatt> mGattMap = new HashMap<>();

    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            if (mLEScanEmitter != null) {
                mLEScanEmitter.onNext(result.getDevice());
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            if (mLEScanEmitter != null) {
                for (ScanResult r : results) {
                    mLEScanEmitter.onNext(r.getDevice());
                }
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            if (mLEScanEmitter != null) {
                // mLEScanEmitter.onError(new Throwable("scan failed with errorCode: " + errorCode));
                mLEScanEmitter.onComplete();
            }
        }
    };

    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            BLEData d = findBLEData(gatt);
            List<ObservableEmitter<BLEData>> emitters = findObservableEmitter(gatt);

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                d.connectedState = true;
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                d.connectedState = false;
            }

            for (ObservableEmitter<BLEData> s : emitters) {
                s.onNext(d);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            BLEData d = findBLEData(gatt);
            List<ObservableEmitter<BLEData>> subscribers = findObservableEmitter(gatt);

            if (status == BluetoothGatt.GATT_SUCCESS) {
                d.services = gatt.getServices();

                for (ObservableEmitter<BLEData> s : subscribers) {
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
            List<ObservableEmitter<BLEData>> emitters = findObservableEmitter(gatt);

            if (status == BluetoothGatt.GATT_SUCCESS) {
                d.rssi = rssi;

                for (ObservableEmitter<BLEData> s : emitters) {
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
    public BLEDataServer(@ApplicationContext Context context, BluetoothLeScanner leScanner, BLEPeripheralServer peripheralServer) {
        mContext = context;
        mLeScanner = leScanner;
        mPeripheralServer = peripheralServer;
    }

    Observable<BluetoothDevice> scanBLEPeripheral(final boolean enabled) {

        return Observable.create(new ObservableOnSubscribe<BluetoothDevice>() {
            @Override
            public void subscribe(ObservableEmitter<BluetoothDevice> e) throws Exception {
                if (enabled) {
                    mLEScanEmitter = e;
                    mLeScanner.startScan(mScanCallback);
                } else {
                    e.onComplete();
                    mLeScanner.stopScan(mScanCallback);
                }

            }
        });
    }

    Observable<BLEData> connect(final BluetoothDevice device) {
        // if device is already connected,
        return Observable.create(new ObservableOnSubscribe<BLEData>() {
            @Override
            public void subscribe(ObservableEmitter<BLEData> e) throws Exception {
                BluetoothGatt gatt = findBluetoothGatt(device);

                if (gatt == null) {
                    gatt = device.connectGatt(mContext, false, mGattCallback);
                } else {
                    e.onNext(findBLEData(gatt));
                }

                mGattMap.put(e, gatt);
            }
        });
    }

    public List<BLEData> getRemoteBLEDatas() {
        return mBLEDatas;
    }

    public boolean readRemoteRssi(BluetoothDevice device) {
        BluetoothGatt gatt = findBluetoothGatt(device);

        if (gatt != null) {
            return gatt.readRemoteRssi();
        }

        return false;
    }

    public void startCentralMode() {
        // stop Peripheral Mode first
        stopPeripheralMode();
    }

    public void stopCentralMode() {
        // stop scan
        // disconnect from all gatt
    }

    public boolean supportLEAdvertiser() {
        return mPeripheralServer.supportPeripheralMode();
    }

    public void startPeripheralMode(String name) {
        // stop Central mode first
        stopCentralMode();

        mPeripheralServer.startPeripheralMode(name);
    }

    public void stopPeripheralMode() {
        mPeripheralServer.stopPeripheralMode();
    }

    private BluetoothGatt findBluetoothGatt(BluetoothDevice device) {
        for (BluetoothGatt d : mGattMap.values()) {
            if (d.getDevice() == device) {
                return d;
            }
        }

        return null;
    }

    private List<ObservableEmitter<BLEData>> findObservableEmitter(BluetoothGatt gatt) {
        List<ObservableEmitter<BLEData>> emitters = new ArrayList<>();

        for(ObservableEmitter<BLEData> s : mGattMap.keySet()) {
            if (mGattMap.get(s) == gatt) {
                emitters.add(s);
            }
        }

        return emitters;
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
