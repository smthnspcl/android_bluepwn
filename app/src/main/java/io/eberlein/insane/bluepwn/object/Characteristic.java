package io.eberlein.insane.bluepwn.object;

import android.bluetooth.BluetoothGattCharacteristic;

import com.movisens.smartgattlib.Characteristics;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.eberlein.insane.bluepwn.Static;
import io.paperdb.Paper;

import static io.eberlein.insane.bluepwn.Static.TABLE_CHARACTERISTIC;

public class Characteristic extends DBObject {
    private Integer permssions;
    private Integer properties;
    private Integer writeType;
    private String name;
    private byte[] value;

    private List<String> descriptors;

    public Characteristic() {
        super(UUID.randomUUID().toString());
        descriptors = new ArrayList<>();
    }

    public Characteristic(BluetoothGattCharacteristic c) {
        super(c.getUuid().toString());
        this.permssions = c.getPermissions();
        this.properties = c.getProperties();
        this.value = c.getValue();
        this.writeType = c.getWriteType();
        this.name = Characteristics.lookup(c.getUuid()).getName();
        this.descriptors = new ArrayList<>();
    }

    public Characteristic(String uuid, String name, Integer permissions, Integer properties, Integer writeType, byte[] value) {
        super(uuid);
        this.name = name;
        this.permssions = permissions;
        this.properties = properties;
        this.value = value;
        this.writeType = writeType;
        this.descriptors = new ArrayList<>();
    }

    public static Characteristic get(String uuid) {
        return Paper.book(Static.TABLE_CHARACTERISTIC).read(uuid);
    }

    public static Characteristic getExistingOrNew(BluetoothGattCharacteristic bgc) {
        Characteristic c = Paper.book(TABLE_CHARACTERISTIC).read(bgc.getUuid().toString());
        if(c == null) return new Characteristic(bgc);
        else return c;
    }

    public static String getWriteTypeString(int writeType) {
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

    public static String getPropertyString(int property) {
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

    public static String getPermissionString(int permission) {
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

    public String getName() {
        return name;
    }

    public byte[] getValue() {
        return value;
    }

    public Integer getPermssions() {
        return permssions;
    }

    public Integer getProperties() {
        return properties;
    }

    public Integer getWriteType() {
        return writeType;
    }

    public List<String> getDescriptors() {
        return descriptors;
    }

    public void updateDescriptors(Descriptor d) {
        String u = d.getUuid();
        if (!descriptors.contains(u)) descriptors.add(u);
    }

    public void save() {
        Paper.book(TABLE_CHARACTERISTIC).write(getUuid(), this);
    }
}
