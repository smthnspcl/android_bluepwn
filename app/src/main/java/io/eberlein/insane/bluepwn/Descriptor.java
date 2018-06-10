package io.eberlein.insane.bluepwn;

import android.bluetooth.BluetoothGattDescriptor;

import io.paperdb.Paper;

public class Descriptor {
    String uuid;
    String name;
    Integer permissions;
    byte[] value;

    Descriptor(){}

    Descriptor(BluetoothGattDescriptor d){
        this.uuid = d.getUuid().toString();
        this.name = "";
        this.permissions = d.getPermissions();
        this.value = d.getValue();
    }

    Descriptor(String uuid, String name, Integer permissions, byte[] value){
        this.uuid = uuid;
        this.name = name;
        this.permissions = permissions;
        this.value = value;
    }

    void save(){
        Paper.book("descriptor").write(uuid, this);
    }

    static String getPermissionsString(int permissions){
        return Characteristic.getPermissionString(permissions);
    }

    static Descriptor getExistingOrNew(BluetoothGattDescriptor descriptor){
        Descriptor d = Paper.book("descriptor").read(descriptor.getUuid().toString());
        if(d == null) return new Descriptor(descriptor);
        else return d;
    }

}
