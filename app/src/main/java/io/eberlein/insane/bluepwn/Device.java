package io.eberlein.insane.bluepwn;

import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.paperdb.Paper;

// todo extract important information from https://en.wikipedia.org/wiki/List_of_Bluetooth_profiles
// todo script to pull tables https://www.bluetooth.com/specifications/assigned-numbers/service-discovery


public class Device {
    String address;
    String name;
    String manufacturer;
    String bond;
    String type;
    Date lastModified;

    List<String> services;
    List<String> locations;

    public List<Location> getLocations() {
        List<Location> locations = new ArrayList<>();
        for(Scan s : LocalDatabase.getAllScans()) {
            if (s.devices.contains(address)) locations.addAll(s.getLocations());
        }
        return locations;
    }

    public List<Service> getServices(){
        List<Service> services = new ArrayList<>();
        for(Service u : LocalDatabase.getAllServices()) {if(this.services.contains(u.uuid)) services.add(u);}
        return services;
    }

    void save(){
        Paper.book("device").write(address, this);
    }

    void updateServices(List<Service> services){
        for(Service s : services) updateServices(s);
    }

    void updateServices(Service service){
        if(!this.services.contains(service.uuid)) this.services.add(service.uuid);
    }

    Device(){}

    public Device(BluetoothDevice device){
        address = device.getAddress();
        name = device.getName();
        manufacturer = "todo";
        bond = getBondStateAsString(device.getBondState());
        type = getTypeAsString(device.getType());
        lastModified = new Date();
        locations = new ArrayList<>();
        services = new ArrayList<>();
    }

    public Device(String address, String name, String manufacturer, String bond, String type, Date lastModified){
        this.address = address;
        this.name = name;
        this.manufacturer = manufacturer;
        this.bond = bond;
        this.type = type;
        this.lastModified = lastModified;
    }

    private String getTypeAsString(int _type){
        switch (_type){
            case BluetoothDevice.DEVICE_TYPE_UNKNOWN:
                return "unknown";
            case BluetoothDevice.DEVICE_TYPE_CLASSIC:
                return "classic";
            case BluetoothDevice.DEVICE_TYPE_LE:
                return "le";
            case BluetoothDevice.DEVICE_TYPE_DUAL:
                return "dual";
            default:
                return "none";
        }
    }

    private String getBondStateAsString(int _state){
        switch (_state){
            case BluetoothDevice.BOND_NONE:
                return "none";
            case BluetoothDevice.BOND_BONDING:
                return "bonding";
            case BluetoothDevice.BOND_BONDED:
                return "bonded";
            default:
                return "unknown";
        }
    }
}