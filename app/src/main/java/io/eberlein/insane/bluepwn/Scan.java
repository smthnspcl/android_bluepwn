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
    @Column Boolean lastLoaded;

    List<Device> devices;
    @Column(typeConverter = JSONArrayTypeConverter.class) JSONArray locationsIds;

    @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "devices")
    public List<Device> getDevices(){
        return SQLite.select().from(Device.class).queryList();
    }

    public Scan(){
        devices = new ArrayList<>();
        locationsIds = new JSONArray();
    }
}
