package org.zpcat.ble.injector.module;

import android.support.v4.app.Fragment;

import org.zpcat.ble.data.DataManager;
import org.zpcat.ble.ui.central.CentralPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by jacobsu on 4/27/16.
 */
@Module
public class FragmentModule {

    private Fragment mFragment;

    public FragmentModule(Fragment fragment) {
        mFragment = fragment;
    }

    @Provides
    public CentralPresenter provideCentralPresenter(DataManager dataManager) {
        return new CentralPresenter(dataManager);
    }
}
