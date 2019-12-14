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
import io.eberlein.insane.bluepwn.object.ILocation;


public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder>{
    private List<ILocation> ILocations;

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(View v, int p);
    }

    public LocationAdapter() {
        ILocations = new ArrayList<>();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.locationLatitudeLabel) TextView locationLatitudeLabel;
        @BindView(R.id.locationLongitudeLabel) TextView locationLongitudeLabel;
        @BindView(R.id.locationAccuracyLabel) TextView locationAccuracyLabel;

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
    public LocationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater i = LayoutInflater.from(parent.getContext());
        return new ViewHolder(parent.getContext(), i.inflate(R.layout.viewholder_locations_item, parent, false));
    }

    public void onBindViewHolder(LocationAdapter.ViewHolder holder, int position) {
        ILocation l = ILocations.get(position);
        holder.locationLatitudeLabel.setText(String.valueOf(l.getLatitude()));
        holder.locationLongitudeLabel.setText(String.valueOf(l.getLongitude()));
        holder.locationAccuracyLabel.setText(String.valueOf(l.getAccuracy()));
    }

    @Override
    public int getItemCount() {
        return ILocations.size();
    }

    public void addAll(List<ILocation> ILocations) {
        this.ILocations = ILocations;
        notifyDataSetChanged();
    }

    public void add(ILocation ILocation) {
        for (ILocation l : ILocations) {
            if (l.getUuid().equals(ILocation.getUuid())) {
                ILocations.set(ILocations.indexOf(l), ILocation);
                notifyItemChanged(ILocations.indexOf(ILocation));
                return;
            }
        }
        ILocations.add(ILocation);
        notifyItemChanged(ILocations.indexOf(ILocation));
    }

    public ILocation get(int index) {
        return ILocations.get(index);
    }

    public List<ILocation> get() {
        return ILocations;
    }

    public void empty() {
        ILocations = new ArrayList<>();
    }
}
