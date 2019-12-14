package io.eberlein.insane.bluepwn.object;

import android.bluetooth.BluetoothGattDescriptor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.paperdb.Paper;

import static io.eberlein.insane.bluepwn.Static.TABLE_DESCRIPTOR;

public class Descriptor extends DBObject {
    private String name;
    private Integer permissions;
    private byte[] value;

    public Descriptor() {
        super(UUID.randomUUID().toString());
    }

    public Descriptor(BluetoothGattDescriptor d) {
        super(d.getUuid().toString());
        this.name = "";
        this.permissions = d.getPermissions();
        this.value = d.getValue();
    }

    public Descriptor(String uuid, String name, Integer permissions, byte[] value) {
        super(uuid);
        this.name = name;
        this.permissions = permissions;
        this.value = value;
    }

    public static List<Descriptor> get() {
        List<Descriptor> descs = new ArrayList<>();
        for (String d : Paper.book(TABLE_DESCRIPTOR).getAllKeys())
            descs.add(Paper.book(TABLE_DESCRIPTOR).read(d));
        return descs;
    }

    public static Descriptor get(String uuid) {
        return Paper.book(TABLE_DESCRIPTOR).read(uuid);
    }

    public static String getPermissionsString(int permissions) {
        return Characteristic.getPermissionString(permissions);
    }

    public static Descriptor getExistingOrNew(BluetoothGattDescriptor descriptor) {
        Descriptor d = Paper.book(TABLE_DESCRIPTOR).read(descriptor.getUuid().toString());
        if(d == null) return new Descriptor(descriptor);
        else return d;
    }

    public void save() {
        Paper.book(TABLE_DESCRIPTOR).write(getUuid(), this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getValue() {
        return value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }

    public Integer getPermissions() {
        return permissions;
    }
}
