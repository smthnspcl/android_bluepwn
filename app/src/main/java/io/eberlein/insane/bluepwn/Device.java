package io.eberlein.insane.bluepwn;

import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.paperdb.Paper;

import static io.eberlein.insane.bluepwn.Static.BOND_BONDED;
import static io.eberlein.insane.bluepwn.Static.BOND_BONDING;
import static io.eberlein.insane.bluepwn.Static.BOND_NONE;
import static io.eberlein.insane.bluepwn.Static.BOND_UNKNOWN;
import static io.eberlein.insane.bluepwn.Static.TABLE_DEVICE;
import static io.eberlein.insane.bluepwn.Static.TYPE_CLASSIC;
import static io.eberlein.insane.bluepwn.Static.TYPE_DUAL;
import static io.eberlein.insane.bluepwn.Static.TYPE_LE;
import static io.eberlein.insane.bluepwn.Static.TYPE_UNKNOWN;

// todo extract important information from https://en.wikipedia.org/wiki/List_of_Bluetooth_profiles
// todo script to pull tables https://www.bluetooth.com/specifications/assigned-numbers/service-discovery


public class Device extends DBObject {
    String address;
    String name;
    String manufacturer;
    String bond;
    String type;

    List<String> services;
    List<String> locations;

    public List<Location> getLocations() {
        List<Location> locations = new ArrayList<>();
        for(Scan s : Scan.get()) {
            if (s.devices.contains(address)) locations.addAll(s.getLocations());
        }
        return locations;
    }

    public List<Service> getServices(){
        List<Service> services = new ArrayList<>();
        for(Service u : Service.get()) {if(this.services.contains(u.uuid)) services.add(u);}
        return services;
    }

    void save(){
        Paper.book(TABLE_DEVICE).write(address, this);
    }

    static List<Device> get(){
        List<Device> devices = new ArrayList<>();
        for(String a : Paper.book(TABLE_DEVICE).getAllKeys()) devices.add(Paper.book(TABLE_DEVICE).read(a));
        return devices;
    }

    static Device getExistingOrNew(String address){
        Device d = Paper.book(TABLE_DEVICE).read(address);
        if(d != null) return d;
        return new Device();
    }

    static List<Device> get(List<String> addrs){
        List<Device> devices = new ArrayList<>();
        for(String a : addrs) {
            Device d = Paper.book(TABLE_DEVICE).read(a);
            if(d != null) devices.add(d);
        }
        return devices;
    }

    void updateServices(List<Service> services){
        for(Service s : services) updateServices(s);
    }

    void updateServices(Service service){
        if(!this.services.contains(service.uuid)) this.services.add(service.uuid);
    }

    Device(){
        super(UUID.randomUUID().toString());
        address = "";
        name = "";
        manufacturer = "";
        bond = "";
        type = "";
        services = new ArrayList<>();
        locations = new ArrayList<>();
    }

    public Device(BluetoothDevice device){
        super(UUID.randomUUID().toString());
        setValues(device);
    }

    public Device(String address, String name, String manufacturer, String bond, String type, Date lastModified){
        super(UUID.randomUUID().toString());
        this.address = address;
        this.name = name;
        this.manufacturer = manufacturer;
        this.bond = bond;
        this.type = type;
    }

    void setValues(BluetoothDevice device){
        address = device.getAddress();
        name = device.getName();
        manufacturer = "todo";
        bond = getBondStateAsString(device.getBondState());
        type = getTypeAsString(device.getType());
        locations = new ArrayList<>();
        services = new ArrayList<>();
    }

    private String getTypeAsString(int _type){
        switch (_type){
            case BluetoothDevice.DEVICE_TYPE_UNKNOWN:
                return TYPE_UNKNOWN;
            case BluetoothDevice.DEVICE_TYPE_CLASSIC:
                return TYPE_CLASSIC;
            case BluetoothDevice.DEVICE_TYPE_LE:
                return TYPE_LE;
            case BluetoothDevice.DEVICE_TYPE_DUAL:
                return TYPE_DUAL;
            default:
                return TYPE_UNKNOWN;
        }
    }

    private String getBondStateAsString(int _state){
        switch (_state){
            case BluetoothDevice.BOND_NONE:
                return BOND_NONE;
            case BluetoothDevice.BOND_BONDING:
                return BOND_BONDING;
            case BluetoothDevice.BOND_BONDED:
                return BOND_BONDED;
            default:
                return BOND_UNKNOWN;
        }
    }
}