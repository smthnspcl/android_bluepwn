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
    String type;
    String bond;
    String manufacturer;
    Date lastModified;

    List<String> parcelUuids;
    List<String> locations;

    public List<Location> getLocations() {
        List<Location> locations = new ArrayList<>();
        for(String l : this.locations) locations.add(Paper.book("location").read(l));
        return locations;
    }

    public List<ParcelUuid> getParcelUuids(){
        List<ParcelUuid> parcelUuids = new ArrayList<>();
        for(String p : this.parcelUuids) parcelUuids.add(Paper.book("parcelUuid").read(p));
        return parcelUuids;
    }

    public Device(BluetoothDevice device){
        address = device.getAddress();
        name = device.getName();
        type = getTypeAsString(device.getType());
        bond = getBondStateAsString(device.getBondState());
        manufacturer = "todo";
        lastModified = new Date();
        parcelUuids = new ArrayList<>();
        locations = new ArrayList<>();
    }

    public Device(String address, String name, String type, String bond, String manufacturer, Date lastModified){
        this.address = address;
        this.name = name;
        this.type = type;
        this.bond = bond;
        this.manufacturer = manufacturer;
        this.lastModified = lastModified;
        parcelUuids = new ArrayList<>();
        locations = new ArrayList<>();
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