package io.eberlein.insane.bluepwn;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class LocalDatabase {
    public static List<Device> getAllDevices(){
        List<String> addrs = Paper.book("device").getAllKeys();
        List<Device> devices = new ArrayList<>();
        for(String a : addrs) devices.add(Paper.book("device").read(a));
        return devices;
    }

    public static List<Action> getAllActions(){
        List<String> aids = Paper.book("action").getAllKeys();
        List<Action> actions = new ArrayList<>();
        for(String a : aids) actions.add(Paper.book("action").read(a));
        return actions;
    }

    public static List<Scan> getAllScans(){
        List<String> sids = Paper.book("scan").getAllKeys();
        List<Scan> scans = new ArrayList<>();
        for(String s : sids) scans.add(Paper.book("scan").read(s));
        return scans;
    }

    public static List<ParcelUuid> getAllParcelUuids(){
        List<String> puids = Paper.book("parcelUuids").getAllKeys();
        List<ParcelUuid> parcelUuids = new ArrayList<>();
        for(String p : puids) parcelUuids.add(Paper.book("parcelUuid").read(p));
        return parcelUuids;
    }

    public static List<Location> getAllLocations(){
        List<String> lids = Paper.book("location").getAllKeys();
        List<Location> locations = new ArrayList<>();
        for(String l : lids) locations.add(Paper.book("location").read(l));
        return locations;
    }
}
