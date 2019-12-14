package io.eberlein.insane.bluepwn.object;


import java.util.Date;

import io.eberlein.insane.bluepwn.Static;

public class DBObject {
    private String uuid;
    private String lastModified;
    private String created;

    public DBObject(String uuid) {
        this.uuid = uuid;
        this.created = Static.date2String(new Date());
        this.lastModified = this.created;
    }

    public DBObject(String uuid, Date created, Date lastModified) {
        this.uuid = uuid;
        this.created = Static.date2String(created);
        this.lastModified = Static.date2String(lastModified);
    }

    public DBObject(String uuid, String created, String lastModified) {
        this.uuid = uuid;
        this.created = created;
        this.lastModified = lastModified;
    }

    public String getCreated() {
        return created;
    }

    public String getLastModified() {
        return lastModified;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
