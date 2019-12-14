package io.eberlein.insane.bluepwn.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import io.eberlein.insane.bluepwn.R;
import io.eberlein.insane.bluepwn.object.Characteristic;


public class CharacteristicsAdapter extends RecyclerView.Adapter<CharacteristicsAdapter.ViewHolder>{
    private List<Characteristic> characteristics;

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(View v, int p);
    }

    public CharacteristicsAdapter() {
        characteristics = new ArrayList<>();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

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
    public CharacteristicsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater i = LayoutInflater.from(parent.getContext());
        return new ViewHolder(parent.getContext(), i.inflate(R.layout.viewholder_characteristics_item, parent, false));
    }

    public void onBindViewHolder(CharacteristicsAdapter.ViewHolder holder, int position) {
        Characteristic d = characteristics.get(position);
    }

    @Override
    public int getItemCount() {
        return characteristics.size();
    }

    public void addAll(List<Characteristic> characteristics) {
        this.characteristics = characteristics;
        notifyDataSetChanged();
    }

    public void add(Characteristic characteristic) {
        if (characteristic.getUuid().isEmpty()) return;
        for(Characteristic d : characteristics){
            if (d.getUuid().equals(characteristic.getUuid())) {
                characteristics.set(characteristics.indexOf(d), characteristic);
                notifyItemChanged(characteristics.indexOf(characteristic));
                return;
            }
        }
        characteristics.add(characteristic);
        notifyItemChanged(characteristics.indexOf(characteristic));
    }

    public Characteristic get(int index) {
        return characteristics.get(index);
    }

    public List<Characteristic> get() {
        return characteristics;
    }

    public void empty() {
        characteristics = new ArrayList<>();
    }
}
