package io.eberlein.insane.bluepwn;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.paperdb.Paper;

import static io.eberlein.insane.bluepwn.Static.TABLE_DEVICE;
import static io.eberlein.insane.bluepwn.Static.TABLE_LOCATION;
import static io.eberlein.insane.bluepwn.Static.TABLE_SCAN;

public class Scan {
    String id;
    Date timestamp;

    List<String> devices;
    List<String> locations;

    public Scan(){
        id = UUID.randomUUID().toString();
        timestamp = new Date();
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

    void save(){
        Paper.book(TABLE_SCAN).write(id, this);
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
