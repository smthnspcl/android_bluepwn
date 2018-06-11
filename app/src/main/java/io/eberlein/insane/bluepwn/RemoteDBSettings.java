package io.eberlein.insane.bluepwn;

public class RemoteDBSettings {
    private String server;
    private Boolean authentication;
    private String username;
    private String password;
    private Boolean ssl;

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

    void setServer(String server){
        this.server = server;
    }

    public void setAuthentication(Boolean authentication) {
        this.authentication = authentication;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSsl(Boolean ssl) {
        this.ssl = ssl;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getAuthentication() {
        return authentication;
    }

    public Boolean getSsl() {
        return ssl;
    }

    public String getPassword() {
        return password;
    }

    public String getServer() {
        return server;
    }

    public String getUsername() {
        return username;
    }
}
