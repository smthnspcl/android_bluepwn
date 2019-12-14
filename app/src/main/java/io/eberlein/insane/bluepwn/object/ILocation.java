package io.eberlein.insane.bluepwn.object;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.paperdb.Paper;

import static io.eberlein.insane.bluepwn.Static.TABLE_LOCATION;

public class ILocation extends DBObject {
    private float accuracy;
    private double altitude;
    private double longitude;
    private double latitude;
    private long timestamp;
    private float speed;
    private String note;
    private String country;
    private String city;
    private String street;

    private List<String> scans;

    /*
    public List<Scan> getScans(){
        List<Scan> scans = new ArrayList<>();
        for(String s : this.scans) scans.add(Paper.book("scan").read(s));
        return scans;
    }
    */

    public ILocation() {
        super(UUID.randomUUID().toString());
        accuracy = 0;
        altitude = 0;
        longitude = 0;
        latitude = 0;
        timestamp = 0;
        speed = 0;
    }

    public ILocation(String id, float accuracy, double altitude, double longitude, double latitude, long timestamp, float speed) {
        super(id);
        this.accuracy = accuracy;
        this.altitude = altitude;
        this.longitude = longitude;
        this.latitude = latitude;
        this.timestamp = timestamp;
        this.speed = speed;
    }

    public ILocation(android.location.Location location) {
        super(UUID.randomUUID().toString());
        accuracy = location.getAccuracy();
        altitude = location.getAltitude();
        longitude = location.getLongitude();
        latitude = location.getLatitude();
        timestamp = location.getTime();
        speed = location.getSpeed();
    }

    public static ILocation get(String id) {
        return Paper.book(TABLE_LOCATION).read(id);
    }

    public static List<ILocation> get() {
        List<ILocation> ILocations = new ArrayList<>();
        for (String l : Paper.book(TABLE_LOCATION).getAllKeys())
            ILocations.add(Paper.book(TABLE_LOCATION).read(l));
        return ILocations;
    }

    public static List<ILocation> getWithinRadius(ILocation ILocation, Integer radius) {
        return new ArrayList<>(); // todo
    }

    public void save() {
        Paper.book(TABLE_LOCATION).write(getUuid(), this);
    }

    public boolean isEmpty() {
        return (accuracy == 0 && altitude == 0 && longitude == 0 && latitude == 0 && timestamp == 0 && speed == 0);
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public List<String> getScans() {
        return scans;
    }

    public void setScans(List<String> scans) {
        this.scans = scans;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}