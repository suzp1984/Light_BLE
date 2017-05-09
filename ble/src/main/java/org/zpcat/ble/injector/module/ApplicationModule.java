package org.zpcat.ble.injector.module;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.zpcat.ble.data.BLEDataServer;
import org.zpcat.ble.data.BLEPeripheralServer;
import org.zpcat.ble.data.DataManager;
import org.zpcat.ble.injector.ApplicationContext;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by jacobsu on 4/20/16.
 */
@Module
public class ApplicationModule {
    private Application mApplication;

    public ApplicationModule(Application application) {
        mApplication = application;
    }

    @Provides
    public Application provideApplication() {
        return mApplication;
    }

    @Provides
    @ApplicationContext
    public Context provideContext() {
        return mApplication;
    }

    @Provides
    public BluetoothManager provideBluetoothManager() {
        return (BluetoothManager) mApplication.getSystemService(Context.BLUETOOTH_SERVICE);
    }

    @Provides
    public BluetoothAdapter provideBluetoothAdapter(@NonNull BluetoothManager btmanager) {
        return btmanager.getAdapter();
    }

    @Provides
    public BluetoothLeScanner provideBluetoothLeScanner(@NonNull BluetoothAdapter btAdapter) {
        return btAdapter.getBluetoothLeScanner();
    }

    @Provides
    @Singleton
    public AdvertiseSettings.Builder provideAdvertiseSettingsBuilder() {
        AdvertiseSettings.Builder builder = new AdvertiseSettings.Builder();
        builder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY);
        builder.setConnectable(true);
        builder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH);

        return builder;
    }

    @Provides
    @Singleton
    public AdvertiseData.Builder provideAdvertiseDataBuilder() {
        AdvertiseData.Builder builder = new AdvertiseData.Builder();
        builder.setIncludeDeviceName(true);
        builder.setIncludeTxPowerLevel(true);

        return builder;
    }

  /*  @Provides
    @Singleton
    public BLEPeripheralServer provideBLEPeripheralServer(@ApplicationContext Context context) {
        return new BLEPeripheralServer(context);
    }
*/
    /*@Singleton
    @Provides
    public DataManager provideDataManager(BLEDataServer bleDataServer) {
        return new DataManager(bleDataServer);
    }*/
}
