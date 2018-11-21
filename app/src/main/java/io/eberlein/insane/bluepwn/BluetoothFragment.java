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
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static io.eberlein.insane.bluepwn.Static.ACTION_CURRENT_SCAN;
import static io.eberlein.insane.bluepwn.Static.ACTION_DATA_KEY;
import static io.eberlein.insane.bluepwn.Static.ACTION_DEVICE_DISCOVERED;
import static io.eberlein.insane.bluepwn.Static.ACTION_DISCOVERY_FINISHED;
import static io.eberlein.insane.bluepwn.Static.ACTION_DISCOVERY_STOPPED;
import static io.eberlein.insane.bluepwn.Static.ACTION_SCANNER_INITIALIZED;
import static io.eberlein.insane.bluepwn.Static.ACTION_SCAN_FINISHED;
import static io.eberlein.insane.bluepwn.Static.ACTION_SCAN_STARTED;
import static io.eberlein.insane.bluepwn.Static.ACTION_SCAN_STOPPED;
import static io.eberlein.insane.bluepwn.Static.ACTION_SERVICE_DISCOVERED;
import static io.eberlein.insane.bluepwn.Static.ACTION_START_SCAN;
import static io.eberlein.insane.bluepwn.Static.ACTION_STOP_DISCOVERY;
import static io.eberlein.insane.bluepwn.Static.ACTION_STOP_SCAN;
import static io.eberlein.insane.bluepwn.Static.TABLE_DEVICE;
import static io.eberlein.insane.bluepwn.Static.send2BcR;


public class BluetoothFragment extends Fragment {

    BroadcastReceiver currentlyScanningReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            currentlyScanning = Boolean.valueOf(intent.getStringExtra(ACTION_DATA_KEY));

        }
    };

    BroadcastReceiver currentlyDiscoveringReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            currentlyDiscovering = Boolean.valueOf(intent.getStringExtra(ACTION_DATA_KEY));
        }
    };

    BroadcastReceiver currentScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String key = intent.getStringExtra(ACTION_DATA_KEY);
            if(key == null) Log.log(this.getClass(), "key null; creating new scan object");
            else currentScan = Scan.get(key);
        }
    };

    BroadcastReceiver scanningStartedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            currentlyScanning = true;
            currentlyDiscovering = false;
            scanBtn.setImageResource(R.drawable.ic_clear_white_48dp);
        }
    };

    BroadcastReceiver scanningStoppedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            currentlyScanning = false;
            scanBtn.setImageResource(R.drawable.ic_sync_white_48dp);
        }
    };

    BroadcastReceiver scanningFinishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            currentScan = Scan.get(intent.getStringExtra(ACTION_DATA_KEY));
            Log.log(this.getClass(), currentScan.toString());
            devices.addAll(currentScan.getDevices());
            currentlyScanning = false;
            scanBtn.setImageResource(R.drawable.ic_sync_white_48dp);
        }
    };

    BroadcastReceiver discoveryStoppedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            currentlyScanning = true;
            currentlyDiscovering = false;
        }
    };

    BroadcastReceiver discoveryFinishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            currentScan = Scan.get(intent.getStringExtra(ACTION_DATA_KEY));
            currentlyDiscovering = false;
            currentlyScanning = true;
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
            scanningStartedReceiver, scanningStoppedReceiver, deviceDiscoveredReceiver, serviceDiscoveredReceiver, scannerServiceInitializedReceiver,
            scanningFinishedReceiver, discoveryStoppedReceiver, discoveryFinishedReceiver, currentScanReceiver
    };

    private void registerReceivers(){
        Context c = getContext();
        c.registerReceiver(scanningStartedReceiver, new IntentFilter(ACTION_SCAN_STARTED));
        c.registerReceiver(scanningStoppedReceiver, new IntentFilter(ACTION_SCAN_STOPPED));
        c.registerReceiver(deviceDiscoveredReceiver, new IntentFilter(ACTION_DEVICE_DISCOVERED));
        c.registerReceiver(serviceDiscoveredReceiver, new IntentFilter(ACTION_SERVICE_DISCOVERED));
        c.registerReceiver(scannerServiceInitializedReceiver, new IntentFilter(ACTION_SCANNER_INITIALIZED));
        c.registerReceiver(scanningFinishedReceiver, new IntentFilter(ACTION_SCAN_FINISHED));
        c.registerReceiver(discoveryStoppedReceiver, new IntentFilter(ACTION_DISCOVERY_STOPPED));
        c.registerReceiver(discoveryFinishedReceiver, new IntentFilter(ACTION_DISCOVERY_FINISHED));
        c.registerReceiver(currentScanReceiver, new IntentFilter(ACTION_CURRENT_SCAN));
    }

    private void unregisterReceivers(){
        Context c = getContext();
        for(BroadcastReceiver bcr : broadcastReceivers) c.unregisterReceiver(bcr);
    }

    @BindView(R.id.scanBtn) FloatingActionButton scanBtn;
    @BindView(R.id.devicesRecycler) RecyclerView deviceRecycler;
    @BindView(R.id.continuousScanningCheckbox) CheckBox continuousScanningCheckbox;

    private boolean currentlyScanning = false;
    private boolean currentlyDiscovering = false;
    private Scan currentScan;

    @OnClick(R.id.scanBtn)
    void scanBtnClicked(){
        Log.log(this.getClass(),"scanBtnClicked");
        if(!currentlyScanning) send2Service(ACTION_START_SCAN, null);
        else {
            if(currentlyScanning) send2Service(ACTION_STOP_SCAN, null);
            if(currentlyDiscovering) send2Service(ACTION_STOP_DISCOVERY, null);
        }
    }

    private DeviceAdapter devices;
    private ScanSettings settings;

    private List<Notification> toNotifyDevices;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.onCreate(this.getClass());
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
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.onResume(this.getClass());
        send2Service(ACTION_CURRENT_SCAN, null);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.onPause(this.getClass());
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
        send2BcR(getContext(), action, data);
    }
}
