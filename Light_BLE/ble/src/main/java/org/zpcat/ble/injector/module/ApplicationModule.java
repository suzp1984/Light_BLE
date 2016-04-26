package org.zpcat.ble.injector.module;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.support.annotation.NonNull;

import org.zpcat.ble.injector.ApplicationContext;

import javax.inject.Inject;

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
}
