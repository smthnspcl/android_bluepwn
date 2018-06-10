package io.eberlein.insane.bluepwn;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.paperdb.Paper;

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

    void save(){
        Paper.book("scan").write(id, this);
    }

    public List<Device> getDevices(){
        List<Device> devs = new ArrayList<>();
        for(String d : devices) devs.add(Paper.book("device").read(d));
        return devs;
    }

    public List<Location> getLocations(){
        List<Location> locations = new ArrayList<>();
        for(String l : this.locations) if(l != null) locations.add(Paper.book("location").read(l));
        return locations;
    }

    public List<Device> getDevicesWithType(String type){
        List<Device> r = new ArrayList<>();
        for(String d : devices){
            Device _d = Paper.book("device").read(d);
            if(_d.type.equals(type)) r.add(_d);
        }
        return r;
    }
}
