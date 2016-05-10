package org.zpcat.ble.ui.peripheral;

import org.zpcat.ble.ui.base.MvpView;

/**
 * Created by jacobsu on 5/10/16.
 */
public interface PeripheralMvpView extends MvpView {
    void showSupportLEAdvertiser(boolean support);
}
