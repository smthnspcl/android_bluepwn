package io.eberlein.insane.bluepwn;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

import static io.eberlein.insane.bluepwn.Static.TABLE_CHARACTERISTIC;
import static io.eberlein.insane.bluepwn.Static.classToTable;


public class DatabaseObject {
    String uuid;

    DatabaseObject(String uuid){
        this.uuid = uuid;
    }

    void save(){
        Paper.book(classToTable(getClass())).write(uuid, this);
    }

    static List<Characteristic> get(){
        List<Characteristic> characteristics = new ArrayList<>();
        for(String c : Paper.book(TABLE_CHARACTERISTIC).getAllKeys()) characteristics.add(Paper.book(TABLE_CHARACTERISTIC).read(c));
        return characteristics;
    }
}
