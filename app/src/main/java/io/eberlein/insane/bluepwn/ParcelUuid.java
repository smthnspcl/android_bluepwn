package io.eberlein.insane.bluepwn;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = LocalDatabase.class)
public class ParcelUuid extends BaseModel {
    @PrimaryKey(autoincrement = true) Long id;
    @Column(typeConverter = ParcelUuidTypeConverter.class) android.os.ParcelUuid uuid;

    ParcelUuid(){}

    ParcelUuid(android.os.ParcelUuid parcelUuid){
        uuid = parcelUuid;
    }

}
