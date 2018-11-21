package io.eberlein.insane.bluepwn;

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
    List<String> devices;
    List<String> locations;

    Scan(){
        super(UUID.randomUUID().toString());
        devices = new ArrayList<>();
        locations = new ArrayList<>();
    }

    static Scan get(String id){
        return Paper.book(TABLE_SCAN).read(id);
    }

    static List<Scan> get(){
        List<Scan> scans = new ArrayList<>();
        for(String s : Paper.book(TABLE_SCAN).getAllKeys()) scans.add(Paper.book(TABLE_SCAN).read(s));
        return scans;
    }

    List<Device> getGATTDevices(){
        List<Device> devices = new ArrayList<>();
        for(String d : this.devices) {
            Device _d = Device.getExistingOrNew(d);
            if(_d.type.equals(TYPE_LE)) devices.add(_d);
        }
        return devices;
    }

    List<Device> getClassicDevices(){
        List<Device> devices = new ArrayList<>();
        for(String d : this.devices) {
            Device _d = Device.getExistingOrNew(d);
            if(_d.type.equals(TYPE_CLASSIC)) devices.add(_d);
        }
        return devices;
    }

    void save(){
        Paper.book(TABLE_SCAN).write(uuid, this);
    }

    public List<Device> getDevices(){
        List<Device> devs = new ArrayList<>();
        for(String d : devices) devs.add(Paper.book(TABLE_DEVICE).read(d));
        return devs;
    }

    public List<Location> getLocations(){
        List<Location> locations = new ArrayList<>();
        for(String l : this.locations) if(l != null) locations.add(Paper.book(TABLE_LOCATION).read(l));
        return locations;
    }

    public List<Device> getDevicesWithType(String type){
        List<Device> r = new ArrayList<>();
        for(String d : devices){
            Device _d = Paper.book(TABLE_DEVICE).read(d);
            if(_d.type.equals(type)) r.add(_d);
        }
        return r;
    }
}
