package org.zpcat.ble.ui.central;

import android.bluetooth.BluetoothDevice;

import org.zpcat.ble.data.BLEDataServer;
import org.zpcat.ble.data.DataManager;
import org.zpcat.ble.ui.base.BasePresenter;
import org.zpcat.ble.utils.Log;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by jacobsu on 4/27/16.
 */
public class CentralPresenter extends BasePresenter<CentralMvpView> {

    private DataManager mDataManager;

    private Disposable   mScanDisposable;
    private final List<Disposable>  mConnectedDisposable;

    @Inject
    public CentralPresenter(DataManager dataManager) {
        mDataManager = dataManager;
        mConnectedDisposable = new ArrayList<>();
    }

    @Override
    public void attachView(CentralMvpView centralView) {
        super.attachView(centralView);

        // send all deivces to view here;
        //List<BluetoothDevice> devices = mDataManager.getRemoteDevices();
        List<BLEDataServer.BLEData> datas = mDataManager.getRemoteBLEDatas();

        for(BLEDataServer.BLEData data: datas) {
            getMvpView().showBLEData(data);
        }
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mScanDisposable != null && !mScanDisposable.isDisposed()) {
            mScanDisposable.dispose();
        }

        for (Disposable s : mConnectedDisposable) {
            if (s.isDisposed()) {
                s.dispose();
            }
        }

        mConnectedDisposable.clear();
    }

    public void getRemoteDevices() {

        List<BLEDataServer.BLEData> datas = mDataManager.getRemoteBLEDatas();

        for(BLEDataServer.BLEData data: datas) {
            getMvpView().showBLEDevice(data.device);
        }
    }

    public void scanBLEPeripheral(boolean enabled) {
        checkViewAttached();

        if (mScanDisposable != null && !mScanDisposable.isDisposed()) {
            mScanDisposable.dispose();
        }

        mScanDisposable = mDataManager.scanBLEPeripheral(enabled)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Consumer<BluetoothDevice>() {
                    @Override
                    public void accept(BluetoothDevice bluetoothDevice) throws Exception {
                        getMvpView().showBLEDevice(bluetoothDevice);
                    }
                });
    }

    public void connectGatt(BluetoothDevice device) {
        checkViewAttached();

        // debugs here! if connect same bluetoothDevice multi times

        Disposable s = mDataManager.connectGatt(device)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Consumer<BLEDataServer.BLEData>() {
                    @Override
                    public void accept(BLEDataServer.BLEData bleData) throws Exception {
                        if (isViewAttached()) {
                            getMvpView().showBLEData(bleData);
                        }
                    }
                });

        mConnectedDisposable.add(s);
    }

    public boolean readRemoteRssi(BluetoothDevice device) {
        return mDataManager.readRemoteRssi(device);
    }
}
