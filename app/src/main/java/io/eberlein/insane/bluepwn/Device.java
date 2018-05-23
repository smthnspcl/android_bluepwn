package io.eberlein.insane.bluepwn;

import android.bluetooth.BluetoothDevice;

import com.alibaba.fastjson.JSONArray;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// todo extract important information from https://en.wikipedia.org/wiki/List_of_Bluetooth_profiles
// todo script to pull tables https://www.bluetooth.com/specifications/assigned-numbers/service-discovery

@Table(database = LocalDatabase.class)
class Device extends BaseModel{
    @PrimaryKey String address;
    @Column String name;
    @Column String type;
    @Column String bond;
    @Column String manufacturer;
    @Column Date lastModified;

    @Column(typeConverter = JSONArrayTypeConverter.class) JSONArray locationIdsJson;
    @Column(typeConverter = JSONArrayTypeConverter.class) JSONArray parcelUuidsJson;

    // todo proximity alert bool value; trigger with locationManager.addProximityAlert();

    public List<Location> getLocations(){
        List<Location> locations = new ArrayList<>();
        for(Object l : locationIdsJson.toArray()) locations.add(SQLite.select().from(Location.class).where(Location_Table.id.eq((Long) l)).querySingle());
        return locations;
    }

    public List<ParcelUuid> getParcelUuids(){
        List<ParcelUuid> parcelUuids = new ArrayList<>();
        for(Object p : parcelUuidsJson.toArray()) parcelUuids.add(SQLite.select().from(ParcelUuid.class).where(ParcelUuid_Table.id.eq((Long) p)).querySingle());
        return parcelUuids;
    }

    public Device() {}

    public Device(BluetoothDevice device){
        address = device.getAddress();
        name = device.getName();
        type = getTypeAsString(device.getType());
        bond = getBondStateAsString(device.getBondState());
        manufacturer = "todo";
        parcelUuidsJson = new JSONArray();
        lastModified = new Date();
        locationIdsJson = new JSONArray();
    }

    public Device(String address, String name, String type, String bond, String manufacturer, JSONArray parcelUuidsJson, Date lastModified, JSONArray locationIdsJson){
        this.address = address;
        this.name = name;
        this.type = type;
        this.bond = bond;
        this.manufacturer = manufacturer;
        this.parcelUuidsJson = parcelUuidsJson;
        this.lastModified = lastModified;
        this.locationIdsJson = locationIdsJson;
    }

    private String getTypeAsString(int _type){
        switch (_type){
            case BluetoothDevice.DEVICE_TYPE_UNKNOWN:
                return "unknown";
            case BluetoothDevice.DEVICE_TYPE_CLASSIC:
                return "classic";
            case BluetoothDevice.DEVICE_TYPE_LE:
                return "le";
            case BluetoothDevice.DEVICE_TYPE_DUAL:
                return "dual";
            default:
                return "none";
        }
    }

    private String getBondStateAsString(int _state){
        switch (_state){
            case BluetoothDevice.BOND_NONE:
                return "none";
            case BluetoothDevice.BOND_BONDING:
                return "bonding";
            case BluetoothDevice.BOND_BONDED:
                return "bonded";
            default:
                return "unknown";
        }
    }
}