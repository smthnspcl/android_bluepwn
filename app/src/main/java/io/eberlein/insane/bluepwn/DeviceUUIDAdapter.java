package io.eberlein.insane.bluepwn;


import android.content.Context;
import android.os.ParcelUuid;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;


public class DeviceUUIDAdapter extends RecyclerView.Adapter<DeviceUUIDAdapter.ViewHolder> {
    private List<ParcelUuid> uuids;

    private DeviceUUIDAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(View v, int p);
    }

    void setOnItemClickListener(DeviceUUIDAdapter.OnItemClickListener listener){
        this.listener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        // @BindView(R.id.dev) TextView uuid;
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

    DeviceUUIDAdapter(){
        uuids = new ArrayList<>();
    }

    @Override
    public DeviceUUIDAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater i = LayoutInflater.from(parent.getContext());
        return new DeviceUUIDAdapter.ViewHolder(parent.getContext(), i.inflate(R.layout.viewholder_devices_item, parent, false));
    }

    public void onBindViewHolder(DeviceUUIDAdapter.ViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return uuids.size();
    }

    void populate(List<ParcelUuid> uuids){ this.uuids.addAll(uuids); }

    void add(ParcelUuid uuid){
        for(ParcelUuid u : uuids){
            if(u.equals(uuid)){
                uuids.set(uuids.indexOf(u), uuid);
                notifyItemChanged(uuids.indexOf(uuid));
                return;
            }
        }
        uuids.add(uuid);
        notifyItemChanged(uuids.indexOf(uuid));
    }

    ParcelUuid get(int index){
        return uuids.get(index);
    }

    List<ParcelUuid> get(){return uuids;}

    void empty(){
        uuids = new ArrayList<>();
    }
}