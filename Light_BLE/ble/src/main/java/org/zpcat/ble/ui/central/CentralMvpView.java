package org.zpcat.ble.ui.central;

import android.bluetooth.BluetoothDevice;

import org.zpcat.ble.ui.base.MvpView;

/**
 * Created by jacobsu on 4/27/16.
 */
public interface CentralMvpView extends MvpView {
    void showBLEDevice(BluetoothDevice bt);
}
