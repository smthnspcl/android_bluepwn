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

public class StagersFragment extends Fragment{
    @BindView(R.id.spinner) Spinner selectionSpinner;
    @BindView(R.id.query) AutoCompleteTextView selectionQuery;
    @BindView(R.id.recycler) RecyclerView recycler;
    @BindView(R.id.fab) RapidFloatingActionButton fab;

    private StagerAdapter stagerAdapter;
    private ArrayAdapter<String> selectionSpinnerAdapter;

    @OnClick(R.id.search)
    public void searchBtnClicked(){
        stagerAdapter.empty();
        //String q = selectionQuery.getText().toString();
        /*if(q.isEmpty())*/ stagerAdapter.addAll(Stager.get());
        /*else {

            Operator p = null;
            switch (selectionSpinner.getSelectedItem().toString()){
                case "id":
                    p = Action_Table.id.eq(Long.getLong(q)); break; // todo check if number supplied
                case "name":
                    p = Action_Table.name.like(q); break; // does not work
                case "hex":
                    p = Action_Table.hex.is(q.equals("true")); break; // works
                case "data":
                    p = Action_Table.data.like(q); break;
                case "macPrefix":
                    p = Action_Table.macPrefix.like(q); break;

            }
            //if(p != null) actionAdapter.addAll(SQLite.select().from(Action.class).where(p).queryList());
            actionAdapter.addAll(LocalDatabase.getAllActions());

        }*/
    }

    @OnClick(R.id.fab)
    public void addStagerButtonClicked(){
        Intent i = new Intent(getContext(), StagerActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.onCreate(this.getClass());
        stagerAdapter = new StagerAdapter();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.objectlist_search, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.onResume(this.getClass());
        init();
    }

    static StagersFragment newInstance(int p, Service s){
        StagersFragment sf = new StagersFragment();
        Bundle b = new Bundle();
        b.putString("uuid", s.uuid);
        sf.setArguments(b);
        return sf;
    }

    private void initStagerRecycler(List<Stager> stagers){
        stagerAdapter.addAll(stagers);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(stagerAdapter);
        stagerAdapter.setOnItemClickListener(new StagerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int p) {
                Intent i = new Intent(getContext(), StagerActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("id", stagers.get(p).uuid);
                startActivity(i);
            }
        });
    }

    private void init(){
        List<Stager> stagers = Stager.get();
        initStagerRecycler(stagers);
        // ArrayAdapter<Stager> actionArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, stagers);
        selectionSpinner.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, Static.STAGER_KEYS));
    }

    public StagersFragment(){ }
}
