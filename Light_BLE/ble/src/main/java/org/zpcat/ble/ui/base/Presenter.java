package org.zpcat.ble.ui.base;

/**
 * Created by jacobsu on 4/27/16.
 */
public interface Presenter<V extends MvpView> {
    void attachView(V mvpView);

    void detachView();
}
