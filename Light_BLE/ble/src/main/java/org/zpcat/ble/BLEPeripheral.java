package org.zpcat.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;

import java.util.List;
import java.util.UUID;

/**
 * Created by
 *
 * http://gaiger-programming.blogspot.jp/2015/05/android-as-bluetooth-low-energy.html
 *
 */
public class BLEPeripheral {

    public interface ConnectionCallback {
        void onConnectionStateChange(BluetoothDevice device, int newState);
    }

    public interface WriteCallback {
        void onWrite(byte[] data);
    }

    public static boolean isEnableBluetooth(){
        return BluetoothAdapter.getDefaultAdapter().isEnabled();
    }

    public static String getAddress() {
        return BluetoothAdapter.getDefaultAdapter().getAddress();
    }

    BluetoothManager mManager;
    BluetoothAdapter mAdapter;
    BluetoothLeAdvertiser mLeAdvertiser;

    AdvertiseSettings.Builder mAdvSettingBuilder;
    AdvertiseData.Builder mAdvDataBuilder;

    BluetoothGattServer mGattServer;

    ConnectionCallback mConnectionCallback;

    WriteCallback mWriteCallback;

    private AdvertiseCallback mAdvCallback = new AdvertiseCallback() {

        @Override
        public void onStartFailure(int errorCode){
            Log.d("advertise","onStartFailure");
        }

        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect){
            Log.d("advertise","onStartSuccess");
        };
    };

    private final BluetoothGattServerCallback mGattServerCallback
            = new BluetoothGattServerCallback(){

        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState){
            Log.d("GattServer", "Our gatt server connection state changed, new state ");
            Log.d("GattServer", Integer.toString(newState));

            if(null != mConnectionCallback && BluetoothGatt.GATT_SUCCESS == status)
                mConnectionCallback.onConnectionStateChange(device, newState);

            super.onConnectionStateChange(device, status, newState);
        }

        @Override
        public void onServiceAdded(int status, BluetoothGattService service) {
            Log.d("GattServer", "Our gatt server service was added.");
            super.onServiceAdded(status, service);
        }

        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
            Log.d("GattServer", "Our gatt characteristic was read.");
            super.onCharacteristicReadRequest(device, requestId, offset, characteristic);
            mGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset,
                    characteristic.getValue());
        }

        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            Log.d("GattServer", "We have received a write request for one of our hosted characteristics");
            //Log.d("GattServer", "data = "+ value.toString());
            super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);

            if(null != mWriteCallback)
                mWriteCallback.onWrite(value);

        }

        @Override
        public void onNotificationSent(BluetoothDevice device, int status)
        {
            Log.d("GattServer", "onNotificationSent");
            super.onNotificationSent(device, status);
        }

        @Override
        public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattDescriptor descriptor) {
            Log.d("GattServer", "Our gatt server descriptor was read.");
            super.onDescriptorReadRequest(device, requestId, offset, descriptor);

        }

        @Override
        public void onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            Log.d("GattServer", "Our gatt server descriptor was written.");
            super.onDescriptorWriteRequest(device, requestId, descriptor, preparedWrite, responseNeeded, offset, value);
        }

        @Override
        public void onExecuteWrite(BluetoothDevice device, int requestId, boolean execute) {
            Log.d("GattServer", "Our gatt server on execute write.");
            super.onExecuteWrite(device, requestId, execute);
        }

    };

    public int init(Context context){

        if(null == mManager)
        {
            mManager = (BluetoothManager)context.getSystemService(Context.BLUETOOTH_SERVICE);

            if(null == mManager)
                return -1;

            if(!context.getPackageManager().
                    hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE))
                return -2;
        }

        if(null == mAdapter)
        {
            mAdapter = mManager.getAdapter();

            if(!mAdapter.isMultipleAdvertisementSupported())
                return -3;
        }

        if(null == mAdvSettingBuilder)
        {
            mAdvSettingBuilder = new AdvertiseSettings.Builder();
            mAdvSettingBuilder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY);
            mAdvSettingBuilder.setConnectable(true);
            mAdvSettingBuilder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH);
        }

        if(null == mAdvDataBuilder)
        {
            mAdvDataBuilder = new AdvertiseData.Builder();
            mAdapter.setName("SimplePeripheral");
            mAdvDataBuilder.setIncludeDeviceName(true);
        }


        if(null == mGattServer)
        {
            mGattServer = mManager.openGattServer(context, mGattServerCallback);

            if(null == mGattServer)
                return -4;

            addDeviceInfoService();
        }

        return 0;
    }

    public void setConnectionCallback(ConnectionCallback callback)
    {
        mConnectionCallback = callback;
    }

    public void close()
    {
        if(null != mLeAdvertiser)
            stopAdvertise();

        if(null != mGattServer)
            mGattServer.close();
        mGattServer = null;

        if(null != mAdvDataBuilder)
            mAdvDataBuilder = null;

        if(null != mAdvSettingBuilder)
            mAdvSettingBuilder = null;

        if(null != mAdapter)
            mAdapter = null;

        if(null != mManager)
            mManager = null;
    }

    public void setService(String read1Data, String read2Data, WriteCallback
            writeCallBack)
    {

        if(null == mGattServer)
            return ;

        stopAdvertise();

        final String  SERVICE_A = "0000fff0-0000-1000-8000-00805f9b34fb";
        final String  CHAR_READ1 = "0000fff1-0000-1000-8000-00805f9b34fb";
        final String  CHAR_READ2 = "0000fff2-0000-1000-8000-00805f9b34fb";
        final String  CHAR_WRITE = "0000fff3-0000-1000-8000-00805f9b34fb";
        final String  CHAR_NOTIFY = "0000fff4-0000-1000-8000-00805f9b34fb";



        BluetoothGattService previousService =
                mGattServer.getService( UUID.fromString(SERVICE_A));

        if(null != previousService)
            mGattServer.removeService(previousService);



        BluetoothGattCharacteristic read1Characteristic = new BluetoothGattCharacteristic(
                UUID.fromString(CHAR_READ1),
                BluetoothGattCharacteristic.PROPERTY_READ,
                BluetoothGattCharacteristic.PERMISSION_READ
        );

        BluetoothGattCharacteristic read2Characteristic = new BluetoothGattCharacteristic(
                UUID.fromString(CHAR_READ2),
                BluetoothGattCharacteristic.PROPERTY_READ,
                BluetoothGattCharacteristic.PERMISSION_READ
        );

        BluetoothGattCharacteristic writeCharacteristic = new BluetoothGattCharacteristic(
                UUID.fromString(CHAR_WRITE),
                BluetoothGattCharacteristic.PROPERTY_WRITE,
                BluetoothGattCharacteristic.PERMISSION_WRITE
        );




        read1Characteristic.setValue(read1Data.getBytes());
        read2Characteristic.setValue(read2Data.getBytes());
        mWriteCallback = writeCallBack;


        BluetoothGattService AService = new BluetoothGattService(
                UUID.fromString(SERVICE_A),
                BluetoothGattService.SERVICE_TYPE_PRIMARY);


        AService.addCharacteristic(read1Characteristic);
        AService.addCharacteristic(read2Characteristic);
        AService.addCharacteristic(writeCharacteristic);



        final BluetoothGattCharacteristic notifyCharacteristic = new BluetoothGattCharacteristic(
                UUID.fromString(CHAR_NOTIFY),
                BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                BluetoothGattCharacteristic.PERMISSION_READ
        );


        notifyCharacteristic.setValue("0");
        AService.addCharacteristic(notifyCharacteristic);

        final Handler handler = new Handler();

        Thread thread = new Thread() {
            int i = 0;

            @Override
            public void run() {

                while(true) {

                    try {
                        sleep(1500);
                    } catch (InterruptedException e) {}

                    handler.post(this);

                    List<BluetoothDevice> connectedDevices
                            = mManager.getConnectedDevices(BluetoothProfile.GATT);

                    if(null != connectedDevices)
                    {
                        notifyCharacteristic.setValue(String.valueOf(i).getBytes());

                        if(0 != connectedDevices.size())
                            mGattServer.notifyCharacteristicChanged(connectedDevices.get(0),
                                    notifyCharacteristic, false);
                    }
                    i++;
                }
            }
        };

        thread.start();


        mGattServer.addService(AService);
    }


    public void startAdvertise(String scanRespenseName)
    {
        mAdapter.setName(scanRespenseName);
        mAdvDataBuilder.setIncludeDeviceName(true);

        startAdvertise();
    }

    public void startAdvertise()
    {
        if(null == mAdapter)
            return;

        if (null == mLeAdvertiser)
            mLeAdvertiser = mAdapter.getBluetoothLeAdvertiser();

        if(null == mLeAdvertiser)
            return;

        mLeAdvertiser.startAdvertising(mAdvSettingBuilder.build(),
                mAdvDataBuilder.build(), mAdvCallback);
    }

    public void stopAdvertise()
    {
        if(null != mLeAdvertiser)
            mLeAdvertiser.stopAdvertising(mAdvCallback);

        mLeAdvertiser = null;
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
}
