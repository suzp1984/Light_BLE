package org.zpcat.ble.data;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;


import javax.inject.Inject;

import rx.Observable;

/**
 * Created by jacobsu on 4/27/16.
 */
public class BLEDataServer {

    private BluetoothLeScanner mLeScanner;

    @Inject
    public BLEDataServer(BluetoothLeScanner leScanner) {
        mLeScanner = leScanner;
    }

    Observable<BluetoothDevice> scanBLEPeripheral(boolean enabled) {
        return null;
    }
}
