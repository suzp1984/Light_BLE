package org.zpcat.ble.fragment;

import org.zpcat.ble.BLEApplication;
import org.zpcat.ble.DeviceControlActivity;
import org.zpcat.ble.R;
import org.zpcat.ble.adapter.LeDeviceAdapter;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by moses on 8/25/15.
 */
public class DeviceScanFragment extends Fragment {

    @Bind(R.id.recycler)
    RecyclerView mRecyclerView;
    
    @Bind(R.id.swip_refresh_layout)
    SwipeRefreshLayout mRefreshLayout;

    private LeDeviceAdapter mLeDeviceAdapter;
    private boolean mScanning;

    @Inject
    BluetoothLeScanner mLeScanner;

    private ScanCallback mBleScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, final ScanResult result) {
            super.onScanResult(callbackType, result);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLeDeviceAdapter.addDevice(result.getDevice());
                    mLeDeviceAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
        }

        @Override
        public void onScanFailed(int errorCode) {
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        BLEApplication bleApplication = (BLEApplication) getActivity().getApplication();
        bleApplication.getApplicationComponent().inject(this);

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
                Intent intent = new Intent(getActivity(), DeviceControlActivity.class);

                intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device.getName());
                intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());

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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        scanLeDevice(true);
    }

   @Override
   public void onPause() {
       super.onPause();

       scanLeDevice(false);
   }


    private void scanLeDevice(final boolean enable) {

        if (enable) {
            mScanning = true;

            mLeScanner.startScan(mBleScanCallback);
        } else {
            mScanning = false;

            mLeScanner.stopScan(mBleScanCallback);
        }
    }

    private void onRefreshSwipLayout() {
        scanLeDevice(true);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(false);
            }
        }, 2000);
    }
}
