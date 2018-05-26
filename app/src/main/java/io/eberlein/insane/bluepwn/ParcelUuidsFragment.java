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
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ParcelUuidsFragment extends Fragment {

    @BindView(R.id.selectionQuery) AutoCompleteTextView selectionQuery;
    @BindView(R.id.selectionSpinner) Spinner selectionSpinner;
    @BindView(R.id.uuidsRecycler) RecyclerView uuidsRecycler;

    private ParcelUuidAdapter parcelUuidAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parcelUuidAdapter = new ParcelUuidAdapter();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_uuids, container, false);
        ButterKnife.bind(this, v);
        uuidsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        parcelUuidAdapter.addAll(SQLite.select().from(ParcelUuid.class).queryList());
        uuidsRecycler.setAdapter(parcelUuidAdapter);
        parcelUuidAdapter.setOnItemClickListener(new ParcelUuidAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int p) {
                Intent i = new Intent(getContext(), ParcelUuidActivity.class); // todo parceluuid activity
                // switch between actions using this uuid and devices having that uuid
                // display list in recycler
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("id", parcelUuidAdapter.get(p).id);
                startActivity(i);
            }
        });
        // selectionSpinner.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, ));
        return v;
    }
}
