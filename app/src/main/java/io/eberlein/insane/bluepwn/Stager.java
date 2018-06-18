package io.eberlein.insane.bluepwn;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.paperdb.Paper;

import static io.eberlein.insane.bluepwn.Static.TABLE_SERVICE;
import static io.eberlein.insane.bluepwn.Static.TABLE_STAGER;

public class Stager {
    List<Stage> stages;
    List<String> services;
    String uuid;
    String name;
    String type;
    Date lastModified;

    Stager(){
        uuid = UUID.randomUUID().toString();
        name = "";
        type = "";
        lastModified = new Date();
        stages = new ArrayList<>();
        services = new ArrayList<>();
    }

    Stager(String service){
        uuid = UUID.randomUUID().toString();
        services = new ArrayList<>();
        services.add(service);
        name = "";
        type = "";
        lastModified = new Date();
    }

    Stager(String uuid, String name, String type, Date lastModified, List<Stage> stages, List<String> services){
        this.uuid = uuid;
        this.name = name;
        this.type = type;
        this.lastModified = lastModified;
        this.stages = stages;
        this.services = services;
    }

    void save(){
        Paper.book(TABLE_STAGER).write(uuid, this);
    }

    static Stager get(@Nullable String uuid){
        if(uuid == null) return new Stager();
        Stager s = Paper.book(TABLE_STAGER).read(uuid);
        if(s != null) return s;
        return new Stager();
    }

    static List<Stager> get(){
        List<Stager> stagers = new ArrayList<>();
        for(String s : Paper.book(TABLE_STAGER).getAllKeys()) stagers.add(Paper.book(TABLE_STAGER).read(s));
        return stagers;
    }
}
