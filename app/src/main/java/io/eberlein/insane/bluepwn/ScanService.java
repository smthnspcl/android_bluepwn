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

import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;


public class ScanService extends IntentService {
    private final IBinder binder = new ScanBinder();

    private BluetoothAdapter bluetoothAdapter;
    private LocationManager locationManager;
    private GPSLocationListener locationListener;
    private Scan scan;
    private DatabaseDefinition db;
    private Boolean continuousScanning = false;
    private Context context;

    List<Callable<Void>> deviceDiscoveredCallableList;
    List<Callable<Void>> discoveryFinishedCallableList;
    List<Callable<Void>> discoveryStartedCallableList;

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
            public Void call() throws Exception {
                if(scan != null) scan.locationsIds.add(locationListener.currentLocation.id);
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
        db = FlowManager.getDatabase(LocalDatabase.class);
        if(!bluetoothAdapter.isEnabled()) bluetoothAdapter.enable();
    }

    public Scan getScan() {
        return scan;
    }

    public Boolean isScanning(){
        return bluetoothAdapter.isDiscovering();
    }

    public void scan(){
        if(!bluetoothAdapter.isDiscovering()) {bluetoothAdapter.startDiscovery(); Toast.makeText(context, "scanning", Toast.LENGTH_SHORT).show();}
    }

    public void cancelScanning(){
        bluetoothAdapter.cancelDiscovery();
    }

    public ScanService() {
        super("ScanService");
        deviceDiscoveredCallableList = new ArrayList<>();
        discoveryFinishedCallableList = new ArrayList<>();
        discoveryStartedCallableList = new ArrayList<>();
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
        context.unregisterReceiver(onDiscoveryStartedReceiver);
        context.unregisterReceiver(onDiscoveryFinishedReceiver);
        context.unregisterReceiver(onDiscoveryStartedReceiver);
        context.unregisterReceiver(onUuidFoundReceiver);
    }

    @Override
    public void onDestroy() {
        unregisterReceivers();
        bluetoothAdapter.disable();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private void saveDevice(Device device){
        db.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) { device.save(databaseWrapper); }
        }).build().execute();
    }

    private void saveParcelUuid(ParcelUuid parcelUuid){
        db.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) { parcelUuid.save(databaseWrapper); }
        }).build().execute();
    }

    private void saveScan(Scan scan){
        db.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                scan.save(databaseWrapper);
            }
        }).build().execute();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    BroadcastReceiver onDeviceDiscoveredReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(BluetoothDevice.ACTION_FOUND.equals(intent.getAction())){
                try{for(Callable<Void> c : deviceDiscoveredCallableList) c.call();}catch (Exception e) {e.printStackTrace();}
                Device d = new Device(intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE));
                if(locationListener.currentLocation == null) d.locationIdsJson.add(new Location());
                else d.locationIdsJson.add(locationListener.currentLocation.id);
                saveDevice(d);
                if(!scan.devices.contains(d)) scan.devices.add(d);
            }
        }
    };

    BroadcastReceiver onDiscoveryFinishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())){
                try{for(Callable<Void> c : discoveryFinishedCallableList) c.call();}catch (Exception e) {e.printStackTrace();}
                saveScan(scan);
                for(Device d : scan.devices) bluetoothAdapter.getRemoteDevice(d.address).fetchUuidsWithSdp();
                if(continuousScanning) bluetoothAdapter.startDiscovery();
            }
        }
    };

    BroadcastReceiver onDiscoveryStartedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(intent.getAction())){
                try{for(Callable<Void> c : discoveryStartedCallableList) c.call();}catch (Exception e) {e.printStackTrace();}
                scan = new Scan();
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
                        ParcelUuid _u = ParcelUuid.getExistingOrNew((android.os.ParcelUuid) u);
                        if(!device.parcelUuidsJson.contains(_u.id)) device.parcelUuidsJson.add(_u.id);
                        if(SQLite.select().from(ParcelUuid.class).where(ParcelUuid_Table.id.eq(_u.id)).querySingle() == null) saveParcelUuid(_u);
                    }
                    if(!scan.devices.contains(device)) scan.devices.add(device);
                    saveDevice(device);
                }
            }
        }
    };
}
