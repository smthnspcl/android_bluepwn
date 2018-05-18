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
import com.alibaba.fastjson.JSON;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeviceActivity extends AppCompatActivity {
    @BindView(R.id.tvMac) TextView tvMac;
    @BindView(R.id.tvName) TextView tvName;
    @BindView(R.id.tvType) TextView tvType;
    @BindView(R.id.tvBond) TextView tvBond;
    @BindView(R.id.tvManufacturer) TextView tvManufacturer;
    @BindView(R.id.actionRecycler) RecyclerView actionRecycler;

    DeviceUUIDAdapter deviceUUIDAdapter;
    BluetoothAdapter bluetoothAdapter;
    Device device;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        ButterKnife.bind(this);
        deviceUUIDAdapter = new DeviceUUIDAdapter();
        device = SQLite.select().from(Device.class).where(Device_Table.address.eq(getIntent().getExtras().getString("address"))).querySingle();
        // deviceUUIDAdapter.populate(device.uuids);
        tvMac.setText(device.address);
        tvName.setText(device.name);
        tvType.setText(device.type);
        tvBond.setText(device.bond);
        tvManufacturer.setText(device.manufacturer);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        actionRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        // todo find actions by manufacturer and service id
        deviceUUIDAdapter.setOnItemClickListener(new DeviceUUIDAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int p) {
                AlertDialog.Builder b = new AlertDialog.Builder(getApplicationContext());
                b.setPositiveButton("send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // todo three button chooser (close, edit, send)
                        Intent i = new Intent();
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.putExtra("device", JSON.toJSONString(device));
                        startActivity(i);
                    }
                });
            }
        });

        switch (device.type){
            case "le": onBluetoothLe(); break;
            case "classic": onBluetoothClassic(); break;
            default: onBluetoothElse();
        }
        actionRecycler.setAdapter(deviceUUIDAdapter);
        // device.getMac() returns null and throws null
    }

    void onBluetoothLe(){

    }

    void onBluetoothClassic(){
        // iterate over device.uuids
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
