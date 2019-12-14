package io.eberlein.insane.bluepwn.object;

import io.paperdb.Paper;

import static io.eberlein.insane.bluepwn.Static.KEY_REMOTE_DATABASE_SETTINGS;
import static io.eberlein.insane.bluepwn.Static.TABLE_SETTINGS;

public class RemoteDBSettings {
    private String server;
    private Boolean authentication;
    private String username;
    private String password;
    private Boolean ssl;

    public RemoteDBSettings() {
        this.server = "";
        this.authentication = false;
        this.username = "";
        this.password = "";
        this.ssl = false;
    }

    public RemoteDBSettings(String server, Boolean authentication, String username, String password, Boolean ssl) {
        this.server = server;
        this.authentication = authentication;
        this.username = username;
        this.password = password;
        this.ssl = ssl;
    }

    public static RemoteDBSettings get() {
        RemoteDBSettings rdbs = Paper.book(TABLE_SETTINGS).read(KEY_REMOTE_DATABASE_SETTINGS);
        if(rdbs == null) return new RemoteDBSettings();
        return rdbs;
    }

    public void save() {
        Paper.book(TABLE_SETTINGS).write(KEY_REMOTE_DATABASE_SETTINGS, this);
    }

    public Boolean getAuthentication() {
        return authentication;
    }

    public void setAuthentication(Boolean authentication) {
        this.authentication = authentication;
    }

    public Boolean getSsl() {
        return ssl;
    }

    public void setSsl(Boolean ssl) {
        this.ssl = ssl;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
