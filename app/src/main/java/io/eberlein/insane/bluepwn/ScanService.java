package io.eberlein.insane.bluepwn;

import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import io.paperdb.Paper;

public class ScanService extends IntentService {
    private final IBinder binder = new ScanBinder();

    private BluetoothAdapter bluetoothAdapter;
    private LocationManager locationManager;
    private GPSLocationListener locationListener;
    Scan scan;
    private Boolean continuousScanning = false;
    private Context context;

    List<Callable<Void>> deviceDiscoveredCallableList;
    List<Callable<Void>> discoveryFinishedCallableList;
    List<Callable<Void>> discoveryStartedCallableList;
    List<Callable<Void>> uuidFoundCallableList;

    public class ScanBinder extends Binder {
        ScanService getService() {
            return ScanService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private void initGPS(){
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
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
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        initGPS();
        registerReceivers();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!bluetoothAdapter.isEnabled()) bluetoothAdapter.enable();
    }

    public Boolean isScanning(){
        return bluetoothAdapter.isDiscovering();
    }

    public void scan(){
        if(!bluetoothAdapter.isDiscovering()) {bluetoothAdapter.startDiscovery(); Toast.makeText(context, "scanning", Toast.LENGTH_SHORT).show();}
    }

    public void cancelScanning(){
        bluetoothAdapter.cancelDiscovery();
        Toast.makeText(context, "stopped scanning", Toast.LENGTH_SHORT).show();
    }

    public ScanService() {
        super("ScanService");
        deviceDiscoveredCallableList = new ArrayList<>();
        discoveryFinishedCallableList = new ArrayList<>();
        discoveryStartedCallableList = new ArrayList<>();
        uuidFoundCallableList = new ArrayList<>();
        scan = new Scan();
        // notificationManager = getSystemService()
    }

    public void setContinuousScanning(Boolean value){
        this.continuousScanning = value;
    }

    public Boolean getContinuousScanning() {
        return continuousScanning;
    }

    private void registerReceivers(){
        context.registerReceiver(onDeviceDiscoveredReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        context.registerReceiver(onDiscoveryFinishedReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
        context.registerReceiver(onDiscoveryStartedReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        context.registerReceiver(onUuidFoundReceiver, new IntentFilter(BluetoothDevice.ACTION_UUID));
    }

    private void unregisterReceivers(){
        for(BroadcastReceiver br : Arrays.asList(onDeviceDiscoveredReceiver, onDiscoveryFinishedReceiver, onDiscoveryStartedReceiver, onUuidFoundReceiver)){
            try { context.unregisterReceiver(br); } catch (RuntimeException e){e.printStackTrace();}
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceivers();
        bluetoothAdapter.disable();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private void saveDevice(Device device){
        Paper.book("device").write(device.address, device);
    }

    private void saveParcelUuid(ParcelUuid parcelUuid){
       Paper.book("parcelUuid").write(parcelUuid.uuid.toString(), parcelUuid);
    }

    private void saveScan(Scan scan){
        Paper.book("scan").write(scan.id, scan);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    BroadcastReceiver onDeviceDiscoveredReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(BluetoothDevice.ACTION_FOUND.equals(intent.getAction())){
                Device d = new Device(intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE));
                saveDevice(d);
                if(!scan.devices.contains(d.address)) scan.devices.add(d.address);
                saveScan(scan);
                try{for(Callable<Void> c : deviceDiscoveredCallableList) c.call();}catch (Exception e) {e.printStackTrace();}
            }
        }
    };

    BroadcastReceiver onDiscoveryFinishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())){
                saveScan(scan);
                for(Device d : scan.getDevices()) bluetoothAdapter.getRemoteDevice(d.address).fetchUuidsWithSdp();
                if(continuousScanning) bluetoothAdapter.startDiscovery();
                else {
                    try{for(Callable<Void> c : discoveryFinishedCallableList) c.call();}catch (Exception e) {e.printStackTrace();}
                }
            }
        }
    };

    BroadcastReceiver onDiscoveryStartedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(intent.getAction())){
                if(!continuousScanning) scan = new Scan();
                try{for(Callable<Void> c : discoveryStartedCallableList) c.call();}catch (Exception e) {e.printStackTrace();}
            }
        }
    };

    BroadcastReceiver onUuidFoundReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(BluetoothDevice.ACTION_UUID.equals(intent.getAction())){
                BluetoothDevice d = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Parcelable[] uuids = intent.getParcelableArrayExtra(BluetoothDevice.EXTRA_UUID);
                Device device = Paper.book("device").read(d.getAddress());
                if(uuids != null && device != null){
                    for(Parcelable u : uuids){
                        android.os.ParcelUuid __u = (android.os.ParcelUuid) u;
                        ParcelUuid _u = Paper.book("parcelUuid").read(__u.toString(), new ParcelUuid(__u));
                        saveParcelUuid(_u);
                        if(!device.parcelUuids.contains(_u.uuid.toString())) device.parcelUuids.add(_u.uuid.toString());
                    }
                    saveDevice(device);
                }
                try{for(Callable<Void> c : uuidFoundCallableList) c.call();}catch (Exception e){e.printStackTrace();}
            }
        }
    };
}
