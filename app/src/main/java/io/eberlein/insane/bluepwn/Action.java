package io.eberlein.insane.bluepwn;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.Date;
import java.util.UUID;

@Table(database = LocalDatabase.class)
class Action extends BaseModel {
    @PrimaryKey(autoincrement = true) Long id;
    @Column String name;
    @Column Boolean hex;
    @Column String data;
    @Column String uuid;
    @Column String macPrefix;
    @Column Date lastModified;

    Action() {}

    public Action(Long id, String name, String data, String macPrefix, Date lastModified, Boolean hex){
        this.id = id;
        this.name = name;
        this.hex = hex;
        this.data = data;
        this.macPrefix = macPrefix;
        this.lastModified = lastModified;
    }
}