package org.zpcat.ble.data;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;

import org.zpcat.ble.BLEApplication;
import org.zpcat.ble.injector.ApplicationContext;

import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by jacobsu on 5/10/16.
 */
@Singleton
public class BLEPeripheralServer {

    BluetoothLeAdvertiser mBLEAdvertiser;

    @Inject
    AdvertiseSettings.Builder mAdvertiseSettingBuilder;

    @Inject
    AdvertiseData.Builder mAdvertiseDataBuilder;

    @Inject
    BluetoothManager mBtManager;

    @Inject
    BluetoothAdapter mBtAdapter;

    private BluetoothGattServer mGattServer;

    private final BluetoothGattServerCallback mGattServerCallback = new BluetoothGattServerCallback() {
        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            super.onConnectionStateChange(device, status, newState);
        }

        @Override
        public void onServiceAdded(int status, BluetoothGattService service) {
            super.onServiceAdded(status, service);
        }

        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicReadRequest(device, requestId, offset, characteristic);
        }

        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);
        }

        @Override
        public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattDescriptor descriptor) {
            super.onDescriptorReadRequest(device, requestId, offset, descriptor);
        }

        @Override
        public void onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            super.onDescriptorWriteRequest(device, requestId, descriptor, preparedWrite, responseNeeded, offset, value);
        }

        @Override
        public void onExecuteWrite(BluetoothDevice device, int requestId, boolean execute) {
            super.onExecuteWrite(device, requestId, execute);
        }

        @Override
        public void onNotificationSent(BluetoothDevice device, int status) {
            super.onNotificationSent(device, status);
        }

        @Override
        public void onMtuChanged(BluetoothDevice device, int mtu) {
            super.onMtuChanged(device, mtu);
        }
    };

    private AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
        }

        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);
        }
    };

    @Inject
    public BLEPeripheralServer(@ApplicationContext Context context) {
        ((BLEApplication)context).getApplicationComponent().inject(this);

        if (supportPeripheralMode()) {
            mBLEAdvertiser = mBtAdapter.getBluetoothLeAdvertiser();

            mGattServer = mBtManager.openGattServer(context, mGattServerCallback);
            addDeviceInfoService();
        }
    }

    public void startPeripheralMode(String name) {
        if (!supportPeripheralMode()) {
            return;
        }

        if (name != null) {
            mBtAdapter.setName(name);
        }

        mBLEAdvertiser.startAdvertising(mAdvertiseSettingBuilder.build(), mAdvertiseDataBuilder.build(),
                mAdvertiseCallback);
    }

    public void stopPeripheralMode() {
        if (!supportPeripheralMode()) {
            return;
        }

        mBLEAdvertiser.stopAdvertising(mAdvertiseCallback);
    }

    private void addDeviceInfoService() {
        if(null == mGattServer)
            return;

        final String SERVICE_DEVICE_INFORMATION = "0000180a-0000-1000-8000-00805f9b34fb";
        final String SOFTWARE_REVISION_STRING = "00002A28-0000-1000-8000-00805f9b34fb";


        BluetoothGattService previousService =
                mGattServer.getService( UUID.fromString(SERVICE_DEVICE_INFORMATION));

        if(null != previousService)
            mGattServer.removeService(previousService);


        BluetoothGattCharacteristic softwareVerCharacteristic = new BluetoothGattCharacteristic(
                UUID.fromString(SOFTWARE_REVISION_STRING),
                BluetoothGattCharacteristic.PROPERTY_READ,
                BluetoothGattCharacteristic.PERMISSION_READ
        );

        BluetoothGattService deviceInfoService = new BluetoothGattService(
                UUID.fromString(SERVICE_DEVICE_INFORMATION),
                BluetoothGattService.SERVICE_TYPE_PRIMARY);


        softwareVerCharacteristic.setValue(new String("0.0.0").getBytes());

        deviceInfoService.addCharacteristic(softwareVerCharacteristic);
        mGattServer.addService(deviceInfoService);
    }

    public boolean supportPeripheralMode() {
        return mBtAdapter.isMultipleAdvertisementSupported();
    }
}
