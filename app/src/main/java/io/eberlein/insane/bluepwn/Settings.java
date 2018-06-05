package io.eberlein.insane.bluepwn;

public class Settings {
    RemoteDBSettings remoteDBSettings;

    public Settings(){
        remoteDBSettings = new RemoteDBSettings();
    }

    public Settings(RemoteDBSettings mongoDBSettings){
        this.remoteDBSettings = mongoDBSettings;
    }
}
