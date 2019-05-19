package io.eberlein.insane.bluepwn;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;

import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StagesFragment extends Fragment {

    @BindView(R.id.spinner) Spinner selectionSpinner;
    @BindView(R.id.query) AutoCompleteTextView selectionQuery;
    @BindView(R.id.recycler) RecyclerView recycler;
    @BindView(R.id.fab) RapidFloatingActionButton fab;

    private StageAdapter stages;
    private ArrayAdapter<String> selectionSpinnerAdapter;

    @OnClick(R.id.search)
    public void searchBtnClicked() {

    }

    @OnClick(R.id.fab)
    public void addButtonClicked(){
        Intent i = new Intent(getContext(), StageActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    // todo load saved stages

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.objectlist_search, container, false);
        getActivity().setTitle("stages");
        ButterKnife.bind(this, v);
        initStageRecycler();
        selectionSpinner.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, Static.STAGE_KEYS));
        return v;
    }

    private void initStageRecycler(){
        List<Stage> _stages = Stage.get();
        Log.log(this.getClass(), "stages: " + _stages.size());
        stages.addAll(_stages);
        stages.setOnItemClickListener(new StageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int p) {
                Intent i = new Intent(getContext(), DeviceActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("uuid", stages.get(p).uuid);
                startActivity(i);
            }
        });
        recycler.setAdapter(stages);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        //query.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, _devices));
    }
}
