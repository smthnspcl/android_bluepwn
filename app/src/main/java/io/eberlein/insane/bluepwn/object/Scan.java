package io.eberlein.insane.bluepwn.object;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.paperdb.Paper;

import static io.eberlein.insane.bluepwn.Static.TABLE_DEVICE;
import static io.eberlein.insane.bluepwn.Static.TABLE_LOCATION;
import static io.eberlein.insane.bluepwn.Static.TABLE_SCAN;
import static io.eberlein.insane.bluepwn.Static.TYPE_CLASSIC;
import static io.eberlein.insane.bluepwn.Static.TYPE_LE;

public class Scan extends DBObject {
    private List<String> devices;
    private List<String> locations;

    public Scan() {
        super(UUID.randomUUID().toString());
        devices = new ArrayList<>();
        locations = new ArrayList<>();
    }

    public static Scan get(String id) {
        return Paper.book(TABLE_SCAN).read(id);
    }

    public static List<Scan> get() {
        List<Scan> scans = new ArrayList<>();
        for(String s : Paper.book(TABLE_SCAN).getAllKeys()) scans.add(Paper.book(TABLE_SCAN).read(s));
        System.out.println("getting " + scans.size() + " amount of scans");
        return scans;
    }

    public List<Device> getGATTDevices() {
        List<Device> devices = new ArrayList<>();
        for(String d : this.devices) {
            Device _d = Device.get(d);
            if (_d.getType().equals(TYPE_LE)) devices.add(_d);
        }
        return devices;
    }

    public List<Device> getClassicDevices() {
        List<Device> devices = new ArrayList<>();
        for(String d : this.devices) {
            Device _d = Device.get(d);
            if (_d.getType().equals(TYPE_CLASSIC)) devices.add(_d);
        }
        return devices;
    }

    public void save() {
        Log.d(getClass().toString(), "saving scan");
        Log.d(getClass().toString(), this.toString());
        Paper.book(TABLE_SCAN).write(getUuid(), this);
    }

    public List<Device> getDevices(){
        List<Device> devs = new ArrayList<>();
        for(String d : devices) {
            Device device = Paper.book(TABLE_DEVICE).read(d);
            //Log.log(this.getClass(), device.toString());
            devs.add(device);
        }
        return devs;
    }

    public void addLocation(ILocation l) {
        if (!l.isEmpty()) locations.add(l.getUuid());
    }

    public void addDevice(Device device) {
        if (!devices.contains(device.getAddress())) devices.add(device.getAddress());
        this.save();
    }

    public void removeDevice(Device device) {
        devices.remove(device.getAddress());
        this.save();
    }

    public List<ILocation> getLocations(String address) {
        if (devices.contains(address)) return getLocations();
        return new ArrayList<>();
    }

    public List<ILocation> getLocations() {
        List<ILocation> ILocations = new ArrayList<>();
        for (String l : this.locations)
            if (l != null) ILocations.add(Paper.book(TABLE_LOCATION).read(l));
        return ILocations;
    }

    public List<Device> getDevicesWithType(String type){
        List<Device> r = new ArrayList<>();
        for(String d : devices){
            Device _d = Paper.book(TABLE_DEVICE).read(d);
            if (_d.getType().equals(type)) r.add(_d);
        }
        return r;
    }
}
