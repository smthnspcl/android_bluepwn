package io.eberlein.insane.bluepwn.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.eberlein.insane.bluepwn.R;
import io.eberlein.insane.bluepwn.object.Device;


public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder>{
    private List<Device> devices;

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(View v, int p);
    }

    public DeviceAdapter() {
        devices = new ArrayList<>();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvMac) TextView tvMac;
        @BindView(R.id.tvUuids) TextView tvUuids;
        @BindView(R.id.tvName) TextView tvName;
        @BindView(R.id.tvManufacturer) TextView tvManufacturer;

        Context context;

        ViewHolder(Context context, View v){
            super(v);
            this.context = context;
            ButterKnife.bind(this, v);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int p = getAdapterPosition();
                        if(p != RecyclerView.NO_POSITION) {
                            listener.onItemClick(v, p);
                        }
                    }
                }
            });
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public DeviceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater i = LayoutInflater.from(parent.getContext());
        return new ViewHolder(parent.getContext(), i.inflate(R.layout.viewholder_devices_item, parent, false));
    }

    public void onBindViewHolder(DeviceAdapter.ViewHolder holder, int position) {
        Device d = devices.get(position);
        if(d == null) return;
        holder.tvMac.setText(d.getAddress());
        holder.tvName.setText(d.getName());
        holder.tvUuids.setText(String.valueOf(d.getServices().size()));
        holder.tvManufacturer.setText(d.getManufacturer());
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public void addAll(List<Device> devices) {
        this.devices = devices;
        notifyDataSetChanged();
    }

    public void add(Device device) {
        if (device.getAddress().isEmpty()) return;
        for(Device d : devices){
            if(d == null) continue;
            if (d.getAddress().equals(device.getAddress())) {
                devices.set(devices.indexOf(d), device);
                notifyItemChanged(devices.indexOf(device));
                return;
            }
        }
        devices.add(device);
        notifyItemChanged(devices.indexOf(device));
    }

    public Device get(int index) {
        return devices.get(index);
    }

    public List<Device> get() {
        return devices;
    }

    public void empty() {
        devices = new ArrayList<>();
    }
}
