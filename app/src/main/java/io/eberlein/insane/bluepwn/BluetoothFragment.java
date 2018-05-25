package io.eberlein.insane.bluepwn;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
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

import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;

import java.util.concurrent.Callable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;


public class BluetoothFragment extends Fragment {
    @BindView(R.id.scanBtn) FloatingActionButton scanBtn;
    @BindView(R.id.devicesRecycler) RecyclerView deviceRecycler;
    @BindView(R.id.continuousScanningCheckbox) CheckBox continuousScanningCheckbox;

    @OnCheckedChanged(R.id.continuousScanningCheckbox)
    void continuousScanningCheckboxCheckedChanged(){
        if(bound) scanService.setContinuousScanning(!scanService.getContinuousScanning());
        else continuousScanningCheckbox.setChecked(!continuousScanningCheckbox.isChecked());
    }

    @OnClick(R.id.scanBtn)
    void scanBtnClicked(){
        if(bound) {
            if (scanService.isScanning()) {
                continuousScanningCheckbox.setChecked(false);
                scanService.cancelScanning();
            } else {
                scanBtn.setImageResource(R.drawable.ic_clear_white_48dp);
                scanService.scan();
            }
        } else {
            Toast.makeText(getContext(), "scanService has not been bound", Toast.LENGTH_SHORT).show();
        }
    }

    private ScanService scanService;
    private DeviceAdapter devices;
    private Scan scan;
    private Boolean bound = false;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ScanService.ScanBinder binder = (ScanService.ScanBinder) service;
            scanService = binder.getService();
            bound = true;

            scanService.deviceDiscoveredCallableList.add(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    devices.add();
                    return null;
                }
            });

            scanService.discoveryFinishedCallableList.add(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    devices.addAll(scanService.getScan().devices);
                    scanBtn.setImageResource(R.drawable.ic_update_white_48dp);
                    return null;
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissions();
        devices = new DeviceAdapter();
    }

    @Override
    public void onStart() {
        super.onStart();
        getContext().bindService(new Intent(getActivity(), ScanService.class), serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_bluetooth, container, false);
        ButterKnife.bind(this, v);
        scanBtn.setImageResource(R.drawable.ic_update_white_48dp);
        deviceRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        deviceRecycler.setAdapter(devices);
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        scan = SQLite.select().from(Scan.class).orderBy(Scan_Table.id.desc()).querySingle();
        if(scan == null) scan = new Scan();
        devices.addAll(scan.devices);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unbindService(serviceConnection);
    }

    private void requestPermissions(){
        if(!(ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED))
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.BLUETOOTH}, Static.BLUETOOTH_RESULT);
        if(!(ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED))
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Static.LOCATION_RESULT);
    }
}
