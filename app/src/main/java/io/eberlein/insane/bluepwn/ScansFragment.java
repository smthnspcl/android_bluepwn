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
import android.widget.Spinner;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScansFragment extends Fragment {

    @BindView(R.id.scansRecycler) RecyclerView scansRecycler;
    @BindView(R.id.filterSpinner) Spinner filters;

    private ScanAdapter scans;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scans = new ScanAdapter();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_scans, container, false);
        ButterKnife.bind(this, v);
        scansRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        scansRecycler.setAdapter(scans);
        scans.addAll(SQLite.select().from(Scan.class).orderBy(Scan_Table.id.desc()).queryList());
        scans.setOnItemClickListener(new ScanAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int p) {
                Intent i = new Intent(getContext(), ScanActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("id", scans.get(p).id);
                startActivity(i);
            }
        });
        return v;
    }
}
