package io.eberlein.insane.bluepwn;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ManyToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

@Table(database = LocalDatabase.class)
@ManyToMany(referencedTable = Device.class)
public class Location {
    @PrimaryKey(autoincrement = true) Long id;
    @Column(typeConverter = LocationTypeConverter.class) android.location.Location location;

    public Location(){}

    public Location(android.location.Location location){
        this.location = location;

    }
}