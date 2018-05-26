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


public class ParcelUuidAdapter extends RecyclerView.Adapter<ParcelUuidAdapter.ViewHolder> {
    private List<io.eberlein.insane.bluepwn.ParcelUuid> uuids;

    private ParcelUuidAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(View v, int p);
    }

    void setOnItemClickListener(ParcelUuidAdapter.OnItemClickListener listener){
        this.listener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.uuid) TextView uuid;
        @BindView(R.id.serviceName) TextView serviceName;
        // todo amount of actions

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

    ParcelUuidAdapter(){
        uuids = new ArrayList<>();
    }

    @Override
    public ParcelUuidAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater i = LayoutInflater.from(parent.getContext());
        return new ParcelUuidAdapter.ViewHolder(parent.getContext(), i.inflate(R.layout.viewholder_uuids_item, parent, false));
    }

    public void onBindViewHolder(ParcelUuidAdapter.ViewHolder holder, int position) {
        ParcelUuid uuid = uuids.get(position);
        holder.serviceName.setText(uuid.name);
        holder.uuid.setText(uuid.uuid.toString());
    }

    @Override
    public int getItemCount() {
        return uuids.size();
    }

    void addAll(List<io.eberlein.insane.bluepwn.ParcelUuid> uuids){
        this.uuids.addAll(uuids); }

    void add(io.eberlein.insane.bluepwn.ParcelUuid uuid){
        for(ParcelUuid u : uuids){
            if(u.id.equals(uuid.id)){
                uuids.set(uuids.indexOf(u), uuid);
                notifyItemChanged(uuids.indexOf(uuid));
                return;
            }
        }
        uuids.add(uuid);
        notifyItemChanged(uuids.indexOf(uuid));
    }

    io.eberlein.insane.bluepwn.ParcelUuid get(int index){
        return uuids.get(index);
    }

    List<io.eberlein.insane.bluepwn.ParcelUuid> get(){return uuids;}

    void empty(){
        uuids = new ArrayList<>();
    }
}