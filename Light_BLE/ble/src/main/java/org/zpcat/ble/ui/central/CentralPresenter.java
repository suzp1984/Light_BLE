package org.zpcat.ble.ui.central;

import android.bluetooth.BluetoothDevice;

import org.zpcat.ble.data.DataManager;
import org.zpcat.ble.ui.base.BasePresenter;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by jacobsu on 4/27/16.
 */
public class CentralPresenter extends BasePresenter<CentralMvpView> {

    private DataManager mDataManager;
    private Subscription mScanSubscription;

    @Inject
    public CentralPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(CentralMvpView centralView) {
        super.attachView(centralView);
    }

    @Override
    public void detachView() {
        super.detachView();
    }

    public void scanBLEPeripheral(boolean enabled) {
        checkViewAttached();

        /*mScanSubscription = mDataManager.scanBLEPeripheral(enabled)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<BluetoothDevice>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(BluetoothDevice bluetoothDevice) {
                        getMvpView().showBLEDevice(bluetoothDevice);
                    }
                });*/

    }
}
