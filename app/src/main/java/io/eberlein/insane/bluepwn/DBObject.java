package io.eberlein.insane.bluepwn;


import java.util.Date;

public class DBObject {
    String uuid;
    String lastModified;
    String created;

    DBObject(String uuid){
        this.uuid = uuid;
        this.created = Static.date2String(new Date());
        this.lastModified = this.created;
    }

    DBObject(String uuid, Date created, Date lastModified){
        this.uuid = uuid;
        this.created = Static.date2String(created);
        this.lastModified = Static.date2String(lastModified);
    }

    DBObject(String uuid, String created, String lastModified){
        this.uuid = uuid;
        this.created = created;
        this.lastModified = lastModified;
    }
}
