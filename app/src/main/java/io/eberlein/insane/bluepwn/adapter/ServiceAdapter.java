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
import io.eberlein.insane.bluepwn.object.Service;


public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ViewHolder> {
    private List<Service> services;

    private ServiceAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(View v, int p);
    }

    public ServiceAdapter() {
        services = new ArrayList<>();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.service) TextView uuid;
        @BindView(R.id.name) TextView name;
        @BindView(R.id.stagers) TextView stagers;

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

    public void setOnItemClickListener(ServiceAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public ServiceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater i = LayoutInflater.from(parent.getContext());
        return new ServiceAdapter.ViewHolder(parent.getContext(), i.inflate(R.layout.viewholder_service_item, parent, false));
    }

    public void onBindViewHolder(ServiceAdapter.ViewHolder holder, int position) {
        Service service = services.get(position);
        holder.name.setText(service.getName());
        holder.uuid.setText(service.getUuid());
        holder.stagers.setText(String.valueOf(service.getStagers().size()));
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    public void addAll(List<Service> services) {
        this.services.addAll(services);
        notifyDataSetChanged();
    }

    public void add(Service service) {
        for(Service u : services){
            if (u.getUuid().equals(service.getUuid())) {
                services.set(services.indexOf(u), service);
                notifyItemChanged(services.indexOf(service));
                return;
            }
        }
        services.add(service);
        notifyItemChanged(services.indexOf(service));
    }

    public Service get(int index) {
        return services.get(index);
    }

    public List<Service> get() {
        return services;
    }

    public void empty() {
        services = new ArrayList<>();
    }
}