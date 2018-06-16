package io.eberlein.insane.bluepwn;

import android.app.IntentService;
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
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import org.greenrobot.eventbus.EventBus;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import top.wuhaojie.bthelper.BtHelperClient;
import top.wuhaojie.bthelper.OnSearchDeviceListener;

// todo fix stop scanning when locked

public class ScanService extends IntentService {
    private final IBinder binder = new ScanBinder();

    private LocationManager locationManager;
    private GPSLocationListener locationListener;

    Scan scan;

    private Boolean continuousScanning = false;

    private Context context;
    private ResultReceiver receiver;

    private String prioritize = "gatt";
    private List<Device> toSdpScanDevices;
    private List<Device> toGattScanDevices;

    private BtHelperClient bt;
    private BluetoothAdapter btAdapter;


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
        bt = BtHelperClient.from(context);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!btAdapter.isEnabled()) btAdapter.enable();
    }

    public Boolean isScanning(){
        return btAdapter.isDiscovering();
    }

    public void scan(){
        if(btAdapter.isEnabled()){
            if(!btAdapter.isDiscovering()) {
                bt.searchDevices(new OnSearchDeviceListener() {
                    @Override
                    public void onStartDiscovery() {
                        EventBus.getDefault().post(new EventDiscoveryStarted());
                    }

                    @Override
                    public void onNewDeviceFounded(BluetoothDevice bluetoothDevice) {
                        Device device = Device.get(bluetoothDevice.getAddress());
                        if(device.address.equals("")) device.setValues(bluetoothDevice);
                        if(locationListener.currentLocation != null && !locationListener.currentLocation.isEmpty()) device.locations.add(locationListener.currentLocation.id);
                        device.save();
                        if(!scan.devices.contains(device.address)) scan.devices.add(device.address);
                        scan.save();
                        EventBus.getDefault().post(new EventDeviceDiscovered(device));
                    }

                    @Override
                    public void onSearchCompleted(List<BluetoothDevice> list, List<BluetoothDevice> list1) {
                        EventBus.getDefault().post(new EventDiscoveryFinished());
                    }

                    @Override
                    public void onError(Exception e) {
                        EventBus.getDefault().post(new EventDiscoveryFinished());
                    }
                });
            }
        } else {
            btAdapter.enable();
        }
    }

    public void cancelScanning(){
        btAdapter.cancelDiscovery();
    }

    public ScanService() {
        super("ScanService");
        scan = new Scan();
        toSdpScanDevices = new ArrayList<>();
        toGattScanDevices = new ArrayList<>();
        // notificationManager = getSystemService()
    }

    public void setContinuousScanning(Boolean value){
        this.continuousScanning = value;
    }

    public Boolean getContinuousScanning() {
        return continuousScanning;
    }

    private void registerReceivers(){
        context.registerReceiver(onDiscoveryStartedReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        context.registerReceiver(onUuidFoundReceiver, new IntentFilter(BluetoothDevice.ACTION_UUID));
    }

    private void unregisterReceivers(){
        for(BroadcastReceiver br : Arrays.asList(onDiscoveryStartedReceiver, onUuidFoundReceiver)){
            try { context.unregisterReceiver(br); } catch (RuntimeException e){e.printStackTrace();}
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unregisterReceivers();
        btAdapter.disable();
    }

    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        EventBus.getDefault().register(this);
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    void doScanOnNextDevice(){
        if(toGattScanDevices.size() == 0 && toSdpScanDevices.size() == 0) {
            if(continuousScanning) scan();
            else {EventBus.getDefault().post(new EventToScanDevicesEmpty());}
        } else {
            if(prioritize.equals("gatt")){
                if(toGattScanDevices.size() > 0) { doGattScanOnNextDevice();}
                else if(toSdpScanDevices.size() > 0) doSdpScanOnNextDevice(); }
            else if(prioritize.equals("sdp")){
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
            btAdapter.getRemoteDevice(toGattScanDevices.get(0).address).connectGatt(context, false, new BluetoothGattCallback() {
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                    if(newState == BluetoothProfile.STATE_CONNECTED){
                        gatt.discoverServices();
                    }
                }

                @Override
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    if(status == BluetoothGatt.GATT_SUCCESS){
                        Device device = toGattScanDevices.get(0);
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
                            device.updateServices(service);
                        }
                        device.save();
                        EventBus.getDefault().post(new EventGATTScanFinished());
                        toGattScanDevices.remove(0);
                        doScanOnNextDevice();
                    }
                }
            });
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        receiver = intent.getParcelableExtra("receiver");
    }

    BroadcastReceiver onDiscoveryStartedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(intent.getAction())){
                if(!continuousScanning) scan = new Scan();
                EventBus.getDefault().post(new EventDiscoveryStarted());
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
                    // toSdpScanDevices.remove(0);
                    EventBus.getDefault().post(new EventSDPScanFinished());
                    doScanOnNextDevice();
                }
            }
        }
    };
}
