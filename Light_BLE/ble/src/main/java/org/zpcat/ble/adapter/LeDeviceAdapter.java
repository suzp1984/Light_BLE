package org.zpcat.ble.adapter;

import org.zpcat.ble.R;

import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by moses on 8/24/15.
 */
public class LeDeviceAdapter extends RecyclerView.Adapter<LeDeviceAdapter.DeviceViewHolder> {

    private final List<BluetoothDevice> mLeDevices = new ArrayList<>();

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

        holder.deviceName.setText(bt.getName());
        holder.deviceAddress.setText(bt.getAddress());

        holder.deviceCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClicked(mLeDevices.get(position), position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mLeDevices.size();
    }

    public void addDevice(BluetoothDevice device) {
        if (device != null && !mLeDevices.contains(device)) {
            mLeDevices.add(device);
        }
    }

    public void setListener(DeviceItemClickListener listener) {
        mListener = listener;
    }

    public class DeviceViewHolder extends RecyclerView.ViewHolder {

        TextView deviceName;
        TextView deviceAddress;
        CardView deviceCard;

        public DeviceViewHolder(View itemView) {
            super(itemView);

            deviceCard = (CardView) itemView.findViewById(R.id.device_card);
            deviceName = (TextView) itemView.findViewById(R.id.device_name);
            deviceAddress = (TextView) itemView.findViewById(R.id.device_address);
        }
    }

    public interface DeviceItemClickListener {
        void onItemClicked(BluetoothDevice device, int position);
    }
}
