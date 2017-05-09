package org.zpcat.ble;

import android.app.Application;

import org.zpcat.ble.injector.component.ApplicationComponent;
import org.zpcat.ble.injector.component.DaggerApplicationComponent;
import org.zpcat.ble.injector.module.ApplicationModule;

/**
 * Created by jacobsu on 4/21/16.
 */
public class BLEApplication extends Application {

    private ApplicationComponent mApplicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

    }

    public ApplicationComponent getApplicationComponent() {
        if (mApplicationComponent == null) {
            mApplicationComponent = DaggerApplicationComponent.builder()
                    .applicationModule(new ApplicationModule(this))
                    .build();
        }

        return mApplicationComponent;
    }
}
