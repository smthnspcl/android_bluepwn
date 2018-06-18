package io.eberlein.insane.bluepwn;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
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
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import static io.eberlein.insane.bluepwn.Static.TYPE_CLASSIC;
import static io.eberlein.insane.bluepwn.Static.TYPE_DUAL;
import static io.eberlein.insane.bluepwn.Static.TYPE_LE;


public class BluetoothFragment extends Fragment {
    @BindView(R.id.scanBtn) FloatingActionButton scanBtn;
    @BindView(R.id.devicesRecycler) RecyclerView deviceRecycler;
    @BindView(R.id.continuousScanningCheckbox) CheckBox continuousScanningCheckbox;

    @OnClick(R.id.scanBtn)
    void scanBtnClicked(){
        if(btAdapter.isEnabled()){
            if(btAdapter.isDiscovering()) {
                continuousScanningCheckbox.setChecked(false);
                EventBus.getDefault().post(new EventStopScanning());
            } else {
                EventBus.getDefault().post(new EventStartScanning());
            }
        } else {
            btAdapter.enable();
        }
    }

    private DeviceAdapter devices;
    private Scan scan;

    private String prioritize = TYPE_LE;
    private List<Device> toSdpScanDevices;
    private List<Device> toGattScanDevices;

    private BluetoothAdapter btAdapter;

    private LocationManager locationManager;
    private GPSLocationListener locationListener;

    BroadcastReceiver onDiscoveryStartedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(intent.getAction())){
                if(!continuousScanningCheckbox.isChecked()) scan = new Scan();
                scanBtn.setImageResource(R.drawable.ic_clear_white_48dp);
            }
        }
    };

    BroadcastReceiver onUuidFoundReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(BluetoothDevice.ACTION_UUID.equals(intent.getAction())){
                BluetoothDevice d = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Parcelable[] uuids = intent.getParcelableArrayExtra(BluetoothDevice.EXTRA_UUID);
                Device device = Device.get(d.getAddress());
                if(uuids != null && device != null){
                    for(Parcelable u : uuids){
                        android.os.ParcelUuid __u = (android.os.ParcelUuid) u;
                        Service _u = Service.getExistingOrNew(__u.getUuid().toString());
                        _u.save();
                        if(!device.services.contains(_u.uuid)) device.services.add(_u.uuid);
                    }
                    device.save();
                    EventBus.getDefault().post(new EventSDPScanFinished(device));

                }
            }
        }
    };

    BroadcastReceiver onDeviceDiscoveredReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Device device = Device.get(bluetoothDevice.getAddress());
                device.populateIfEmpty(bluetoothDevice);
                if(device.address.isEmpty()) device.setValues(bluetoothDevice);
                if(locationListener.currentLocation != null && !locationListener.currentLocation.isEmpty()) device.locations.add(locationListener.currentLocation.id);
                device.save();
                scan.save();
                EventBus.getDefault().post(new EventDeviceDiscovered(device));
            }
        }
    };

    BroadcastReceiver onDiscoveryFinishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            doScanOnNextDevice();
        }
    };

    private final BroadcastReceiver[] broadcastReceivers = {
            onDeviceDiscoveredReceiver,
            onDiscoveryStartedReceiver,
            onUuidFoundReceiver,
            onDiscoveryFinishedReceiver
    };

    @Subscribe
    public void onDeviceDiscovered(EventDeviceDiscovered e){
        if(e.device.type.equals(TYPE_CLASSIC) || e.device.type.equals(TYPE_DUAL)) toSdpScanDevices.add(e.device);
        if(e.device.type.equals(TYPE_LE) || e.device.type.equals(TYPE_DUAL)) toGattScanDevices.add(e.device);
        if(!scan.devices.contains(e.device.address)) scan.devices.add(e.device.address);
        devices.add(e.device);
    }

    @Subscribe
    public void onToScanDevicesEmpty(EventToScanDevicesEmpty e){
        if(continuousScanningCheckbox.isChecked()){
            scan();
        } else {
            scanBtn.setImageResource(R.drawable.ic_update_white_48dp);
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onStartScanning(EventStartScanning e){
        scan();
    }

    @Subscribe
    public void onStopScanning(EventStopScanning e){
        if(btAdapter.isDiscovering()) btAdapter.cancelDiscovery();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGATTScanFinished(EventGATTScanFinished e){
        devices.add(e.device);
        toGattScanDevices.remove(0);
        doScanOnNextDevice();
    }

    @Subscribe
    public void onSDPScanFinished(EventSDPScanFinished e){
        devices.add(e.device);
        if(toSdpScanDevices.size() >= 1) toSdpScanDevices.remove(0);
        doScanOnNextDevice();
    }

    private void initGPS(){
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new GPSLocationListener();

        locationListener.onLocationChangedFunctions.add(new Callable<Void>() {
            @Override
            public Void call() {
                if(scan != null && locationListener.currentLocation != null && !locationListener.currentLocation.isEmpty()) scan.locations.add(locationListener.currentLocation.id);
                return null;
            }
        });

        try{ locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } catch (SecurityException e) { e.printStackTrace(); }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toGattScanDevices = new ArrayList<>();
        toSdpScanDevices = new ArrayList<>();
        EventBus.getDefault().register(this);
        requestPermissions();
        initGPS();
        registerReceivers();
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!btAdapter.isEnabled()) btAdapter.enable();
        devices = new DeviceAdapter();
        scan = new Scan();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(scan == null) {scan = new Scan();}
        devices.addAll(scan.getDevices());
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_bluetooth, container, false);
        ButterKnife.bind(this, v);
        scanBtn.setImageResource(R.drawable.ic_update_white_48dp);
        deviceRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        deviceRecycler.setAdapter(devices);
        devices.setOnItemClickListener(new DeviceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int p) {
                Intent i = new Intent(getContext(), DeviceActivity.class);
                i.putExtra("address", devices.get(p).address);
                startActivity(i);
            }
        });
        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unregisterReceivers();
        btAdapter.disable();
    }

    private void requestPermissions(){
        if(!(ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED))
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.BLUETOOTH}, Static.BLUETOOTH_RESULT);
        if(!(ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED))
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Static.LOCATION_RESULT);
    }

    void doScanOnNextDevice(){
        if(toGattScanDevices.size() == 0 && toSdpScanDevices.size() == 0) {
            if(continuousScanningCheckbox.isChecked()) scan();
            else {EventBus.getDefault().post(new EventToScanDevicesEmpty());}
        } else {
            if(prioritize.equals(TYPE_LE)){
                if(toGattScanDevices.size() > 0) { doGattScanOnNextDevice();}
                else if(toSdpScanDevices.size() > 0) doSdpScanOnNextDevice(); }
            else if(prioritize.equals(TYPE_CLASSIC)){
                if(toSdpScanDevices.size() > 0) doSdpScanOnNextDevice();
                else if(toGattScanDevices.size() > 0) doGattScanOnNextDevice(); }
        }

    }

    private void doSdpScanOnNextDevice(){
        if(toSdpScanDevices.size() > 0) {
            btAdapter.getRemoteDevice(toSdpScanDevices.get(0).address).fetchUuidsWithSdp();
        }
    }

    private void doGattScanOnNextDevice(){
        if(toGattScanDevices.size() > 0) {
            System.out.println("gatt scan");
            btAdapter.getRemoteDevice(toGattScanDevices.get(0).address).connectGatt(getContext(), false, new BluetoothGattCallback() {
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                    if(newState == BluetoothProfile.STATE_CONNECTED){
                        gatt.discoverServices();
                    }
                }

                @Override
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    if(status == BluetoothGatt.GATT_SUCCESS){
                        Device device = Device.get(gatt.getDevice().getAddress());
                        for(BluetoothGattService s : gatt.getServices()){
                            Service service = Service.getExistingOrNew(s.getUuid().toString());
                            for(BluetoothGattCharacteristic c : s.getCharacteristics()){
                                Characteristic characteristic = Characteristic.getExistingOrNew(c);
                                if(!service.characteristics.contains(characteristic.uuid)) service.characteristics.add(characteristic.uuid);
                                for(BluetoothGattDescriptor d : c.getDescriptors()){
                                    Descriptor descriptor = Descriptor.getExistingOrNew(d);
                                    if(!characteristic.descriptors.contains(descriptor.uuid)) characteristic.descriptors.add(descriptor.uuid);
                                    descriptor.save();
                                }
                                characteristic.save();
                            }
                            service.save();
                            if(!device.services.contains(service.uuid)) device.services.add(service.uuid);
                        }
                        device.save();
                        EventBus.getDefault().post(new EventGATTScanFinished(device));
                    }
                }
            });
        }
    }

    private void registerReceivers(){
        getContext().registerReceiver(onDiscoveryStartedReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        getContext().registerReceiver(onUuidFoundReceiver, new IntentFilter(BluetoothDevice.ACTION_UUID));
        getContext().registerReceiver(onDeviceDiscoveredReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        getContext().registerReceiver(onDiscoveryFinishedReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
    }

    private void unregisterReceivers(){
        for(BroadcastReceiver br : broadcastReceivers){
            try { getContext().unregisterReceiver(br); } catch (RuntimeException e){e.printStackTrace();}
        }
    }

    public void scan(){
        if(btAdapter.isEnabled() && !btAdapter.isDiscovering()) btAdapter.startDiscovery();
    }
}
