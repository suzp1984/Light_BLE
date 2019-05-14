package org.zpcat.ble.adapter;

import org.w3c.dom.Text;
import org.zpcat.ble.R;
import org.zpcat.ble.data.BLEDataServer;

import android.bluetooth.BluetoothDevice;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by moses on 8/24/15.
 */
public class LeDeviceAdapter extends RecyclerView.Adapter<LeDeviceAdapter.DeviceViewHolder> {

    private final List<BluetoothDevice> mLeDevices = new ArrayList<>();
    private final Map<BluetoothDevice, BLEDataServer.BLEData> mBLEDataMap = new HashMap<>();

    private DeviceItemClickListener mListener;

    @Override
    public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_device, parent, false);

        DeviceViewHolder viewHolder = new DeviceViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DeviceViewHolder holder, final int position) {
        BluetoothDevice bt = mLeDevices.get(position);
        BLEDataServer.BLEData data = mBLEDataMap.get(bt);
        Log.d("LeDeviceAdapter", "position(" + position + "), " + bt.toString());

        holder.deviceName.setText("Name: " + bt.getName());
        holder.deviceAddress.setText("Address: " + bt.getAddress());

        if (data != null) {
            if (data.connectedState) {
                holder.deviceCard.setBackgroundResource(R.color.colorPrimary);
                holder.deviceState.setText("State: Connected");
            } else {
                holder.deviceCard.setBackgroundResource(R.color.disconnect);
                holder.deviceState.setText("State: Disconnected");
            }

            holder.deviceRSSI.setText("Rssi: " + data.rssi);
        }

        holder.deviceCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null && mLeDevices.size() > position) {
                    mListener.onItemClicked(mLeDevices.get(position), position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mLeDevices.size();
    }

    public BluetoothDevice getBtDeviceAtIndex(int index) {
        if (index < mLeDevices.size()) {
            return mLeDevices.get(index);
        }

        return null;
    }

    public void addDevice(BluetoothDevice device) {
        if (device != null && !mLeDevices.contains(device)) {
            mLeDevices.add(device);
        }

    }

    public void clearDevices() {
        mLeDevices.clear();
    }

    public void showBLEData(BLEDataServer.BLEData data) {
        mBLEDataMap.put(data.device, data);
    }

    public void setListener(DeviceItemClickListener listener) {
        mListener = listener;
    }

    public class DeviceViewHolder extends RecyclerView.ViewHolder {

        TextView deviceName;
        TextView deviceAddress;
        TextView deviceState;
        TextView deviceRSSI;
        CardView deviceCard;

        public DeviceViewHolder(View itemView) {
            super(itemView);

            deviceCard = (CardView) itemView.findViewById(R.id.device_card);
            deviceName = (TextView) itemView.findViewById(R.id.device_name);
            deviceAddress = (TextView) itemView.findViewById(R.id.device_address);
            deviceState = (TextView) itemView.findViewById(R.id.connection_state);
            deviceRSSI = (TextView) itemView.findViewById(R.id.rssi);
        }
    }

    public interface DeviceItemClickListener {
        void onItemClicked(BluetoothDevice device, int position);
    }
}
