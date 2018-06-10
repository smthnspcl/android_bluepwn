package io.eberlein.insane.bluepwn;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Stager {
    List<Stage> stages;
    String id;
    String name;
    String type;
    Date lastModified;

    Stager(){
        id = UUID.randomUUID().toString();
        stages = new ArrayList<>();
    }

    Stager(String id, String name, String type, Date lastModified, List<Stage> stages){
        this.id = id;
        this.name = name;
        this.type = type;
        this.lastModified = lastModified;
        this.stages = stages;
    }
}
