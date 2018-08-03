package io.eberlein.insane.bluepwn;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.paperdb.Paper;

import static io.eberlein.insane.bluepwn.Static.TABLE_CHARACTERISTIC;
import static io.eberlein.insane.bluepwn.Static.TABLE_SERVICE;
import static io.eberlein.insane.bluepwn.Static.TABLE_STAGER;

public class Service {
    String uuid;
    String name;
    String description;

    List<String> characteristics;
    List<String> stagers;

    List<Characteristic> getCharacteristics(){
        List<Characteristic> characteristics = new ArrayList<>();
        for(String c : this.characteristics) characteristics.add(Paper.book(TABLE_CHARACTERISTIC).read(c));
        return characteristics;
    }

    List<Stager> getStagers(){
        List<Stager> stagers = new ArrayList<>();
        for(String s : this.stagers) stagers.add(Paper.book(TABLE_STAGER).read(s));
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
        Service s = Paper.book(TABLE_SERVICE).read(uuid);
        if(s == null) return new Service(uuid);
        else return s;
    }

    void save(){
        Paper.book(TABLE_SERVICE).write(uuid, this);
    }

    static Service get(String uuid){
        if(uuid != null) return Paper.book(TABLE_SERVICE).read(uuid);
        return null;
    }

    static List<Service> get(){
        List<Service> services = new ArrayList<>();
        for(String s : Paper.book(TABLE_SERVICE).getAllKeys()) services.add(Paper.book(TABLE_SERVICE).read(s));
        return services;
    }

    void updateCharacteristics(Characteristic c){
        if(!characteristics.contains(c.uuid)) characteristics.add(c.uuid);
    }

    void updateCharacteristics(Characteristic[] cA){
        for(Characteristic c : cA) updateCharacteristics(c);
    }
}
