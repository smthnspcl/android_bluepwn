package io.eberlein.insane.bluepwn;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = LocalDatabase.class)
public class Settings extends BaseModel {
    @PrimaryKey Long id;
    @Column(typeConverter = MongoDBSettingsTypeConverter.class) MongoDBSettings mongoDBSettings;

    public Settings(){
        id = 0L;
        mongoDBSettings = new MongoDBSettings();
    }

    public Settings(MongoDBSettings mongoDBSettings){
        id = 0L;
        this.mongoDBSettings = mongoDBSettings;
    }
}
