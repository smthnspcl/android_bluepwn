package io.eberlein.insane.bluepwn;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;
import com.google.gson.Gson;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static io.eberlein.insane.bluepwn.Static.ACTION_DATA_KEY;
import static io.eberlein.insane.bluepwn.Static.ACTION_DEVICE_DISCOVERED;
import static io.eberlein.insane.bluepwn.Static.ACTION_SCANNER_INITIALIZED;
import static io.eberlein.insane.bluepwn.Static.ACTION_SCAN_STARTED;
import static io.eberlein.insane.bluepwn.Static.ACTION_SCAN_STOPPED;
import static io.eberlein.insane.bluepwn.Static.ACTION_SERVICE_DISCOVERED;
import static io.eberlein.insane.bluepwn.Static.ACTION_START_SCAN;
import static io.eberlein.insane.bluepwn.Static.ACTION_STOP_SCAN;
import static io.eberlein.insane.bluepwn.Static.TABLE_DEVICE;


public class BluetoothFragment extends Fragment {
    private Intent scannerServiceIntent;

    BroadcastReceiver scanningStartedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            scanBtn.setImageResource(R.drawable.ic_clear_white_48dp);
        }
    };

    BroadcastReceiver scanningStoppedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            scanBtn.setImageResource(R.drawable.ic_sync_white_48dp);
        }
    };

    BroadcastReceiver deviceDiscoveredReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            devices.add(Device.getExistingOrNew(intent.getStringExtra(ACTION_DATA_KEY)));
        }
    };

    BroadcastReceiver serviceDiscoveredReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            devices.add(Device.getExistingOrNew(intent.getStringExtra(ACTION_DATA_KEY)));
        }
    };

    BroadcastReceiver scannerServiceInitializedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(getContext(), "scanner service initialized", Toast.LENGTH_SHORT).show();
        }
    };

    private final BroadcastReceiver[] broadcastReceivers = {
            scanningStartedReceiver, scanningStoppedReceiver, deviceDiscoveredReceiver, serviceDiscoveredReceiver, scannerServiceInitializedReceiver
    };

    @BindView(R.id.scanBtn) FloatingActionButton scanBtn;
    @BindView(R.id.devicesRecycler) RecyclerView deviceRecycler;
    @BindView(R.id.continuousScanningCheckbox) CheckBox continuousScanningCheckbox;

    private boolean currentlyScanning = false;

    @OnClick(R.id.scanBtn)
    void scanBtnClicked(){
        Log.log(this.getClass(),"scanBtnClicked");
        if(!currentlyScanning) send2Service(ACTION_START_SCAN, null);
        else send2Service(ACTION_STOP_SCAN, null);
        currentlyScanning = !currentlyScanning;
        Log.log(this.getClass(), "scanning: " + String.valueOf(currentlyScanning));
    }

    private DeviceAdapter devices;
    private ScanSettings settings;

    private List<Notification> toNotifyDevices;

    private void registerReceivers(){
        getContext().registerReceiver(scanningStartedReceiver, new IntentFilter(ACTION_SCAN_STARTED));
        getContext().registerReceiver(scanningStoppedReceiver, new IntentFilter(ACTION_SCAN_STOPPED));
        getContext().registerReceiver(deviceDiscoveredReceiver, new IntentFilter(ACTION_DEVICE_DISCOVERED));
        getContext().registerReceiver(serviceDiscoveredReceiver, new IntentFilter(ACTION_SERVICE_DISCOVERED));
        getContext().registerReceiver(scannerServiceInitializedReceiver, new IntentFilter(ACTION_SCANNER_INITIALIZED));
    }

    private void unregisterReceivers(){
        for(BroadcastReceiver bcr : broadcastReceivers) getContext().unregisterReceiver(bcr);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.onCreate(this.getClass());
        scannerServiceIntent = new Intent(getContext(), ScannerService.class);
        getActivity().setTitle("scan");
        requestPermissions();
        registerReceivers();
        settings = ScanSettings.getExistingOrNew();
        toNotifyDevices = Notification.getByTable(TABLE_DEVICE);
        devices = new DeviceAdapter();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.onStart(this.getClass());
        //if(scan == null) {scan = new Scan();}  todo
        //devices.addAll(scan.getDevices());
    }

    @Override
    public void onResume() {
        super.onResume();
        getContext().startService(scannerServiceIntent);
    }

    @Override
    public void onPause() {
        super.onPause();
        getContext().stopService(scannerServiceIntent);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.onStop(this.getClass());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceivers();
        Log.onDestroy(this.getClass());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_bluetooth, container, false);
        ButterKnife.bind(this, v);
        continuousScanningCheckbox.setChecked(settings.continuousScanningDefault);
        scanBtn.setImageResource(R.drawable.ic_update_white_48dp);
        deviceRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        deviceRecycler.setAdapter(devices);
        devices.setOnItemClickListener(new DeviceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int p) {
                Intent i = new Intent(getContext(), DeviceActivity.class);
                i.putExtra("address", devices.get(p).address);
                i.putExtra("live", true);
                startActivity(i);
            }
        });
        return v;
    }

    private void requestPermissions(){
        Log.log(this.getClass(), "requesting permissions");
        if(!(ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED))
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.BLUETOOTH}, Static.BLUETOOTH_RESULT);
        if(!(ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED))
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Static.LOCATION_RESULT);
    }

    private void send2Service(String action, @Nullable String data){
        Intent i = new Intent(action);
        if(data != null) i.putExtra(ACTION_DATA_KEY, data);
        getContext().sendBroadcast(i);
    }
}
