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

public class ActionAdapter extends RecyclerView.Adapter<ActionAdapter.ViewHolder>{
    private List<Action> actions;

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(View v, int p);
    }

    void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.actionName) TextView actionName;
        @BindView(R.id.actionMacPrefix) TextView actionMacPrefix;

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

    ActionAdapter(){
        actions = new ArrayList<>();
    }

    ActionAdapter(List<Action> actions){
        this.actions = actions;
    }

    @Override
    public ActionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater i = LayoutInflater.from(parent.getContext());
        return new ViewHolder(parent.getContext(), i.inflate(R.layout.viewholder_actions_item, parent, false));
    }

    public void onBindViewHolder(ActionAdapter.ViewHolder holder, int position) {
        Action a = actions.get(position);
        holder.actionName.setText(a.name);
        holder.actionMacPrefix.setText(a.macPrefix);
    }

    @Override
    public int getItemCount() {
        return actions.size();
    }

    void add(Action action){
        for(Action a : actions){
            if(a.id.equals(action.id)){
                actions.set(actions.indexOf(a), action);
                notifyItemChanged(actions.indexOf(action));
                return;
            }
        }
        actions.add(action);
        notifyItemChanged(actions.indexOf(action));
    }

    void addAll(List<Action> actions){
        for(Action a : actions) add(a);
    }

    void empty(){
        actions = new ArrayList<>();
    }

    Action get(int index){
        return actions.get(index);
    }
}

