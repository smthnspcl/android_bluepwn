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
import io.eberlein.insane.bluepwn.object.OuiEntry;

public class OuiAdapter extends RecyclerView.Adapter<OuiAdapter.ViewHolder>{
    private List<OuiEntry> ouiEntries;

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(View v, int p);
    }

    public OuiAdapter() {
        ouiEntries = new ArrayList<>();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.assignment) TextView assignment;
        @BindView(R.id.companyName) TextView companyName;

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
    public OuiAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater i = LayoutInflater.from(parent.getContext());
        return new ViewHolder(parent.getContext(), i.inflate(R.layout.viewholder_oui_item, parent, false));
    }

    public void onBindViewHolder(OuiAdapter.ViewHolder holder, int position) {
        OuiEntry o = ouiEntries.get(position);
        holder.assignment.setText(o.getAssignment());
        holder.companyName.setText(o.getOrganizationname());
    }

    @Override
    public int getItemCount() {
        return ouiEntries.size();
    }

    public void addAll(List<OuiEntry> scans) {
        this.ouiEntries = scans;
    }

    public void add(OuiEntry e) {
        for(OuiEntry o : ouiEntries){
            if (o.getAssignment().equals(e.getAssignment())) {
                ouiEntries.set(ouiEntries.indexOf(o), e);
                notifyItemChanged(ouiEntries.indexOf(e));
                return;
            }
        }
        ouiEntries.add(e);
        notifyItemChanged(ouiEntries.indexOf(e));
    }

    public OuiEntry get(int index) {
        return ouiEntries.get(index);
    }

    public List<OuiEntry> get() {
        return ouiEntries;
    }

    public void empty() {
        ouiEntries = new ArrayList<>();
    }
}
