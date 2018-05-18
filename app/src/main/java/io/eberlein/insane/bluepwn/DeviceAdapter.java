package io.eberlein.insane.bluepwn;

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


public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder>{
    private List<Device> devices;

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(View v, int p);
    }

    void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
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

    DeviceAdapter(){
        devices = new ArrayList<>();
    }

    @Override
    public DeviceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater i = LayoutInflater.from(parent.getContext());
        return new ViewHolder(parent.getContext(), i.inflate(R.layout.viewholder_devices_item, parent, false));
    }

    public void onBindViewHolder(DeviceAdapter.ViewHolder holder, int position) {
        Device d = devices.get(position);
        holder.tvMac.setText(d.address);
        holder.tvName.setText(d.name);
        // holder.tvUuids.setText(String.valueOf(d.uuids.size()));
        holder.tvManufacturer.setText(d.manufacturer);
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    void add(Device device){
        if(!devices.contains(device)){
            devices.add(device);
            notifyDataSetChanged();
        }
    }

    void addAll(List<Device> devices){
        for(Device d : devices){
            if(!this.devices.contains(d)) add(d);
        }
        notifyDataSetChanged();
    }


    Device get(int index){
        return devices.get(index);
    }

    List<Device> get(){return devices;}

    void empty(){
        devices = new ArrayList<>();
    }
}
