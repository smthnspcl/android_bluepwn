package io.eberlein.insane.bluepwn;

import io.paperdb.Paper;

import static io.eberlein.insane.bluepwn.Static.KEY_REMOTE_DATABASE_SETTINGS;
import static io.eberlein.insane.bluepwn.Static.TABLE_SETTINGS;

public class RemoteDBSettings {
    String server;
    Boolean authentication;
    String username;
    String password;
    Boolean ssl;

    RemoteDBSettings(){
        this.server = "";
        this.authentication = false;
        this.username = "";
        this.password = "";
        this.ssl = false;
    }

    RemoteDBSettings(String server, Boolean authentication, String username, String password, Boolean ssl){
        this.server = server;
        this.authentication = authentication;
        this.username = username;
        this.password = password;
        this.ssl = ssl;
    }

    void save(){
        Paper.book(TABLE_SETTINGS).write(KEY_REMOTE_DATABASE_SETTINGS, this);
    }

    static RemoteDBSettings get(){
        RemoteDBSettings rdbs = Paper.book(TABLE_SETTINGS).read(KEY_REMOTE_DATABASE_SETTINGS);
        if(rdbs == null) return new RemoteDBSettings();
        return rdbs;
    }
}
