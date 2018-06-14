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

import butterknife.BindView;
import butterknife.ButterKnife;

public class ServicesFragment extends Fragment {

    @BindView(R.id.query) AutoCompleteTextView query;
    @BindView(R.id.spinner) Spinner spinner;
    @BindView(R.id.recycler) RecyclerView recycler;

    private ServiceAdapter serviceAdapter;
    private static final String[] selectionSpinnerItems = {
            "id", "name", "description", "protocol" // todo
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serviceAdapter = new ServiceAdapter();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.objectlist_search, container, false);
        ButterKnife.bind(this, v);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        serviceAdapter.addAll(Service.get());
        recycler.setAdapter(serviceAdapter);
        serviceAdapter.setOnItemClickListener(new ServiceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int p) {
                Intent i = new Intent(getContext(), ServiceActivity.class);
                // switch between actions using this uuid and devices having that uuid
                // display list in recycler
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("service", serviceAdapter.get(p).uuid);
                startActivity(i);
            }
        });
        spinner.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, selectionSpinnerItems));
        return v;
    }
}
