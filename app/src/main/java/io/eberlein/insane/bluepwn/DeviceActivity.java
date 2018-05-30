package io.eberlein.insane.bluepwn;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.paperdb.Paper;

public class DeviceActivity extends AppCompatActivity {
    @BindView(R.id.tvMac) TextView tvMac;
    @BindView(R.id.tvName) TextView tvName;
    @BindView(R.id.tvType) TextView tvType;
    @BindView(R.id.tvBond) TextView tvBond;
    @BindView(R.id.locationCountLabel) TextView locationCountLabel;
    @BindView(R.id.tvManufacturer) TextView tvManufacturer;
    @BindView(R.id.actionRecycler) RecyclerView actionRecycler;

    ParcelUuidAdapter parcelUuidAdapter;
    BluetoothAdapter bluetoothAdapter;
    Device device;

    @OnClick(R.id.locationCountLabel)
    public void locationCountLabelClicked(){
        locationsActivityIntent();
    }

    @OnClick(R.id.locationLabel)
    public void locationLabelClicked(){
        locationsActivityIntent();
    }

    private void locationsActivityIntent(){
        Intent i = new Intent(this, LocationsActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("locations", JSON.toJSONString(device.getLocations()));
        startActivity(i);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        ButterKnife.bind(this);
        parcelUuidAdapter = new ParcelUuidAdapter();
        device = Paper.book("device").read(getIntent().getStringExtra("address"));
        List<ParcelUuid> uuids = device.getParcelUuids();
        parcelUuidAdapter.addAll(uuids);
        tvMac.setText(device.address);
        tvName.setText(device.name);
        tvType.setText(device.type);
        tvBond.setText(device.bond);
        locationCountLabel.setText(String.valueOf(device.getLocations().size()));
        tvManufacturer.setText(device.manufacturer);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        actionRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        parcelUuidAdapter.setOnItemClickListener(new ParcelUuidAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int p) {
                Intent i = new Intent(getApplicationContext(), ParcelUuidActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("uuid", parcelUuidAdapter.get(p).uuid.toString());
                startActivity(i);
            }
        });

        switch (device.type){
            case "le": onBluetoothLe(); break;
            case "classic": onBluetoothClassic(); break;
            default: onBluetoothElse();
        }
        actionRecycler.setAdapter(parcelUuidAdapter);
    }


    void onBluetoothLe(){

    }

    void onBluetoothClassic(){

    }

    void onBluetoothElse(){

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


}
