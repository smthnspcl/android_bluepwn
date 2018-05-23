package io.eberlein.insane.bluepwn;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ManyToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.Date;

@Table(database = LocalDatabase.class)
@ManyToMany(referencedTable = Device.class)
public class Location extends BaseModel {
    @PrimaryKey(autoincrement = true) Long id;
    @Column float accuracy;
    @Column double altitude;
    @Column double longitude;
    @Column double latitude;
    @Column long timestamp;
    @Column float speed;
    @Column Date lastModified;

    public Location(){
        accuracy = 0;
        altitude = 0;
        longitude = 0;
        latitude = 0;
        timestamp = 0;
        speed = 0;
        lastModified = new Date();
    }

    public Location(float accuracy, double altitude, double longitude, double latitude, long timestamp, float speed){
        this.accuracy = accuracy;
        this.altitude = altitude;
        this.longitude = longitude;
        this.latitude = latitude;
        this.timestamp = timestamp;
        this.speed = speed;
        lastModified = new Date();
    }

    public Location(android.location.Location location){
        accuracy = location.getAccuracy();
        altitude = location.getAltitude();
        longitude = location.getLongitude();
        latitude = location.getLatitude();
        timestamp = location.getTime();
        speed = location.getSpeed();
        lastModified = new Date();
    }
}