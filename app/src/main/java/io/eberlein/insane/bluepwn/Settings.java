package io.eberlein.insane.bluepwn;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = LocalDatabase.class)
public class Settings extends BaseModel {
    @Column(typeConverter = MongoDBSettingsTypeConverter.class) MongoDBSettings mongoDBSettings;

    public Settings(){
        mongoDBSettings = new MongoDBSettings();
    }

    public Settings(MongoDBSettings mongoDBSettings){
        mongoDBSettings = mongoDBSettings;
    }
}


class MongoDBSettings {
    String host;
    String port;
    String username;
    String password;

    public MongoDBSettings(){}
    public MongoDBSettings(String host, String port, String username, String password){
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public MongoDBSettings(String serverString, String username, String password){
        if(serverString.contains(":")) {
            this.host = serverString.split(":")[0];
            this.port = serverString.split(":")[1];
        } else {
            this.host = serverString;
            this.port = "27017";
        }
        this.username = username;
        this.password = password;
    }
}