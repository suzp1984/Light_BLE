package org.zpcat.ble.ui.main;

import org.zpcat.ble.ui.base.MvpView;

/**
 * Created by jacobsu on 4/27/16.
 */
public interface MainMvpView extends MvpView {
    void startAboutLibrary();
    void startCentralMode();
    void startPeripheralMode();
}
