package io.eberlein.insane.bluepwn;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.paperdb.Paper;

public class Service {
    String uuid;
    String name;
    String description;

    List<String> characteristics;

    Service(){
        characteristics = new ArrayList<>();
        uuid = UUID.randomUUID().toString();
        name = "";
        description = "";
    }

    Service(String uuid){
        this.uuid = uuid;
        this.name = "";
        this.description = "";
        characteristics = new ArrayList<>();
    }

    Service(String uuid, String name){
        this.uuid = uuid;
        this.name = name;
        this.description = "";
        characteristics = new ArrayList<>();
    }

    Service(String uuid, String name, String description){
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        characteristics = new ArrayList<>();
    }

    Service(String uuid, String name, String description, List<String> characteristics){
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.characteristics = characteristics;
    }

    static Service getExistingOrNew(String uuid){
        Service s = Paper.book("service").read(uuid);
        if(s == null) return new Service(uuid);
        else return s;
    }

    void save(){
        Paper.book("service").write(uuid, this);
    }
}
