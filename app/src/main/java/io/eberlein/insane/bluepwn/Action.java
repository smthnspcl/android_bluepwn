package io.eberlein.insane.bluepwn;


import java.util.Date;

public class Action {
    Long id;
    String name;
    Boolean hex;
    String data;
    String uuid;
    String macPrefix;
    Date lastModified;

    ParcelUuid parcelUuid;

    Action() {}

    public Action(Long id, String name, String data, String macPrefix, Date lastModified, Boolean hex, ParcelUuid parcelUuid){
        this.id = id;
        this.name = name;
        this.hex = hex;
        this.data = data;
        this.macPrefix = macPrefix;
        this.lastModified = lastModified;
        this.parcelUuid = parcelUuid;
    }
}