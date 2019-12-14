package io.eberlein.insane.bluepwn.object;

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
    private Integer pin;
    private String address;
    private String name;
    private String manufacturer;
    private String bond;
    private String type;

    private List<String> services;
    private List<String> locations;

    public static List<Device> get() {
        List<Device> devices = new ArrayList<>();
        for(String a : Paper.book(TABLE_DEVICE).getAllKeys()) devices.add(Paper.book(TABLE_DEVICE).read(a));
        return devices;
    }

    public static Device getExistingOrNew(BluetoothDevice device) {
        Device d = Paper.book(TABLE_DEVICE).read(device.getAddress());
        if(d != null) return d;
        return new Device(device);
    }

    public static Device get(String address) {
        return Paper.book(TABLE_DEVICE).read(address);
    }

    public static List<Device> get(List<String> addrs) {
        List<Device> devices = new ArrayList<>();
        for(String a : addrs) {
            Device d = Paper.book(TABLE_DEVICE).read(a);
            if(d != null) devices.add(d);
        }
        return devices;
    }

    public List<ILocation> getLocations() {
        List<ILocation> locations = new ArrayList<>();
        for (Scan s : Scan.get()) locations.addAll(s.getLocations(address));
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    public void addService(Service s) {
        if (!services.contains(s.getUuid())) services.add(s.getUuid());
    }

    public void addLocation(ILocation l) {
        if (!l.isEmpty()) locations.add(l.getUuid());
    }

    public List<Service> getServices() {
        List<Service> services = new ArrayList<>();
        for (Service u : Service.get()) {
            if (this.services.contains(u.getUuid())) services.add(u);
        }
        return services;
    }

    public void setServices(List<String> services) {
        this.services = services;
    }

    public void save() {
        Paper.book(TABLE_DEVICE).write(address, this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPin() {
        return pin;
    }

    public void setPin(Integer pin) {
        this.pin = pin;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBond() {
        return bond;
    }

    public void setBond(String bond) {
        this.bond = bond;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void updateServices(List<Service> services) {
        for(Service s : services) updateServices(s);
    }

    public void updateServices(Service service) {
        if (!this.services.contains(service.getUuid())) this.services.add(service.getUuid());
    }

    Device(){
        super(UUID.randomUUID().toString());
        pin = 0;
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

    public void setValues(BluetoothDevice device) {
        address = device.getAddress();
        name = device.getName();
        manufacturer = "todo";
        bond = getBondStateAsString(device.getBondState());
        type = getTypeAsString(device.getType());
        locations = new ArrayList<>();
        services = new ArrayList<>();
        pin = 0;
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