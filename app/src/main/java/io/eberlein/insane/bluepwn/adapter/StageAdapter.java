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
import io.eberlein.insane.bluepwn.object.Stage;

public class StageAdapter extends RecyclerView.Adapter<StageAdapter.ViewHolder>{
    private List<Stage> stages;

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(View v, int p);
    }

    public StageAdapter() {
        stages = new ArrayList<>();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name) TextView name;

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

    public StageAdapter(List<Stage> stages) {
        this.stages = stages;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public StageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater i = LayoutInflater.from(parent.getContext());
        return new ViewHolder(parent.getContext(), i.inflate(R.layout.viewholder_stagers_item, parent, false));
    }

    public void onBindViewHolder(StageAdapter.ViewHolder holder, int position) {
        Stage s = stages.get(position);
        holder.name.setText(s.getName());
    }

    @Override
    public int getItemCount() {
        return stages.size();
    }

    public void add(Stage stage) {
        for(Stage s : stages){
            if (s.getUuid().equals(stage.getUuid())) {
                stages.set(stages.indexOf(s), stage);
                notifyItemChanged(stages.indexOf(stage));
                return;
            }
        }
        stages.add(stage);
        notifyItemChanged(stages.indexOf(stage));
    }

    public void addAll(List<Stage> stages) {
        for(Stage s : stages) add(s);
    }

    public void empty() {
        stages = new ArrayList<>();
    }

    public Stage get(int index) {
        return stages.get(index);
    }
}

