package org.zpcat.ble.injector.component;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;

import org.zpcat.ble.BluetoothLeService;
import org.zpcat.ble.data.BLEDataServer;
import org.zpcat.ble.data.DataManager;
import org.zpcat.ble.injector.module.ApplicationModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by jacobsu on 4/20/16.
 */
@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(BluetoothLeService service);

    BluetoothManager bluetoothManager();
    BluetoothAdapter bluetoothAdapter();
    BluetoothLeScanner bluetoothLeScanner();
    DataManager dataManager();
    BLEDataServer bleDataServer();
}
