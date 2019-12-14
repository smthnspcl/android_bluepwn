package io.eberlein.insane.bluepwn.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.eberlein.insane.bluepwn.R;
import io.eberlein.insane.bluepwn.adapter.CharacteristicsAdapter;
import io.eberlein.insane.bluepwn.object.Service;

public class ServiceTabCharacteristicsFragment extends Fragment {

    @BindView(R.id.recycler) RecyclerView recycler;

    private CharacteristicsAdapter characteristics;
    private Service service;

    public static ServiceTabCharacteristicsFragment newInstance(int p, Service s) {
        ServiceTabCharacteristicsFragment stsf = new ServiceTabCharacteristicsFragment();
        Bundle b = new Bundle();
        b.putString("uuid", s.getUuid());
        stsf.setArguments(b);
        return stsf;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service = Service.get(getArguments().getString("uuid"));
        characteristics = new CharacteristicsAdapter();
        characteristics.addAll(service.getCharacteristics());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.recycler, container, false);
        ButterKnife.bind(this, v);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(characteristics);
        return v;
    }
}
