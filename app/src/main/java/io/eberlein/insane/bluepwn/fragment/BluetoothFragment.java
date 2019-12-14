package io.eberlein.insane.bluepwn.fragment;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.eberlein.insane.bluepwn.R;
import io.eberlein.insane.bluepwn.Static;
import io.eberlein.insane.bluepwn.activity.DeviceActivity;
import io.eberlein.insane.bluepwn.adapter.DeviceAdapter;
import io.eberlein.insane.bluepwn.object.Device;
import io.eberlein.insane.bluepwn.object.Notification;
import io.eberlein.insane.bluepwn.object.Scan;
import io.eberlein.insane.bluepwn.object.ScanSettings;

import static io.eberlein.insane.bluepwn.Static.TABLE_DEVICE;
import static io.eberlein.insane.bluepwn.Static.action.ACTION_CODE_KEY;
import static io.eberlein.insane.bluepwn.Static.action.ACTION_DATA_KEY;
import static io.eberlein.insane.bluepwn.Static.action.scanner.ACTION_SCANNER_CMD;
import static io.eberlein.insane.bluepwn.Static.action.scanner.ACTION_SCANNER_INFO;
import static io.eberlein.insane.bluepwn.Static.action.scanner.codes.ACTION_CODE_CURRENT_SCAN;
import static io.eberlein.insane.bluepwn.Static.action.scanner.codes.ACTION_CODE_DEVICE_DISCOVERED;
import static io.eberlein.insane.bluepwn.Static.action.scanner.codes.ACTION_CODE_DISCOVERY_FINISHED;
import static io.eberlein.insane.bluepwn.Static.action.scanner.codes.ACTION_CODE_DISCOVERY_STARTED;
import static io.eberlein.insane.bluepwn.Static.action.scanner.codes.ACTION_CODE_DISCOVERY_STOPPED;
import static io.eberlein.insane.bluepwn.Static.action.scanner.codes.ACTION_CODE_GET_CURRENT_SCAN;
import static io.eberlein.insane.bluepwn.Static.action.scanner.codes.ACTION_CODE_SCANNING_FINISHED;
import static io.eberlein.insane.bluepwn.Static.action.scanner.codes.ACTION_CODE_SCANNING_STARTED;
import static io.eberlein.insane.bluepwn.Static.action.scanner.codes.ACTION_CODE_SCANNING_STOPPED;
import static io.eberlein.insane.bluepwn.Static.action.scanner.codes.ACTION_CODE_SERVICE_DISCOVERED;
import static io.eberlein.insane.bluepwn.Static.action.scanner.codes.ACTION_CODE_SERVICE_INITIALIZED;
import static io.eberlein.insane.bluepwn.Static.action.scanner.codes.ACTION_CODE_START_DISCOVERY;
import static io.eberlein.insane.bluepwn.Static.action.scanner.codes.ACTION_CODE_STOP_DISCOVERY;
import static io.eberlein.insane.bluepwn.Static.action.scanner.codes.ACTION_CODE_STOP_SCAN;

// todo
// modal after scan to ask for current position note if no gps
// pull down menu with quick settings

public class BluetoothFragment extends Fragment {
    BroadcastReceiver scannerServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(getClass().toString(), intent.getStringExtra(ACTION_CODE_KEY));
            Log.d(getClass().toString(), "\t" + intent.getStringExtra(ACTION_DATA_KEY));
            switch (intent.getStringExtra(ACTION_CODE_KEY)){
                case ACTION_CODE_CURRENT_SCAN:
                    currentScan = Scan.get(intent.getStringExtra(ACTION_DATA_KEY));
                    break;
                case ACTION_CODE_SCANNING_STARTED:
                    currentlyScanning = true;
                    currentlyDiscovering = false;
                    scanBtn.setImageResource(R.drawable.baseline_device_unknown_white_48);
                    break;
                case ACTION_CODE_SCANNING_STOPPED:
                    currentlyScanning = false;
                    scanBtn.setImageResource(R.drawable.ic_sync_white_48dp);
                    break;
                case ACTION_CODE_SCANNING_FINISHED:
                    //currentScan = Scan.get(intent.getStringExtra(ACTION_DATA_KEY));
                    Log.d(getClass().toString(), "scan uuid: " + currentScan.getUuid());
                    Log.d(getClass().toString(), "\tsize: " + currentScan.getDevices().size());
                    //devices.addAll(currentScan.getDevices());
                    currentlyScanning = false;
                    scanBtn.setImageResource(R.drawable.ic_sync_white_48dp);
                    break;
                case ACTION_CODE_DISCOVERY_STARTED:
                    currentlyDiscovering = false;
                    scanBtn.setImageResource(R.drawable.ic_clear_white_48dp);
                    break;
                case ACTION_CODE_DISCOVERY_STOPPED:
                    currentlyScanning = true;
                    currentlyDiscovering = false;
                    break;
                case ACTION_CODE_DISCOVERY_FINISHED:
                    currentScan = Scan.get(intent.getStringExtra(ACTION_DATA_KEY));
                    currentlyDiscovering = false;
                    currentlyScanning = true;
                    break;
                case ACTION_CODE_DEVICE_DISCOVERED:
                    devices.add(Device.get(intent.getStringExtra(ACTION_DATA_KEY)));
                    break;
                case ACTION_CODE_SERVICE_DISCOVERED:
                    devices.add(Device.get(intent.getStringExtra(ACTION_DATA_KEY)));
                    break;
                case ACTION_CODE_SERVICE_INITIALIZED:
                    Toast.makeText(getContext(), "scanner service initialized", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void registerReceivers(){
        getContext().registerReceiver(scannerServiceReceiver, new IntentFilter(ACTION_SCANNER_INFO));
    }

    private void unregisterReceivers(){
        getContext().unregisterReceiver(scannerServiceReceiver);
    }

    @BindView(R.id.scanBtn) FloatingActionButton scanBtn;
    @BindView(R.id.devicesRecycler) RecyclerView deviceRecycler;
    @BindView(R.id.continuousScanningCheckbox) CheckBox continuousScanningCheckbox;

    private boolean currentlyScanning = false;
    private boolean currentlyDiscovering = false;
    private Scan currentScan;

    @OnClick(R.id.scanBtn)
    void scanBtnClicked(){
        if(!currentlyScanning && !currentlyDiscovering) {
            send2Service(ACTION_CODE_START_DISCOVERY, null);
        } else {
            if (currentlyScanning) {
                Log.d(this.getClass().toString(), "stopping scan");
                send2Service(ACTION_CODE_STOP_SCAN, null);
            }
            if (currentlyDiscovering) {
                Log.d(this.getClass().toString(), "stopping discovery");
                send2Service(ACTION_CODE_STOP_DISCOVERY, null);
            }
        }
    }

    private DeviceAdapter devices;
    private ScanSettings settings;

    private List<Notification> toNotifyDevices;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }

    @Override
    public void onResume() {
        super.onResume();
        send2Service(ACTION_CODE_GET_CURRENT_SCAN, null);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceivers();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_bluetooth, container, false);
        ButterKnife.bind(this, v);
        continuousScanningCheckbox.setChecked(settings.getContinuousScanning());
        scanBtn.setImageResource(R.drawable.ic_update_white_48dp);
        deviceRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        deviceRecycler.setAdapter(devices);
        devices.setOnItemClickListener(new DeviceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int p) {
                Intent i = new Intent(getContext(), DeviceActivity.class);
                i.putExtra("address", devices.get(p).getAddress());
                i.putExtra("live", true);
                startActivity(i);
            }
        });
        return v;
    }

    private void requestPermissions(){
        if(!(ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED))
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.BLUETOOTH}, Static.BLUETOOTH_RESULT);
        if(!(ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED))
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Static.LOCATION_RESULT);
    }

    private void send2Service(String action, @Nullable String data){
        Intent i = new Intent(ACTION_SCANNER_CMD);
        i.putExtra(ACTION_CODE_KEY, action);
        if(data != null) i.putExtra(ACTION_DATA_KEY, data);
        getContext().sendBroadcast(i);
    }
}
