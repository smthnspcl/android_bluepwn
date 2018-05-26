package io.eberlein.insane.bluepwn;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.Date;
import java.util.UUID;


@Table(database = LocalDatabase.class)
public class ParcelUuid extends BaseModel {
    @PrimaryKey(autoincrement = true) Long id;
    @Column String name;
    @Column String description;
    @Column String protocol; // new action: specify uuid and use protocol space holders // list of rows ( stages ) ?
    @Column Date lastModified;
    @Column(typeConverter = ParcelUuidTypeConverter.class) android.os.ParcelUuid uuid;

    ParcelUuid(){}

    ParcelUuid(android.os.ParcelUuid parcelUuid){
        uuid = parcelUuid;
        lastModified = new Date();
    }

    static ParcelUuid getExistingOrNew(android.os.ParcelUuid uuid){
        ParcelUuid u = SQLite.select().from(ParcelUuid.class).where(ParcelUuid_Table.uuid.eq(uuid)).querySingle();
        if(u != null) return u;
        return new ParcelUuid(uuid);
    }
}
