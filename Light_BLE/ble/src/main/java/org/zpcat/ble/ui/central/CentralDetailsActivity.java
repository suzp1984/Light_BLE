package org.zpcat.ble.ui.central;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import org.zpcat.ble.R;
import org.zpcat.ble.SampleGattAttributes;
import org.zpcat.ble.data.BLEDataServer;
import org.zpcat.ble.ui.base.BaseActivity;
import org.zpcat.ble.utils.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by jacobsu on 4/30/16.
 */
public class CentralDetailsActivity extends BaseActivity implements CentralMvpView {

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    private String mDeviceName;
    private String mDeviceAddress;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private BluetoothGattCharacteristic mWriteCharacteristic;

    @Bind(R.id.device_address)
    TextView mDeviceAddressTv;

    @Bind(R.id.gatt_services_list)
    ExpandableListView mGattServicesList;

    @Bind(R.id.connection_state)
    TextView mConnectionState;

    @Bind(R.id.data_value)
    TextView mDataField;

    @Bind(R.id.signal_rssi)
    TextView mRssiField;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Inject
    CentralPresenter mCentralPresenter;

    @Inject
    BluetoothAdapter mBtAdapter;

    // If a given GATT characteristic is selected, check for supported features.  This sample
    // demonstrates 'Read' and 'Notify' features.  See
    // http://d.android.com/reference/android/bluetooth/BluetoothGatt.html for the complete
    // list of supported characteristic features.
    private final ExpandableListView.OnChildClickListener mServicesListClickListener =
            new ExpandableListView.OnChildClickListener() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                            int childPosition, long id) {
                    if (mGattCharacteristics != null) {
                        final BluetoothGattCharacteristic characteristic =
                                mGattCharacteristics.get(groupPosition).get(childPosition);
                        final int charaProp = characteristic.getProperties();
                        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                            // If there is an active notification on a characteristic, clear
                            // it first so it doesn't update the data field on the user interface.
                            Log.d("BluetoothGattCharacteristic has PROPERTY_READ, so send read request");

                            /*if (mNotifyCharacteristic != null) {
                                mBluetoothLeService.setCharacteristicNotification(
                                        mNotifyCharacteristic, false);
                                mNotifyCharacteristic = null;
                            }
                            mBluetoothLeService.readCharacteristic(characteristic);*/
                        }

                        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                            Log.d("BluetoothGattCharacteristic has PROPERTY_NOTIFY, so send notify request");

                           /* mNotifyCharacteristic = characteristic;
                            mBluetoothLeService.setCharacteristicNotification(
                                    characteristic, true);*/
                        }

                        if (((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE) |
                                (charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)) > 0) {
                            Log.d("BluetoothGattCharacteristic has PROPERY_WRITE | PROPERTY_WRITE_NO_RESPONSE");

                            mWriteCharacteristic = characteristic;
                            // popup an dialog to write something.
                            /*showCharactWriteDialog();*/
                        }

                        return true;
                    }
                    return false;
                }
            };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        setContentView(R.layout.central_gatt_services);

        ButterKnife.bind(this);

        if (mToolbar != null) {
            mToolbar.setTitle("Gatt Services");
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        // Sets up UI references.
        if (mDeviceAddress != null) {
            mDeviceAddressTv.setText(mDeviceAddress);
        }

        mGattServicesList.setOnChildClickListener(mServicesListClickListener);
        mConnectionState = (TextView) findViewById(R.id.connection_state);
        mDataField = (TextView) findViewById(R.id.data_value);
        mRssiField = (TextView) findViewById(R.id.signal_rssi);

    }

    @Override
    public void onStart() {
        super.onStart();

        mCentralPresenter.attachView(this);

        mCentralPresenter.connectGatt(mBtAdapter.getRemoteDevice(mDeviceAddress));
    }

    @Override
    public void onStop() {
        super.onStop();

        mCentralPresenter.detachView();
    }

    @Override
    public void showBLEDevice(BluetoothDevice bt) {
        // do nothing here
    }

    @Override
    public void showBLEData(final BLEDataServer.BLEData data) {
        if (Objects.equals(data.device.getAddress(), mDeviceAddress)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mRssiField.setText(" " + data.rssi);
                    mConnectionState.setText(data.connectedState ? "Connected" : "Disconnected");
                    mDataField.setText(data.data);
                    displayGattServices(data.services);
                }
            });
        }
    }

    private void displayGattServices(List<BluetoothGattService> gattServices) {
        Log.d("displayGATTServices");

        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(
                    LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(
                        LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }

        SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(
                this,
                gattServiceData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 },
                gattCharacteristicData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 }
        );
        mGattServicesList.setAdapter(gattServiceAdapter);
    }
}
