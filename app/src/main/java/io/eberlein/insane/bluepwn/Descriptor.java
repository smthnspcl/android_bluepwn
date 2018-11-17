package io.eberlein.insane.bluepwn;

import android.bluetooth.BluetoothGattDescriptor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.paperdb.Paper;

import static io.eberlein.insane.bluepwn.Static.TABLE_DESCRIPTOR;

public class Descriptor extends DBObject{
    String name;
    Integer permissions;
    byte[] value;

    Descriptor(){
        super(UUID.randomUUID().toString());
    }

    static List<Descriptor> get(){
        List<Descriptor> descs = new ArrayList<>();
        for(String d : Paper.book(TABLE_DESCRIPTOR).getAllKeys()) descs.add(Paper.book(TABLE_DESCRIPTOR).read(d));
        return descs;
    }

    static Descriptor get(String uuid){
        return Paper.book(TABLE_DESCRIPTOR).read(uuid);
    }

    Descriptor(BluetoothGattDescriptor d){
        super(d.getUuid().toString());
        this.name = "";
        this.permissions = d.getPermissions();
        this.value = d.getValue();
    }

    Descriptor(String uuid, String name, Integer permissions, byte[] value){
        super(uuid);
        this.name = name;
        this.permissions = permissions;
        this.value = value;
    }

    void save(){
        Paper.book(TABLE_DESCRIPTOR).write(uuid, this);
    }

    static String getPermissionsString(int permissions){
        return Characteristic.getPermissionString(permissions);
    }

    static Descriptor getExistingOrNew(BluetoothGattDescriptor descriptor){
        Descriptor d = Paper.book(TABLE_DESCRIPTOR).read(descriptor.getUuid().toString());
        if(d == null) return new Descriptor(descriptor);
        else return d;
    }

}
