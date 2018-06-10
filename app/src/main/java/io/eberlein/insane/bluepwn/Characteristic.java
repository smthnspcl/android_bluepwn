package io.eberlein.insane.bluepwn;

import android.bluetooth.BluetoothGattCharacteristic;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class Characteristic {

    Integer permssions;
    Integer properties;
    Integer writeType;
    String uuid;
    String name;
    byte[] value;

    List<String> descriptors;

    Characteristic(){
        descriptors = new ArrayList<>();
    }

    Characteristic(BluetoothGattCharacteristic c){
        this.uuid = c.getUuid().toString();
        this.permssions = c.getPermissions();
        this.properties = c.getProperties();
        this.value = c.getValue();
        this.writeType = c.getWriteType();
        this.name = "";
        this.descriptors = new ArrayList<>();
    }

    Characteristic(String uuid, String name, Integer permissions, Integer properties, Integer writeType, byte[] value){
        this.uuid = uuid;
        this.name = name;
        this.permssions = permissions;
        this.properties = properties;
        this.value = value;
        this.writeType = writeType;
        this.descriptors = new ArrayList<>();
    }

    void save(){
        Paper.book("characteristic").write(uuid, this);
    }

    static Characteristic getExistingOrNew(BluetoothGattCharacteristic bgc){
        Characteristic c = Paper.book("characteristic").read(bgc.getUuid().toString());
        if(c == null) return new Characteristic(bgc);
        else return c;
    }

    static String getWriteTypeString(int writeType){
        switch (writeType){
            case BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT:
                return "default";
            case BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE:
                return "no-response";
            case BluetoothGattCharacteristic.WRITE_TYPE_SIGNED:
                return "signed";
            default:
                return "unknown";
        }
    }

    static String getPropertyString(int property){
        switch (property){
            case BluetoothGattCharacteristic.PROPERTY_BROADCAST:
                return "broadcast";
            case BluetoothGattCharacteristic.PROPERTY_EXTENDED_PROPS:
                return "extended-props";
            case BluetoothGattCharacteristic.PROPERTY_INDICATE:
                return "indicate";
            case BluetoothGattCharacteristic.PROPERTY_NOTIFY:
                return "notify";
            case BluetoothGattCharacteristic.PROPERTY_READ:
                return "read";
            case BluetoothGattCharacteristic.PROPERTY_SIGNED_WRITE:
                return "signed-write";
            case BluetoothGattCharacteristic.PROPERTY_WRITE:
                return "write";
            case BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE:
                return "write-no-response";
            default:
                return "unknown";
        }
    }

    static String getPermissionString(int permission){
        switch (permission){
            case BluetoothGattCharacteristic.PERMISSION_READ:
                return "read";
            case BluetoothGattCharacteristic.PERMISSION_READ_ENCRYPTED:
                return "read-encrypted";
            case BluetoothGattCharacteristic.PERMISSION_READ_ENCRYPTED_MITM:
                return "read-encrypted-mitm";
            case BluetoothGattCharacteristic.PERMISSION_WRITE:
                return "write";
            case BluetoothGattCharacteristic.PERMISSION_WRITE_ENCRYPTED:
                return "write-encrypted";
            case BluetoothGattCharacteristic.PERMISSION_WRITE_ENCRYPTED_MITM:
                return "write-encrypted-mitm";
            case BluetoothGattCharacteristic.PERMISSION_WRITE_SIGNED:
                return "write-signed";
            case BluetoothGattCharacteristic.PERMISSION_WRITE_SIGNED_MITM:
                return "write-signed-mitm";
            default:
                return "unknown";
        }
    }
}