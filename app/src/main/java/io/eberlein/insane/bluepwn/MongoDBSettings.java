package io.eberlein.insane.bluepwn;

public class MongoDBSettings {
    String host;
    String port;
    String username;
    String password;
    Boolean authentication;

    public MongoDBSettings(){}

    public MongoDBSettings(String host, String port, String username, String password){
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.authentication = true;
    }

    public MongoDBSettings(String serverString){
        if(serverString.contains(":")) {
            this.host = serverString.split(":")[0];
            this.port = serverString.split(":")[1];
        } else {
            this.host = serverString;
            this.port = "27017";
        }
        this.authentication = false;
    }

    public MongoDBSettings(String serverString, String username, String password){
        setHostPortFromServerString(serverString);
        this.authentication = true;
        this.username = username;
        this.password = password;
    }

    public MongoDBSettings(String host, String port){
        this.host = host;
        this.port = port;
        this.authentication = false;
    }

    private void setHostPortFromServerString(String serverString){
        if(serverString.contains(":")) {
            this.host = serverString.split(":")[0];
            this.port = serverString.split(":")[1];
        } else {
            this.host = serverString;
            this.port = "27017";
        }
    }
}
