package org.zpcat.ble.injector.module;

import android.app.Application;
import android.bluetooth.BluetoothManager;
import android.content.Context;

import org.zpcat.ble.injector.ApplicationContext;

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
}
