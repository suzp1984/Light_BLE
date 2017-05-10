package org.zpcat.ble.ui.peripheral;

import org.zpcat.ble.data.DataManager;
import org.zpcat.ble.ui.base.BasePresenter;

import javax.inject.Inject;

/**
 * Created by jacobsu on 5/10/16.
 */
public class PeripheralPresenter extends BasePresenter<PeripheralMvpView> {

    private DataManager mDataManager;

    @Inject
    public PeripheralPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    public void startLeAdvertise(String name) {
        if (mDataManager.supportAdvertiser()) {
            mDataManager.startPeripheralMode(name);
        } else {
            getMvpView().showSupportLEAdvertiser(false);
        }
    }

    public void stopLeAdvertise() {
        if (mDataManager.supportAdvertiser()) {
            mDataManager.stopPeripheralMode();
        }
    }
}
