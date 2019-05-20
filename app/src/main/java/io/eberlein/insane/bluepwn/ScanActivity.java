package io.eberlein.insane.bluepwn;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ScanActivity extends AppCompatActivity {

    @BindView(R.id.locationCountLabel) TextView locationCount;
    @BindView(R.id.devicesRecycler) RecyclerView devicesRecycler;
    @BindView(R.id.filterSpinner) Spinner filters;

    private DeviceAdapter devices;
    private Gson gson;
    private static final String[] selectionSpinnerAdapterItems = {
            "person unk.", "location unk."
    };

    @OnClick(R.id.locationCountLabel)
    public void locationCountLabelClicked(){
        _goToLocations();
    }

    @OnClick(R.id.textView6)
    public void locationLabelClicked(){
        _goToLocations();
    }

    public void _goToLocations(){
        Intent i = new Intent(this, LocationsActivity.class);
        i.putExtra("uuid", scan.uuid);
        startActivity(i);
    }

    private Scan scan;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.onCreate(this.getClass());
        setContentView(R.layout.activity_scan);
        ButterKnife.bind(this);
        setTitle("scan");
        scan = Scan.get(getIntent().getStringExtra("uuid"));
        locationCount.setText(String.valueOf(scan.locations.size()));
        devices = new DeviceAdapter();
        devices.addAll(scan.getDevices());
        devicesRecycler.setLayoutManager(new LinearLayoutManager(this));
        devicesRecycler.setAdapter(devices);
        devices.setOnItemClickListener(new DeviceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int p) {
                Intent i = new Intent(getApplicationContext(), DeviceActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("address", devices.get(p).address);
                startActivity(i);
            }
        });
        filters.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, selectionSpinnerAdapterItems));
    }
}
