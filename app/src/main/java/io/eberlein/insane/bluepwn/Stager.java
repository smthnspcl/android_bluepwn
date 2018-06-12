package io.eberlein.insane.bluepwn;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.paperdb.Paper;

public class Stager {
    List<Stage> stages;
    String id;
    String name;
    String type;
    Date lastModified;

    Stager(){
        id = UUID.randomUUID().toString();
        name = "";
        type = "";
        lastModified = new Date();
        stages = new ArrayList<>();
    }

    Stager(String id, String name, String type, Date lastModified, List<Stage> stages){
        this.id = id;
        this.name = name;
        this.type = type;
        this.lastModified = lastModified;
        this.stages = stages;
    }

    void save(){
        Paper.book("stager").write(id, this);
    }

    static Stager get(String id){
        return Paper.book("stager").read(id);
    }

    static List<Stager> get(){
        List<Stager> stagers = new ArrayList<>();
        for(String s : Paper.book("stager").getAllKeys()) stagers.add(Paper.book("stager").read(s));
        return stagers;
    }
}
