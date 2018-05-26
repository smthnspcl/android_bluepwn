package io.eberlein.insane.bluepwn;

import com.alibaba.fastjson.JSONArray;
import com.google.gson.JsonArray;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Table(database = LocalDatabase.class)
public class Scan extends BaseModel {
    @PrimaryKey(autoincrement = true) Long id;
    @Column Date timestamp;

    @Column(typeConverter = JSONArrayTypeConverter.class) JSONArray deviceAddresses;
    @Column(typeConverter = JSONArrayTypeConverter.class) JSONArray locationsIds;

    public List<Device> getDevices(){
        List<Device> devices = new ArrayList<>();
        for(Object o : deviceAddresses) devices.add(SQLite.select().from(Device.class).where(Device_Table.address.eq((String) o)).querySingle());
        return devices;
    }

    public List<Location> getLocations(){
        List<Location> locations = new ArrayList<>();
        for(Object o : locationsIds) locations.add(SQLite.select().from(Location.class).where(Location_Table.id.eq((Long) o)).querySingle());
        return locations;
    }

    public Scan(){
        deviceAddresses = new JSONArray();
        locationsIds = new JSONArray();
    }
}
