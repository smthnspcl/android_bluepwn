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
import io.eberlein.insane.bluepwn.adapter.StagerAdapter;
import io.eberlein.insane.bluepwn.object.Service;

public class ServiceTabStagersFragment extends Fragment {
    @BindView(R.id.recycler) RecyclerView stagerRecycler;

    private Service service;
    private StagerAdapter stagers;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service = Service.get(getArguments().getString("uuid"));
        stagers = new StagerAdapter();
        stagers.addAll(service.getStagers());
    }

    public static ServiceTabStagersFragment newInstance(int p, Service s) {
        ServiceTabStagersFragment stsf = new ServiceTabStagersFragment();
        Bundle b = new Bundle();
        b.putString("uuid", s.getUuid());
        stsf.setArguments(b);
        return stsf;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.recycler, container, false);
        ButterKnife.bind(this, v);
        stagerRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        stagerRecycler.setAdapter(stagers);
        return v;
    }
}
