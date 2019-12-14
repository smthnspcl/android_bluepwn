package io.eberlein.insane.bluepwn.service;

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
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;

import io.eberlein.insane.bluepwn.GPSLocationListener;
import io.eberlein.insane.bluepwn.object.Characteristic;
import io.eberlein.insane.bluepwn.object.Descriptor;
import io.eberlein.insane.bluepwn.object.Device;
import io.eberlein.insane.bluepwn.object.Scan;
import io.eberlein.insane.bluepwn.object.ScanSettings;

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
                    send2UI(ACTION_CODE_DISCOVERY_STOPPED, currentScan.getUuid());
                    break;
                case ACTION_CODE_STOP_SCAN:
                    if(toScanDevices.size() > 0) {toScanDevices = new ArrayList<>(); currentGatt.disconnect();}
                    send2UI(ACTION_CODE_STOP_SCAN, currentScan.getUuid());
                    break;
                case ACTION_CODE_GET_CURRENT_SCAN:
                    send2UI(ACTION_CODE_CURRENT_SCAN, currentScan.getUuid());
            }
        }
    };

    BroadcastReceiver onDiscoveryStartedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!settings.getContinuousScanning() || currentScan == null) currentScan = new Scan();
            send2UI(ACTION_CODE_DISCOVERY_STARTED, null);
        }
    };

    BroadcastReceiver onUuidFoundReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothDevice d = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            Parcelable[] uuids = intent.getParcelableArrayExtra(BluetoothDevice.EXTRA_UUID);
            Device device = Device.getExistingOrNew(d);
            if(uuids != null && device != null){
                for(Parcelable u : uuids){
                    android.os.ParcelUuid __u = (android.os.ParcelUuid) u;
                    io.eberlein.insane.bluepwn.object.Service _u = io.eberlein.insane.bluepwn.object.Service.getExistingOrNew(__u.getUuid().toString());
                    _u.save();
                    device.addService(_u);
                }
                device.save();
                currentScan.addDevice(device);
                send2UI(ACTION_CODE_SERVICE_DISCOVERED, device.getAddress());  // todo check
            }
        }
    };

    BroadcastReceiver onDeviceDiscoveredReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            Device device = Device.getExistingOrNew(bluetoothDevice);
            if (device.getAddress().isEmpty()) device.setValues(bluetoothDevice);
            device.addLocation(locationListener.getCurrentILocation());
            device.save();
            currentScan.addDevice(device);
            toScanDevices.add(device);
            send2UI(ACTION_CODE_DEVICE_DISCOVERED, device.getAddress());
        }
    };

    BroadcastReceiver onDiscoveryFinishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            currentScan.save();
            send2UI(ACTION_CODE_DISCOVERY_FINISHED, currentScan.getUuid());
            doScanOnNextDevice();
        }
    };

    BroadcastReceiver onPairRequestReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(BluetoothDevice.ACTION_PAIRING_REQUEST.equals(intent.getAction())) {
                BluetoothDevice d = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d(getClass().toString(), "got pairing request and " + (settings.getAutoPair() ? "accepted" : "rejected") + " it");
                int pin = intent.getIntExtra("android.bluetooth.device.extra.PAIRING_KEY", 0);
                Log.d(getClass().toString(), "pairing key: " + pin);
                Device ld = Device.getExistingOrNew(d);
                ld.setPin(pin);
                ld.save();
                // d.setPairingConfirmation(settings.autoPair); // only works as system service
            }
        }
    };

    void doScanOnNextDevice(){
        Log.d(getClass().toString(), String.valueOf(toScanDevices.size()));
        while(toScanDevices.size() > 0){
            Device device = toScanDevices.get(0);
            Log.d(this.getClass().toString(), "doScanOnNextDevice");
            if (device.getType().equals(TYPE_LE)) doGattScanOnNextDevice(device);
            else if (device.getType().equals(TYPE_CLASSIC)) doSdpScanOnNextDevice(device);
            else if (device.getType().equals(TYPE_DUAL)) {
                doGattScanOnNextDevice(device);
                doSdpScanOnNextDevice(device);
            }
            toScanDevices.remove(device);
        }
        currentScan.save();
        send2UI(ACTION_CODE_SCANNING_FINISHED, currentScan.getUuid());
    }

    private void doSdpScanOnNextDevice(Device device){
        Log.d(this.getClass().toString(), "trying to sdp scan " + device.getAddress());
        bluetoothAdapter.getRemoteDevice(device.getAddress()).fetchUuidsWithSdp();
    }

    private void unregisterReceivers(){
        Log.d(this.getClass().toString(), "unregistering receivers");
        for(BroadcastReceiver br : broadcastReceivers){
            try { this.unregisterReceiver(br); } catch (RuntimeException e){e.printStackTrace();}
        }
    }

    private void registerReceivers(){
        Log.d(this.getClass().toString(), "registering receivers");
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
        Log.d(this.getClass().toString(), "trying to gatt-scan " + device.getAddress());
        BluetoothDevice d = bluetoothAdapter.getRemoteDevice(device.getAddress());
        if(d == null) return;
        d.connectGatt(this, false, new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);
                currentGatt = gatt;
                if(newState == BluetoothProfile.STATE_DISCONNECTED || newState == BluetoothProfile.STATE_DISCONNECTING || status != BluetoothGatt.GATT_SUCCESS) {
                    Log.d(this.getClass().toString(), "disconnected from " + device.getAddress());
                    send2UI(ACTION_CODE_SERVICE_DISCOVERED, device.getAddress());
                    device.save();
                    currentScan.save();
                    doScanOnNextDevice();
                } else if(newState == BluetoothProfile.STATE_CONNECTED){
                    Log.d(this.getClass().toString(), "connected to " + device.getAddress());
                    if (settings.getGattScanTimeout() != null) timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            currentGatt.disconnect();
                        }
                    }, 10 * 1000);
                    gatt.discoverServices();
                } else if(newState == BluetoothProfile.STATE_CONNECTING) {
                    Log.d(this.getClass().toString(), "connecting to " + device.getAddress());
                } else {
                    Log.d(this.getClass().toString(), device.getAddress() + " BluetoothProfile.STATE_?????? = " + newState);
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                super.onServicesDiscovered(gatt, status);
                if(status != BluetoothGatt.GATT_SUCCESS){
                    Log.d(this.getClass().toString(), "discovery failed on " + device.getAddress());
                } else {
                    Log.d(this.getClass().toString(), "discovered services for " + device.getAddress());
                    List<BluetoothGattService> services = gatt.getServices();
                    for(BluetoothGattService s : services) {
                        io.eberlein.insane.bluepwn.object.Service service = io.eberlein.insane.bluepwn.object.Service.getExistingOrNew(s.getUuid().toString());
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
                    Log.d(this.getClass().toString(), "updated database entry for " + device.getAddress());
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
        unregisterReceivers();
        bluetoothAdapter.disable();
    }

    private void initGPS(){
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new GPSLocationListener();

        locationListener.addOnLocationChangedFunction(new Callable<Void>() {
            @Override
            public Void call() {
                if (currentScan != null && locationListener.getCurrentILocation().isEmpty())
                    currentScan.addLocation(locationListener.getCurrentILocation());
                return null;
            }
        });

        try{ locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } catch (SecurityException e) { e.printStackTrace(); }
    }
}
