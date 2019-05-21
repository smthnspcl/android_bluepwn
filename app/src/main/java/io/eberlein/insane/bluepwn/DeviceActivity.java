package io.eberlein.insane.bluepwn;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static io.eberlein.insane.bluepwn.Static.TABLE_DEVICE;
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
    @BindView(R.id.onlyStagers) Switch onlyStagers;
    @BindView(R.id.notify) Switch notify;
    @BindView(R.id.terminal) Button terminal;

    @OnClick(R.id.terminal)
    public void onTerminalClicked(){
        if(bluetoothAdapter.getRemoteDevice(device.address) != null){
            Intent i = new Intent(this, TerminalActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("address", device.address);
            startActivity(i);
        } else {
            Toast.makeText(this, "device is not nearby", Toast.LENGTH_SHORT).show(); // redundant?
        }
    }

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

    @OnClick(R.id.onlyStagers)
    public void onlyStagerClicked(){
        if(onlyStagers.isActivated()) {
            serviceAdapter.empty();
            for (Service _s : device.getServices()) {
                List<Stager> stagers = _s.getStagers();
                if (stagers.size() > 0) serviceAdapter.add(_s);
            }
        } else {
            serviceAdapter.empty();
            serviceAdapter.addAll(device.getServices());
        }
    }

    @OnClick(R.id.notify)
    public void notifyClicked(){
        boolean existsInTable = Notification.exists(TABLE_DEVICE, device.address);
        if(notify.isActivated()){
            if(!existsInTable) Notification.get(TABLE_DEVICE, device.address).save();
        } else Notification.delete(TABLE_DEVICE, device.address);

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
        Log.onCreate(this.getClass());
        setContentView(R.layout.activity_device);
        ButterKnife.bind(this);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        gson = new Gson();
        serviceAdapter = new ServiceAdapter();
        device = Device.get(getIntent().getStringExtra("address"));
        if(getIntent().getBooleanExtra("live", false)) terminal.setVisibility(View.INVISIBLE);
        setTitle("dev: " + device.address);
        serviceAdapter.addAll(device.getServices());
        tvMac.setText(device.address);
        tvName.setText(device.name);
        tvType.setText(device.type);
        tvBond.setText(device.bond);
        notify.setChecked(Notification.exists(TABLE_DEVICE, device.address));
        locationCountLabel.setText(String.valueOf(device.getLocations().size()));
        tvManufacturer.setText(device.manufacturer);
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
        Log.onDestroy(this.getClass());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.onDestroy(this.getClass());
    }

    @Override
    protected void onResume() {
        super.onResume();
        serviceAdapter.empty();
        serviceAdapter.addAll(device.getServices());
        //if(bluetoothAdapter.getRemoteDevice(device.address) == null) terminal.setEnabled(false);
    }
}
