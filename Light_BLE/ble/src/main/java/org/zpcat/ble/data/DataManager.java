package org.zpcat.ble.data;

import android.bluetooth.BluetoothDevice;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by jacobsu on 4/27/16.
 */
@Singleton
public class DataManager {

    private BLEDataServer mBLEServer;

    @Inject
    public DataManager(BLEDataServer bleServer) {
        mBLEServer = bleServer;
    }

    public List<BLEDataServer.BLEData> getRemoteBLEDatas() {
        return mBLEServer.getRemoteBLEDatas();
    }

    public Observable<BluetoothDevice> scanBLEPeripheral(boolean enabled) {
        return mBLEServer.scanBLEPeripheral(enabled);
    }

    public Observable<BLEDataServer.BLEData> connectGatt(BluetoothDevice device) {
        return mBLEServer.connect(device);
    }

    public boolean readRemoteRssi(BluetoothDevice device) {
        return mBLEServer.readRemoteRssi(device);
    }
}
