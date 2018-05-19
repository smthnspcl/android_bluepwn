package io.eberlein.insane.bluepwn;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ManyToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

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

    public Location(){}

    public Location(android.location.Location location){
        accuracy = location.getAccuracy();
        altitude = location.getAltitude();
        longitude = location.getLongitude();
        latitude = location.getLatitude();
        timestamp = location.getTime();
        speed = location.getSpeed();
    }
}