package io.eberlein.insane.bluepwn;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class Service {
    String uuid;
    String name;

    List<String> characteristics;

    Service(){
        characteristics = new ArrayList<>();
    }

    Service(String uuid, String name){
        this.uuid = uuid;
        this.name = name;
        characteristics = new ArrayList<>();
    }

    static Service getExistingOrNew(String uuid){
        Service s = Paper.book("service").read(uuid);
        if(s == null) return new Service(uuid, "");
        else return s;
    }

    void save(){
        Paper.book("service").write(uuid, this);
    }
}
