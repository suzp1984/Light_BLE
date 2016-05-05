package org.zpcat.ble.ui.central;

import org.zpcat.ble.R;
import org.zpcat.ble.adapter.LeDeviceAdapter;
import org.zpcat.ble.data.BLEDataServer;
import org.zpcat.ble.ui.base.BaseFragment;
import org.zpcat.ble.ui.base.BasePresenter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CentralScanFragment extends BaseFragment implements CentralMvpView {

    private final int BLE_SCAN_PEROID = 10000;

    @Bind(R.id.recycler)
    RecyclerView mRecyclerView;
    
    @Bind(R.id.swip_refresh_layout)
    SwipeRefreshLayout mRefreshLayout;

    private LeDeviceAdapter mLeDeviceAdapter;

    @Inject
    CentralPresenter mCentralPresenter;

    private List<BluetoothDevice> mRemoteDevices = new ArrayList<>();

    private static final int READ_RSSI_REPEAT = 1;
    private final long READING_RSSI_TASK_FREQENCY = 2000;

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case READ_RSSI_REPEAT:
                    synchronized (CentralScanFragment.this) {
                        for (BluetoothDevice device : mRemoteDevices) {
                            mCentralPresenter.readRemoteRssi(device);
                        }
                    }

                    sendMessageDelayed(obtainMessage(READ_RSSI_REPEAT),
                            READING_RSSI_TASK_FREQENCY);
                    break;
            }
        }
    };

    private void startReadRssi() {
        if (mHandler.hasMessages(READ_RSSI_REPEAT)) {
            return;
        }

        mHandler.sendEmptyMessage(READ_RSSI_REPEAT);
    }

    private void stopReadRssi() {
        mHandler.removeMessages(READ_RSSI_REPEAT);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        org.zpcat.ble.utils.Log.d("onAttach");
        getFragmentComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        org.zpcat.ble.utils.Log.d("onCreateView");
        View view = inflater.inflate(R.layout.device_scan_fragment, container, false);
        ButterKnife.bind(this, view);

        mLeDeviceAdapter = new LeDeviceAdapter();
        mRecyclerView.setAdapter(mLeDeviceAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mLeDeviceAdapter.setListener(new LeDeviceAdapter.DeviceItemClickListener() {

            @Override
            public void onItemClicked(BluetoothDevice device, int position) {
                Intent intent = new Intent(getActivity(), CentralDetailsActivity.class);

                intent.putExtra(CentralDetailsActivity.EXTRAS_DEVICE_NAME, device.getName());
                intent.putExtra(CentralDetailsActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());

                startActivity(intent);
            }
        });

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onRefreshSwipLayout();
            }
        });

        mRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        org.zpcat.ble.utils.Log.d("onStart");
        mCentralPresenter.attachView(this);
        scanLeDevice(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        org.zpcat.ble.utils.Log.d("onResume");
        startReadRssi();
    }

    @Override
    public void onPause() {
        super.onPause();
        org.zpcat.ble.utils.Log.d("onPause");
        scanLeDevice(false);
        stopReadRssi();
    }

    @Override
    public void onStop() {
        super.onStop();
        org.zpcat.ble.utils.Log.d("onStop");
        mLeDeviceAdapter.clearDevices();
        mLeDeviceAdapter.notifyDataSetChanged();
        mCentralPresenter.detachView();
    }

    @Override
    public void showBLEDevice(BluetoothDevice bt) {
        // maybe running in UI thread
        mLeDeviceAdapter.addDevice(bt);
        mLeDeviceAdapter.notifyDataSetChanged();

        mCentralPresenter.connectGatt(bt);
    }

    @Override
    public void showBLEData(BLEDataServer.BLEData data) {
        mLeDeviceAdapter.showBLEData(data);
        mLeDeviceAdapter.notifyDataSetChanged();

        // if bluetooth was connected, then read remote rssi;
        if (data.connectedState) {
            synchronized (this) {
                if (!mRemoteDevices.contains(data.device)) {
                    mRemoteDevices.add(data.device);
                }
            }
        }
    }

    private void scanLeDevice(final boolean enable) {

        try {
            mCentralPresenter.scanBLEPeripheral(enable);
        } catch (BasePresenter.MvpViewNotAttachedException e) {
            Log.e(CentralScanFragment.class.getName(), e.toString());
            return;
        }

        if (enable) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mRefreshLayout.setRefreshing(false);
                    scanLeDevice(false);
                }
            }, BLE_SCAN_PEROID);
        }
    }

    private void onRefreshSwipLayout() {
        scanLeDevice(true);
    }
}
