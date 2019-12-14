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
import io.eberlein.insane.bluepwn.object.SettingsItem;

public class SettingsItemAdapter extends RecyclerView.Adapter<SettingsItemAdapter.ViewHolder> {
    private List<SettingsItem> settingsItems;

    private SettingsItemAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(View v, int p);
    }

    public SettingsItemAdapter() {
        settingsItems = new ArrayList<>();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title) TextView title;

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

    public void setOnItemClickListener(SettingsItemAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public SettingsItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater i = LayoutInflater.from(parent.getContext());
        return new SettingsItemAdapter.ViewHolder(parent.getContext(), i.inflate(R.layout.viewholder_settings_item, parent, false));
    }

    public void onBindViewHolder(SettingsItemAdapter.ViewHolder holder, int position) {
        SettingsItem settingsItem = settingsItems.get(position);
        holder.title.setText(settingsItem.getTitle());
    }

    @Override
    public int getItemCount() {
        return settingsItems.size();
    }

    public void addAll(List<SettingsItem> settingsItems) {
        this.settingsItems.addAll(settingsItems);
        notifyDataSetChanged();
    }

    public void add(SettingsItem item) {
        for(SettingsItem s : settingsItems){
            if (s.getTitle().equals(item.getTitle())) {
                settingsItems.set(settingsItems.indexOf(s), item);
                notifyItemChanged(settingsItems.indexOf(item));
                return;
            }
        }
        settingsItems.add(item);
        notifyItemChanged(settingsItems.indexOf(item));
    }

    public SettingsItem get(int index) {
        return settingsItems.get(index);
    }

    public List<SettingsItem> get() {
        return settingsItems;
    }

    public void empty() {
        settingsItems = new ArrayList<>();
    }
}