package io.eberlein.insane.bluepwn.fragment;

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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.eberlein.insane.bluepwn.R;
import io.eberlein.insane.bluepwn.Static;
import io.eberlein.insane.bluepwn.activity.DeviceActivity;
import io.eberlein.insane.bluepwn.adapter.DeviceAdapter;
import io.eberlein.insane.bluepwn.object.Device;

public class DevicesFragment extends Fragment {

    @BindView(R.id.query) AutoCompleteTextView query;
    @BindView(R.id.recycler) RecyclerView recycler;
    @BindView(R.id.spinner) Spinner spinner;

    private DeviceAdapter devices;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        devices = new DeviceAdapter();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.objectlist_search, container, false);
        getActivity().setTitle("devices");
        ButterKnife.bind(this, v);
        initDeviceRecycler();
        spinner.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, Static.DEVICE_KEYS));
        return v;
    }

    private void initDeviceRecycler(){
        List<Device> _devices = Device.get();
        devices.addAll(_devices);
        devices.setOnItemClickListener(new DeviceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int p) {
                Intent i = new Intent(getContext(), DeviceActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("address", devices.get(p).getAddress());
                startActivity(i);
            }
        });
        recycler.setAdapter(devices);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        query.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, _devices));
    }
}
