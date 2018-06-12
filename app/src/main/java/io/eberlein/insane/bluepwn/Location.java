package io.eberlein.insane.bluepwn;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.paperdb.Paper;

public class Location {
    String id;
    float accuracy;
    double altitude;
    double longitude;
    double latitude;
    long timestamp;
    float speed;
    Date lastModified;
    String note;
    String country;
    String city;
    String street;

    List<String> scans;

    /*
    public List<Scan> getScans(){
        List<Scan> scans = new ArrayList<>();
        for(String s : this.scans) scans.add(Paper.book("scan").read(s));
        return scans;
    }
    */

    void save(){
        Paper.book("location").write(id, this);
    }

    static Location get(String id){
        return Paper.book("location").read(id);
    }

    static List<Location> get(){
        List<Location> locations = new ArrayList<>();
        for(String l : Paper.book("location").getAllKeys()) locations.add(Paper.book("location").read(l));
        return locations;
    }

    static List<Location> getWithinRadius(Location location, Integer radius){
        return new ArrayList<>(); // todo
    }

    public Location(){
        id = UUID.randomUUID().toString();
        accuracy = 0;
        altitude = 0;
        longitude = 0;
        latitude = 0;
        timestamp = 0;
        speed = 0;
        lastModified = new Date();
    }

    public Location(String id, float accuracy, double altitude, double longitude, double latitude, long timestamp, float speed){
        this.id = id;
        this.accuracy = accuracy;
        this.altitude = altitude;
        this.longitude = longitude;
        this.latitude = latitude;
        this.timestamp = timestamp;
        this.speed = speed;
        lastModified = new Date();
    }

    public Location(android.location.Location location){
        id = UUID.randomUUID().toString();
        accuracy = location.getAccuracy();
        altitude = location.getAltitude();
        longitude = location.getLongitude();
        latitude = location.getLatitude();
        timestamp = location.getTime();
        speed = location.getSpeed();
        lastModified = new Date();
    }

    boolean isEmpty(){
        return (accuracy == 0 && altitude == 0 && longitude == 0 && latitude == 0 && timestamp == 0 && speed == 0);
    }
}