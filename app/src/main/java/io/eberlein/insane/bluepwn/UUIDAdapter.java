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


public class UUIDAdapter extends RecyclerView.Adapter<UUIDAdapter.ViewHolder> {
    private List<Service> services;

    private UUIDAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(View v, int p);
    }

    void setOnItemClickListener(UUIDAdapter.OnItemClickListener listener){
        this.listener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.service) TextView uuid;
        @BindView(R.id.name) TextView name;
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

    UUIDAdapter(){
        services = new ArrayList<>();
    }

    @Override
    public UUIDAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater i = LayoutInflater.from(parent.getContext());
        return new UUIDAdapter.ViewHolder(parent.getContext(), i.inflate(R.layout.viewholder_uuids_item, parent, false));
    }

    public void onBindViewHolder(UUIDAdapter.ViewHolder holder, int position) {
        Service service = services.get(position);
        holder.name.setText(service.name);
        holder.uuid.setText(service.uuid);
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    void addAll(List<Service> services){
        this.services.addAll(services); }

    void add(Service service){
        for(Service u : services){
            if(u.uuid.equals(service.uuid)){
                services.set(services.indexOf(u), service);
                notifyItemChanged(services.indexOf(service));
                return;
            }
        }
        services.add(service);
        notifyItemChanged(services.indexOf(service));
    }

    Service get(int index){
        return services.get(index);
    }

    List<Service> get(){return services;}

    void empty(){
        services = new ArrayList<>();
    }
}