package io.eberlein.insane.bluepwn;

import android.app.Service;
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
import android.location.LocationManager;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static io.eberlein.insane.bluepwn.Static.ACTION_ALREADY_SCANNING;
import static io.eberlein.insane.bluepwn.Static.ACTION_DATA_KEY;
import static io.eberlein.insane.bluepwn.Static.ACTION_DEVICE_DISCOVERED;
import static io.eberlein.insane.bluepwn.Static.ACTION_DISCOVERY_ALREADY_STOPPED;
import static io.eberlein.insane.bluepwn.Static.ACTION_DISCOVERY_FINISHED;
import static io.eberlein.insane.bluepwn.Static.ACTION_DISCOVERY_STARTED;
import static io.eberlein.insane.bluepwn.Static.ACTION_DISCOVERY_STOPPED;
import static io.eberlein.insane.bluepwn.Static.ACTION_SCANNER_INITIALIZED;
import static io.eberlein.insane.bluepwn.Static.ACTION_SCAN_FINISHED;
import static io.eberlein.insane.bluepwn.Static.ACTION_SCAN_STARTED;
import static io.eberlein.insane.bluepwn.Static.ACTION_SCAN_STOPPED;
import static io.eberlein.insane.bluepwn.Static.ACTION_SERVICE_DISCOVERED;
import static io.eberlein.insane.bluepwn.Static.ACTION_START_SCAN;
import static io.eberlein.insane.bluepwn.Static.ACTION_STOP_DISCOVERY;
import static io.eberlein.insane.bluepwn.Static.ACTION_STOP_SCAN;
import static io.eberlein.insane.bluepwn.Static.TYPE_CLASSIC;
import static io.eberlein.insane.bluepwn.Static.TYPE_DUAL;
import static io.eberlein.insane.bluepwn.Static.TYPE_LE;


public class ScannerService extends Service {
    private BluetoothAdapter bluetoothAdapter;
    private LocationManager locationManager;
    private GPSLocationListener locationListener;
    private boolean continuousScanning = false;
    private boolean scanning = false;
    private BluetoothGatt currentGatt;
    private List<Device> toScanDevices;
    private Scan currentScan;

    BroadcastReceiver startScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(!bluetoothAdapter.isEnabled()) bluetoothAdapter.enable();
            if(bluetoothAdapter.isDiscovering())send2UI(ACTION_ALREADY_SCANNING, null);
            else {bluetoothAdapter.startDiscovery(); send2UI(ACTION_SCAN_STARTED, null);}
            Log.log(this.getClass(), "scanning: " + String.valueOf(bluetoothAdapter.isDiscovering()));
        }
    };

    BroadcastReceiver stopDiscoveryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(bluetoothAdapter.isDiscovering()) { bluetoothAdapter.cancelDiscovery(); send2UI(ACTION_DISCOVERY_STOPPED, null);}
            else send2UI(ACTION_DISCOVERY_ALREADY_STOPPED, null);
            Log.log(this.getClass(), "stopped discovery");
        }
    };

    BroadcastReceiver stopScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            toScanDevices = new ArrayList<>();
            if(currentGatt != null) {currentGatt.disconnect(); currentGatt = null;}
            Log.log(this.getClass(), "stopped scanning");
            send2UI(ACTION_SCAN_STOPPED, null); // todo send list of all addresses
        }
    };

    BroadcastReceiver onDiscoveryStartedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(!continuousScanning) currentScan = new Scan();
            send2UI(ACTION_DISCOVERY_STARTED, null);
        }
    };

    BroadcastReceiver onUuidFoundReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothDevice d = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            Parcelable[] uuids = intent.getParcelableArrayExtra(BluetoothDevice.EXTRA_UUID);
            Device device = Device.getExistingOrNew(d.getAddress());
            if(uuids != null && device != null){
                for(Parcelable u : uuids){
                    android.os.ParcelUuid __u = (android.os.ParcelUuid) u;
                    io.eberlein.insane.bluepwn.Service _u = io.eberlein.insane.bluepwn.Service.getExistingOrNew(__u.getUuid().toString());
                    _u.save();
                    if(!device.services.contains(_u.uuid)) device.services.add(_u.uuid);
                }
                device.save();
                send2UI(ACTION_SERVICE_DISCOVERED, device.address);  // todo check
            }
        }
    };

    BroadcastReceiver onDeviceDiscoveredReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            Device device = Device.getExistingOrNew(bluetoothDevice.getAddress());
            device.populateIfEmpty(bluetoothDevice);
            if(device.address.isEmpty()) device.setValues(bluetoothDevice);
            if(locationListener.currentLocation != null && !locationListener.currentLocation.isEmpty()) device.locations.add(locationListener.currentLocation.uuid);
            device.save();
            toScanDevices.add(device);
            send2UI(ACTION_DEVICE_DISCOVERED, device.address);
        }
    };

    BroadcastReceiver onDiscoveryFinishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            send2UI(ACTION_DISCOVERY_FINISHED, currentScan.uuid);
            doScanOnNextDevice();
        }
    };

    void doScanOnNextDevice(){
        if(toScanDevices.size() > 0){
            Log.log(this.getClass(), "doScanOnNextDevice");
            Device device = toScanDevices.get(0);
            if(device.type.equals(TYPE_LE)) doGattScanOnNextDevice(device);
            else if(device.type.equals(TYPE_CLASSIC)) doSdpScanOnNextDevice(device);
            else if(device.type.equals(TYPE_DUAL)) {doGattScanOnNextDevice(device); doSdpScanOnNextDevice(device);}
            toScanDevices.remove(device);
        } else {
            send2UI(ACTION_SCAN_FINISHED, null);
        }
    }

    private void doSdpScanOnNextDevice(Device device){
        Log.log(this.getClass(), "trying to sdp scan " + device.address);
        bluetoothAdapter.getRemoteDevice(device.address).fetchUuidsWithSdp();
    }

    private void unregisterReceivers(){
        Log.log(this.getClass(), "unregistering receivers");
        for(BroadcastReceiver br : broadcastReceivers){
            try { this.unregisterReceiver(br); } catch (RuntimeException e){e.printStackTrace();}
        }
    }

    private void registerReceivers(){
        Log.log(this.getClass(), "registering receivers");
        registerReceiver(onDiscoveryStartedReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        registerReceiver(onUuidFoundReceiver, new IntentFilter(BluetoothDevice.ACTION_UUID));
        registerReceiver(onDeviceDiscoveredReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        registerReceiver(onDiscoveryFinishedReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
        registerReceiver(startScanReceiver, new IntentFilter(ACTION_START_SCAN));
        registerReceiver(stopScanReceiver, new IntentFilter(ACTION_STOP_SCAN));
        registerReceiver(stopDiscoveryReceiver, new IntentFilter(ACTION_STOP_DISCOVERY));
    }

    private final BroadcastReceiver[] broadcastReceivers = {
            onDeviceDiscoveredReceiver,
            onDiscoveryStartedReceiver,
            onUuidFoundReceiver,
            onDiscoveryFinishedReceiver,
            startScanReceiver,
            stopScanReceiver,
            stopDiscoveryReceiver
    };

    private void doGattScanOnNextDevice(Device device){
        Log.log(this.getClass(), "trying to gatt-scan " + device.address);
        BluetoothDevice d = bluetoothAdapter.getRemoteDevice(device.address);
        if(d == null) return;
        d.connectGatt(this, false, new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);
                currentGatt = gatt;
                if(newState == BluetoothProfile.STATE_DISCONNECTED || newState == BluetoothProfile.STATE_DISCONNECTING || status != BluetoothGatt.GATT_SUCCESS) {
                    Log.log(this.getClass(), "disconnected from " + device.address);
                    send2UI(ACTION_SERVICE_DISCOVERED, device.address);
                } else if(newState == BluetoothProfile.STATE_CONNECTED){
                    Log.log(this.getClass(), "connected to " + device.address);
                    gatt.discoverServices();
                } else if(newState == BluetoothProfile.STATE_CONNECTING) {
                    Log.log(this.getClass(), "connecting to " + device.address);
                } else {
                    Log.log(this.getClass(), device.address + " BluetoothProfile.STATE_?????? = " + String.valueOf(newState));
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                super.onServicesDiscovered(gatt, status);
                if(status != BluetoothGatt.GATT_SUCCESS){
                    Log.log(this.getClass(), "discovery failed on " + device.address);
                } else {
                    Log.log(this.getClass(), "discovered services for " + device.address);
                    List<BluetoothGattService> services = gatt.getServices();
                    for(BluetoothGattService s : services) {
                        io.eberlein.insane.bluepwn.Service service = io.eberlein.insane.bluepwn.Service.getExistingOrNew(s.getUuid().toString());
                        for(BluetoothGattCharacteristic c : s.getCharacteristics()){
                            Characteristic characteristic = Characteristic.getExistingOrNew(c);
                            for(BluetoothGattDescriptor d : c.getDescriptors()){
                                Descriptor descriptor = Descriptor.getExistingOrNew(d);
                                characteristic.updateDescriptors(descriptor);
                                descriptor.save();
                            }
                            service.updateCharacteristics(characteristic);
                            characteristic.save();
                        }
                        device.updateServices(service);
                        service.save();
                    }
                    device.save();
                    Log.log(this.getClass(), "updated database entry for " + device.address);
                    gatt.disconnect();
                }
            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;  // is this correct
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.onCreate(this.getClass());
        toScanDevices = new ArrayList<>();
        initGPS();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.enable();
        registerReceivers();
        send2UI(ACTION_SCANNER_INITIALIZED, null);
    }

    private void send2UI(String action, @Nullable String data){
        Intent s = new Intent(action);
        if(data != null) s.putExtra(ACTION_DATA_KEY, data);
        this.sendBroadcast(s);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.onDestroy(this.getClass());
        unregisterReceivers();
        bluetoothAdapter.disable();
    }

    private void initGPS(){
        Log.initGPS(this.getClass());
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new GPSLocationListener();

        locationListener.onLocationChangedFunctions.add(new Callable<Void>() {
            @Override
            public Void call() {
                if(currentScan != null && locationListener.currentLocation != null && !locationListener.currentLocation.isEmpty()) currentScan.locations.add(locationListener.currentLocation.uuid);
                return null;
            }
        });

        try{ locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } catch (SecurityException e) { e.printStackTrace(); }
        Log.initGPSFinished(this.getClass());
    }
}
