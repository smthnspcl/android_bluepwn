package io.eberlein.insane.bluepwn;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsFragment extends Fragment {

    private static final SettingsItem[] SETTINGS_ITEMS = {
            new SettingsItem("general", GeneralSettingsActivity.class),
            new SettingsItem("scanning", ScanningSettingsActivity.class),
            new SettingsItem("remote db", RemoteDatabaseSettingsActivity.class),
            new SettingsItem("local db", LocalDatabaseSettingsActivity.class),
            new SettingsItem("ui", UISettingsActivity.class)
    };

    @BindView(R.id.recycler) RecyclerView recycler;

    private SettingsItemAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, v);
        adapter = new SettingsItemAdapter();
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(adapter);
        adapter.setOnItemClickListener(new SettingsItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int p) {
                Intent i = new Intent(getContext(), adapter.get(p).c);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.empty();
        adapter.addAll(Arrays.asList(SETTINGS_ITEMS));
    }
}
