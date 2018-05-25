package io.eberlein.insane.bluepwn;

import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DeviceActivity extends AppCompatActivity {
    @BindView(R.id.tvMac) TextView tvMac;
    @BindView(R.id.tvName) TextView tvName;
    @BindView(R.id.tvType) TextView tvType;
    @BindView(R.id.tvBond) TextView tvBond;
    @BindView(R.id.tvManufacturer) TextView tvManufacturer;
    @BindView(R.id.actionRecycler) RecyclerView actionRecycler;

    ParcelUuidAdapter parcelUuidAdapter;
    BluetoothAdapter bluetoothAdapter;
    Device device;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        ButterKnife.bind(this);
        parcelUuidAdapter = new ParcelUuidAdapter();
        device = SQLite.select().from(Device.class).where(Device_Table.address.eq(getIntent().getStringExtra("address"))).querySingle();
        List<ParcelUuid> uuids = new ArrayList<>();
        for(Object uid : device.parcelUuidsJson)
            uuids.add(SQLite.select().from(ParcelUuid.class).where(ParcelUuid_Table.id.eq(Long.valueOf((Integer) uid))).querySingle());
        parcelUuidAdapter.addAll(uuids);
        tvMac.setText(device.address);
        tvName.setText(device.name);
        tvType.setText(device.type);
        tvBond.setText(device.bond);
        tvManufacturer.setText(device.manufacturer);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        actionRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        parcelUuidAdapter.setOnItemClickListener(new ParcelUuidAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int p) {
                // action activity for device uuids
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

    void onBluetoothElse(){}

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


}
