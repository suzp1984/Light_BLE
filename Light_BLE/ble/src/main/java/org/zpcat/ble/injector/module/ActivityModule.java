package org.zpcat.ble.injector.module;

import android.app.Activity;

import org.zpcat.ble.data.DataManager;
import org.zpcat.ble.ui.main.MainPresenter;
import org.zpcat.ble.ui.peripheral.PeripheralPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by jacobsu on 4/27/16.
 */
@Module
public class ActivityModule {

    private Activity mActivity;

    public ActivityModule(Activity activity) {
        mActivity = activity;
    }

    @Provides
    public MainPresenter provideMainPresenter() {
        return new MainPresenter();
    }

    @Provides
    public PeripheralPresenter providePeripheralPresenter(DataManager dataManager) {
        return new PeripheralPresenter(dataManager);
    }
}
