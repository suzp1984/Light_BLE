package org.zpcat.ble.fragment;

import org.w3c.dom.Text;
import org.zpcat.ble.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by moses on 8/25/15.
 */
public class DeviceControlFragment extends Fragment {

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    @Bind(R.id.connection_state)
    TextView mConnectionState;

    @Bind(R.id.data_value)
    TextView mDataField;

    @Bind(R.id.signal_rssi)
    TextView mRssiField;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gatt_services_characteristics, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);

        super.onDestroyView();
    }
}
