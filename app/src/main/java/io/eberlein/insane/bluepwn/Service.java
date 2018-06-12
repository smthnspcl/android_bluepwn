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
    List<String> stagers;

    List<Characteristic> getCharacteristics(){
        List<Characteristic> characteristics = new ArrayList<>();
        for(String c : this.characteristics) characteristics.add(Paper.book("characteristic").read(c));
        return characteristics;
    }

    List<Stager> getStagers(){
        List<Stager> stagers = new ArrayList<>();
        for(String s : this.stagers) stagers.add(Paper.book("stager").read(s));
        return stagers;
    }

    Service(){
        characteristics = new ArrayList<>();
        uuid = UUID.randomUUID().toString();
        name = "";
        description = "";
        stagers = new ArrayList<>();
    }

    Service(String uuid){
        this.uuid = uuid;
        this.name = "";
        this.description = "";
        characteristics = new ArrayList<>();
        stagers = new ArrayList<>();
    }

    Service(String uuid, String name){
        this.uuid = uuid;
        this.name = name;
        this.description = "";
        characteristics = new ArrayList<>();
        stagers = new ArrayList<>();
    }

    Service(String uuid, String name, String description){
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        characteristics = new ArrayList<>();
        stagers = new ArrayList<>();
    }

    Service(String uuid, String name, String description, List<String> characteristics){
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.characteristics = characteristics;
        stagers = new ArrayList<>();
    }

    Service(String uuid, String name, String description, List<String> characteristics, List<String> stagers){
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.characteristics = characteristics;
        this.stagers = stagers;
    }

    static Service getExistingOrNew(String uuid){
        Service s = Paper.book("service").read(uuid);
        if(s == null) return new Service(uuid);
        else return s;
    }

    void save(){
        Paper.book("service").write(uuid, this);
    }

    static Service get(String uuid){
        return Paper.book("service").read(uuid);
    }

    static List<Service> get(){
        List<Service> services = new ArrayList<>();
        for(String s : Paper.book("service").getAllKeys()) services.add(Paper.book("service").read(s));
        return services;
    }
}
