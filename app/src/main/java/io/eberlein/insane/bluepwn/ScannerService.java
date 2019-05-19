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
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;

import static android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_FINISHED;
import static android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_STARTED;
import static android.bluetooth.BluetoothDevice.ACTION_FOUND;
import static android.bluetooth.BluetoothDevice.ACTION_PAIRING_REQUEST;
import static android.bluetooth.BluetoothDevice.ACTION_UUID;
import static io.eberlein.insane.bluepwn.Static.TYPE_CLASSIC;
import static io.eberlein.insane.bluepwn.Static.TYPE_DUAL;
import static io.eberlein.insane.bluepwn.Static.TYPE_LE;
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
import static io.eberlein.insane.bluepwn.Static.action.scanner.codes.ACTION_CODE_SERVICE_DISCOVERED;
import static io.eberlein.insane.bluepwn.Static.action.scanner.codes.ACTION_CODE_SERVICE_INITIALIZED;
import static io.eberlein.insane.bluepwn.Static.action.scanner.codes.ACTION_CODE_START_DISCOVERY;
import static io.eberlein.insane.bluepwn.Static.action.scanner.codes.ACTION_CODE_STOP_DISCOVERY;
import static io.eberlein.insane.bluepwn.Static.action.scanner.codes.ACTION_CODE_STOP_SCAN;


public class ScannerService extends Service {
    private BluetoothAdapter bluetoothAdapter;
    private LocationManager locationManager;
    private GPSLocationListener locationListener;
    private ScanSettings settings;
    private BluetoothGatt currentGatt;
    private List<Device> toScanDevices;
    private Scan currentScan;
    private Timer timer = new Timer();

    BroadcastReceiver uiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            currentScan.save();
            switch (intent.getStringExtra(ACTION_CODE_KEY)){
                case ACTION_CODE_START_DISCOVERY:
                    if(!bluetoothAdapter.isEnabled()) bluetoothAdapter.enable();  // if userStupid
                    if(!bluetoothAdapter.isDiscovering()) bluetoothAdapter.startDiscovery();
                    // send2UI happens in onDiscoveryStartedReceiver
                    break;
                case ACTION_CODE_STOP_DISCOVERY:
                    if(bluetoothAdapter.isDiscovering()) bluetoothAdapter.cancelDiscovery();
                    send2UI(ACTION_CODE_DISCOVERY_STOPPED, currentScan.uuid);
                    break;
                case ACTION_CODE_STOP_SCAN:
                    if(toScanDevices.size() > 0) {toScanDevices = new ArrayList<>(); currentGatt.disconnect();}
                    send2UI(ACTION_CODE_STOP_SCAN, currentScan.uuid);
                    break;
                case ACTION_CODE_GET_CURRENT_SCAN:
                    send2UI(ACTION_CODE_CURRENT_SCAN, currentScan.uuid);
            }
        }
    };

    BroadcastReceiver onDiscoveryStartedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(!settings.continuousScanning || currentScan == null) currentScan = new Scan();
            send2UI(ACTION_CODE_DISCOVERY_STARTED, null);
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
                currentScan.addDevice(device);
                send2UI(ACTION_CODE_SERVICE_DISCOVERED, device.address);  // todo check
            }
        }
    };

    BroadcastReceiver onDeviceDiscoveredReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            Device device = Device.getExistingOrNew(bluetoothDevice.getAddress());
            device.setValues(bluetoothDevice);
            if(device.address.isEmpty()) device.setValues(bluetoothDevice);
            if(locationListener.currentLocation != null && !locationListener.currentLocation.isEmpty()) device.locations.add(locationListener.currentLocation.uuid);
            device.save();
            currentScan.addDevice(device);
            currentScan.save();
            toScanDevices.add(device);
            send2UI(ACTION_CODE_DEVICE_DISCOVERED, device.address);
        }
    };

    BroadcastReceiver onDiscoveryFinishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            currentScan.save();
            send2UI(ACTION_CODE_DISCOVERY_FINISHED, currentScan.uuid);
            doScanOnNextDevice();
        }
    };

    BroadcastReceiver onPairRequestReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(BluetoothDevice.ACTION_PAIRING_REQUEST.equals(intent.getAction())) {
                BluetoothDevice d = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                d.setPairingConfirmation(settings.autoPair);
            }
        }
    };

    void doScanOnNextDevice(){
        Log.log(getClass(), String.valueOf(toScanDevices.size()));
        if(toScanDevices.size() > 0){
            Device device = toScanDevices.get(0);
            Log.log(this.getClass(), "doScanOnNextDevice");
            if(device.type.equals(TYPE_LE)) doGattScanOnNextDevice(device);
            else if(device.type.equals(TYPE_CLASSIC)) doSdpScanOnNextDevice(device);
            else if(device.type.equals(TYPE_DUAL)) {doGattScanOnNextDevice(device); doSdpScanOnNextDevice(device);}
            toScanDevices.remove(device);
        } else {
            currentScan.save();
            send2UI(ACTION_CODE_SCANNING_FINISHED, currentScan.uuid);
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
        registerReceiver(uiReceiver, new IntentFilter(ACTION_SCANNER_CMD));
        registerReceiver(onDeviceDiscoveredReceiver, new IntentFilter(ACTION_FOUND));
        registerReceiver(onDiscoveryFinishedReceiver, new IntentFilter(ACTION_DISCOVERY_FINISHED));
        registerReceiver(onDiscoveryStartedReceiver, new IntentFilter(ACTION_DISCOVERY_STARTED));
        registerReceiver(onUuidFoundReceiver, new IntentFilter(ACTION_UUID));
        registerReceiver(onPairRequestReceiver, new IntentFilter(ACTION_PAIRING_REQUEST));
    }

    private final BroadcastReceiver[] broadcastReceivers = {
            onDeviceDiscoveredReceiver,
            onDiscoveryStartedReceiver,
            onUuidFoundReceiver,
            onDiscoveryFinishedReceiver,
            uiReceiver,
            onPairRequestReceiver
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
                    send2UI(ACTION_CODE_SERVICE_DISCOVERED, device.address);
                    device.save();
                    currentScan.save();
                    doScanOnNextDevice();
                } else if(newState == BluetoothProfile.STATE_CONNECTED){
                    Log.log(this.getClass(), "connected to " + device.address);
                    if(settings.gattScanTimeout != null) timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            currentGatt.disconnect();
                        }
                    }, 10 * 1000);
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
        return Service.START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.onCreate(this.getClass());
        settings = ScanSettings.getExistingOrNew();
        currentScan = new Scan();
        toScanDevices = new ArrayList<>();
        initGPS();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!bluetoothAdapter.isEnabled()) bluetoothAdapter.enable();
        registerReceivers();
        send2UI(ACTION_CODE_SERVICE_INITIALIZED, null);
    }

    private void send2UI(String key, @Nullable String data){
        Intent s = new Intent(ACTION_SCANNER_INFO);
        s.putExtra(ACTION_CODE_KEY, key);
        if(data != null) s.putExtra(ACTION_DATA_KEY, data);
        sendBroadcast(s);
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
