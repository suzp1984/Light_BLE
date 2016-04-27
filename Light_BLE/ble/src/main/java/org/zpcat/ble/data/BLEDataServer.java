package org.zpcat.ble.data;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by jacobsu on 4/27/16.
 */
public class BLEDataServer {

    private BluetoothLeScanner mLeScanner;

    private Subscriber<BluetoothDevice> mSubscriber;

    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            if (mSubscriber != null) {
                mSubscriber.onNext(result.getDevice());
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            if (mSubscriber != null) {
                for (ScanResult r : results) {
                    mSubscriber.onNext(r.getDevice());
                }
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            if (mSubscriber != null) {
                mSubscriber.onError(new Throwable("scan failed with errorCode: " + errorCode));
                mSubscriber.onCompleted();
            }
        }
    };

    @Inject
    public BLEDataServer(BluetoothLeScanner leScanner) {
        mLeScanner = leScanner;
    }

    Observable<BluetoothDevice> scanBLEPeripheral(final boolean enabled) {
        return Observable.create(new Observable.OnSubscribe<BluetoothDevice>() {
            @Override
            public void call(Subscriber<? super BluetoothDevice> subscriber) {
                if (enabled) {
                    mSubscriber = (Subscriber<BluetoothDevice>) subscriber;
                    subscriber.onStart();

                    mLeScanner.startScan(mScanCallback);
                } else {
                    subscriber.onCompleted();
                    mLeScanner.stopScan(mScanCallback);
                }
            }
        });
    }
}
