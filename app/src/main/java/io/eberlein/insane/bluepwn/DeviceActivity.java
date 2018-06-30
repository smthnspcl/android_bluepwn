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

import com.google.gson.Gson;

import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.paperdb.Paper;

import static io.eberlein.insane.bluepwn.Static.TYPE_CLASSIC;
import static io.eberlein.insane.bluepwn.Static.TYPE_DUAL;
import static io.eberlein.insane.bluepwn.Static.TYPE_LE;

public class DeviceActivity extends AppCompatActivity {
    @BindView(R.id.tvMac) TextView tvMac;
    @BindView(R.id.tvName) TextView tvName;
    @BindView(R.id.tvType) TextView tvType;
    @BindView(R.id.tvBond) TextView tvBond;
    @BindView(R.id.locationCountLabel) TextView locationCountLabel;
    @BindView(R.id.tvManufacturer) TextView tvManufacturer;
    @BindView(R.id.recycler) RecyclerView recycler;

    ServiceAdapter serviceAdapter;
    BluetoothAdapter bluetoothAdapter;
    Device device;
    private Gson gson;

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
        i.putExtra("locations", gson.toJson(device.getLocations()));
        startActivity(i);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        ButterKnife.bind(this);
        gson = new Gson();
        serviceAdapter = new ServiceAdapter();
        device = Device.get(getIntent().getStringExtra("address"));
        setTitle("dev: " + device.address);
        serviceAdapter.addAll(device.getServices());
        tvMac.setText(device.address);
        tvName.setText(device.name);
        tvType.setText(device.type);
        tvBond.setText(device.bond);
        locationCountLabel.setText(String.valueOf(device.getLocations().size()));
        tvManufacturer.setText(device.manufacturer);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        recycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        serviceAdapter.setOnItemClickListener(new ServiceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int p) {
                Intent i = new Intent(getApplicationContext(), ServiceActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("uuid", serviceAdapter.get(p).uuid);
                startActivity(i);
            }
        });

        switch (device.type){
            case TYPE_LE: onBluetoothLe(); break;
            case TYPE_CLASSIC: onBluetoothClassic(); break;
            case TYPE_DUAL: onBluetoothDual(); break;
            default: onBluetoothElse();
        }
        recycler.setAdapter(serviceAdapter);
    }


    void onBluetoothLe(){

    }

    void onBluetoothClassic(){

    }

    void onBluetoothDual(){

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

    @Override
    protected void onResume() {
        super.onResume();
        serviceAdapter.empty();
        serviceAdapter.addAll(device.getServices());
    }
}
