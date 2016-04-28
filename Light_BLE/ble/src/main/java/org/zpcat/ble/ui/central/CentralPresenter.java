package org.zpcat.ble.ui.central;

import android.bluetooth.BluetoothDevice;

import org.zpcat.ble.data.BLEDataServer;
import org.zpcat.ble.data.DataManager;
import org.zpcat.ble.ui.base.BasePresenter;

import java.util.ArrayList;
import java.util.List;

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
    private List<Subscription> mConnectSubsciptions;

    @Inject
    public CentralPresenter(DataManager dataManager) {
        mDataManager = dataManager;
        mConnectSubsciptions = new ArrayList<>();
    }

    @Override
    public void attachView(CentralMvpView centralView) {
        super.attachView(centralView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mScanSubscription != null) {
            mScanSubscription.unsubscribe();
        }

        for (Subscription s : mConnectSubsciptions) {
            s.unsubscribe();
        }

        mConnectSubsciptions.clear();
    }

    public void scanBLEPeripheral(boolean enabled) {
        checkViewAttached();

        if (mScanSubscription != null) {
            mScanSubscription.unsubscribe();
        }

        mScanSubscription = mDataManager.scanBLEPeripheral(enabled)
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
                });
    }

    public void connectGatt(BluetoothDevice device) {
        checkViewAttached();

        Subscription s = mDataManager.connectGatt(device)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<BLEDataServer.BLEData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(BLEDataServer.BLEData bleData) {
                        getMvpView().showBLEData(bleData);
                    }
                });

        mConnectSubsciptions.add(s);
    }
}
