package io.eberlein.insane.bluepwn;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.paperdb.Paper;

public class LocalDatabase {
    public static List<Device> getAllDevices(){
        List<Device> devices = new ArrayList<>();
        for(String a : Paper.book("device").getAllKeys()) devices.add(Paper.book("device").read(a));
        return devices;
    }

    public static List<Stage> getAllStages(){
        List<Stage> actions = new ArrayList<>();
        for(String s : Paper.book("stage").getAllKeys()) actions.add(Paper.book("stage").read(s));
        return actions;
    }

    public static List<Scan> getAllScans(){
        List<Scan> scans = new ArrayList<>();
        for(String s : Paper.book("scan").getAllKeys()) scans.add(Paper.book("scan").read(s));
        return scans;
    }

    public static List<Service> getAllUUIDs(){
        List<Service> services = new ArrayList<>();
        for(String u : Paper.book("uuid").getAllKeys()) services.add(Paper.book("uuid").read(u));
        return services;
    }

    public static List<Location> getAllLocations(){
        List<String> lids = Paper.book("location").getAllKeys();
        List<Location> locations = new ArrayList<>();
        for(String l : lids) locations.add(Paper.book("location").read(l));
        return locations;
    }

    public static List<Stager> getAllStagers(){
        List<Stager> stagers = new ArrayList<>();
        for(String s : Paper.book("stager").getAllKeys()) stagers.add(Paper.book("stager").read(s));
        return stagers;
    }

    public static List<Stager> getStagersByUuid(UUID uuid){
        List<Stager> stagers = new ArrayList<>();
        for(String key : Paper.book("stager").getAllKeys()) stagers.add(Paper.book("stager").read(key));
        return stagers;
    }
}
