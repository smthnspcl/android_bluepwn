package io.eberlein.insane.bluepwn;

public class Settings {
    MongoDBSettings dbSettings;
}


class MongoDBSettings {
    String host;
    String port;
    String username;
    String password;

    MongoDBSettings(){}
    MongoDBSettings(String host, String port, String username, String password){
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }
}