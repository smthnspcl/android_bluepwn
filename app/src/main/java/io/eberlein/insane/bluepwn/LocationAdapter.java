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


public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder>{
    private List<Location> locations;

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(View v, int p);
    }

    void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
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

    LocationAdapter(){
        locations = new ArrayList<>();
    }

    @Override
    public LocationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater i = LayoutInflater.from(parent.getContext());
        return new ViewHolder(parent.getContext(), i.inflate(R.layout.viewholder_locations_item, parent, false));
    }

    public void onBindViewHolder(LocationAdapter.ViewHolder holder, int position) {
        Location l = locations.get(position);
        holder.locationLatitudeLabel.setText(String.valueOf(l.latitude));
        holder.locationLongitudeLabel.setText(String.valueOf(l.longitude));
        holder.locationAccuracyLabel.setText(String.valueOf(l.accuracy));
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    void addAll(List<Location> locations){
        this.locations = locations;
        notifyDataSetChanged();
    }

    void add(Location location){
        for(Location l : locations){
            if(l.uuid.equals(location.uuid)){
                locations.set(locations.indexOf(l), location);
                notifyItemChanged(locations.indexOf(location));
                return;
            }
        }
        locations.add(location);
        notifyItemChanged(locations.indexOf(location));
    }

    Location get(int index){
        return locations.get(index);
    }

    List<Location> get(){return locations;}

    void empty(){
        locations = new ArrayList<>();
    }
}
