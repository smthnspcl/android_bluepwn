package io.eberlein.insane.bluepwn;

public class Settings {
    MongoDBSettings mongoDBSettings;

    public Settings(){
        mongoDBSettings = new MongoDBSettings();
    }

    public Settings(MongoDBSettings mongoDBSettings){
        this.mongoDBSettings = mongoDBSettings;
    }
}
