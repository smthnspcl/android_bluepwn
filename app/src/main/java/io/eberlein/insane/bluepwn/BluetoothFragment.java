package io.eberlein.insane.bluepwn;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
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

import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class BluetoothFragment extends Fragment {
    @BindView(R.id.scanBtn) FloatingActionButton scanBtn;
    @BindView(R.id.devicesRecycler) RecyclerView deviceRecycler;
    @BindView(R.id.continuousScanningCheckbox) CheckBox continuousScanningCheckbox;

    @OnClick(R.id.scanBtn)
    void scanBtnClicked(){
        if(!bluetoothAdapter.isEnabled()) bluetoothAdapter.enable();
        if(bluetoothAdapter.getState() == BluetoothAdapter.STATE_ON){
            if(bluetoothAdapter.isDiscovering()){
                continuousScanningCheckbox.setChecked(false);
                bluetoothAdapter.cancelDiscovery();
                scanBtn.setImageResource(R.drawable.ic_update_white_48dp);
            } else {
                scan.lastLoaded = false;
                saveScan(scan);
                scan = new Scan();
                bluetoothAdapter.startDiscovery();
                scanBtn.setImageResource(R.drawable.ic_clear_white_48dp);
            }
        }
    }

    private LocationManager locationManager;
    private GPSLocationListener locationListener;

    private BluetoothAdapter bluetoothAdapter;
    private DeviceAdapter devices;
    private DatabaseDefinition db;
    private Scan scan;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissions();
        initGPS();
        scan = new Scan();
        db = FlowManager.getDatabase(LocalDatabase.class);
        devices = new DeviceAdapter();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        registerReceivers();
    }

    private void initGPS(){
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new GPSLocationListener();
        try{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_bluetooth, container, false);
        ButterKnife.bind(this, v);
        scanBtn.setImageResource(R.drawable.ic_update_white_48dp);
        deviceRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        scan.lastLoaded = true;
        saveScan(scan);
        unregisterReceivers();
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceivers();
        // loadLastScan();
    }

    private void unregisterReceivers(){
        try{
            getActivity().unregisterReceiver(onUuidFoundReceiver);
            getActivity().unregisterReceiver(onDeviceDiscoveredReceiver);
            getActivity().unregisterReceiver(onDiscoveryFinishedReceiver);
            getActivity().unregisterReceiver(onDiscoveryStartedReceiver);
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    private void registerReceivers(){
        try{
            getActivity().registerReceiver(onUuidFoundReceiver, new IntentFilter(BluetoothDevice.ACTION_UUID));
            getActivity().registerReceiver(onDeviceDiscoveredReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
            getActivity().registerReceiver(onDiscoveryFinishedReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
            getActivity().registerReceiver(onDiscoveryStartedReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    private void saveScan(Scan scan){
        db.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                scan.save(databaseWrapper);
            }
        }).build().execute();
        // todo onerror save for later saving
    }

    private void saveDevice(Device device){
        db.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                device.save(databaseWrapper);
            }
        });
    }

    private void saveParcelUuid(ParcelUuid parcelUuid){
        db.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                parcelUuid.save(databaseWrapper);
            }
        }).build().execute();
    }

    private void requestPermissions(){
        if(!(ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED))
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.BLUETOOTH}, Static.BLUETOOTH_RESULT);
        if(!(ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED))
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Static.LOCATION_RESULT);
    }

    BroadcastReceiver onDeviceDiscoveredReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(BluetoothDevice.ACTION_FOUND.equals(intent.getAction())){
                Device d = new Device(intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE));
                if(locationListener.currentLocation == null) d.locationIdsJson.add(new Location());
                else d.locationIdsJson.add(locationListener.currentLocation.id);
                devices.add(d);
                scan.devices.add(d);
                saveDevice(d);
            }
        }
    };

    BroadcastReceiver onDiscoveryFinishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())){
                for(Device d : devices.get()) bluetoothAdapter.getRemoteDevice(d.address).fetchUuidsWithSdp();
                if(continuousScanningCheckbox.isChecked()) bluetoothAdapter.startDiscovery(); // todo also run in background ( service? )
                else scanBtn.setImageResource(R.drawable.ic_update_white_48dp);
            }
        }
    };

    BroadcastReceiver onDiscoveryStartedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(intent.getAction())){
                devices.setOnItemClickListener(new DeviceAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int p) {
                        bluetoothAdapter.cancelDiscovery();
                        Intent i = new Intent(getContext(), DeviceActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.putExtra("address", devices.get(p).address);
                        startActivity(i);
                    }
                });
                deviceRecycler.setAdapter(devices);
            }
        }
    };

    BroadcastReceiver onUuidFoundReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(BluetoothDevice.ACTION_UUID.equals(intent.getAction())){
                BluetoothDevice d = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Parcelable[] uuids = intent.getParcelableArrayExtra(BluetoothDevice.EXTRA_UUID);
                Device device = SQLite.select().from(Device.class).where(Device_Table.address.eq(d.getAddress())).querySingle();
                if(uuids != null && device != null){
                    for(Parcelable u : uuids){
                        ParcelUuid _u = new ParcelUuid((android.os.ParcelUuid) u);
                        saveParcelUuid(_u);
                        device.parcelUuidsJson.add(_u.id);
                    }
                    devices.add(device);
                    saveDevice(device);
                }

            }
        }
    };
}
