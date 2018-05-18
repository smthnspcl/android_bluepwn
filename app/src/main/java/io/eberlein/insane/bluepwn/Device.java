package io.eberlein.insane.bluepwn;

import android.bluetooth.BluetoothDevice;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
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

    @Column String locationIdsJson;
    @Column String parcelUuidsJson;

    // app helper
    @Column Boolean lastLoaded;
    // todo proximity alert bool value; trigger with locationManager.addProximityAlert();

    public Device() {}

    public Device(BluetoothDevice device){
        address = device.getAddress();
        name = device.getName();
        type = getTypeAsString(device.getType());
        bond = getBondStateAsString(device.getBondState());
        manufacturer = "todo";
        uuids = new ArrayList<>();
        lastModified = new Date();
        locations = new ArrayList<>();
        lastLoaded = false;
    }

    public Device(String address, String name, String type, String bond, String manufacturer, List<ParcelUuid> uuids, Date lastModified, List<Location> locations, Boolean lastLoaded){
        this.address = address;
        this.name = name;
        this.type = type;
        this.bond = bond;
        this.manufacturer = manufacturer;
        this.uuids = uuids;
        this.lastModified = lastModified;
        this.locations = locations;
        this.lastLoaded = lastLoaded;
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