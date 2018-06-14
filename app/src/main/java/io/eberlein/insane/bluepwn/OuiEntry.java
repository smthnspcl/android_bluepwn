package io.eberlein.insane.bluepwn;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

import static io.eberlein.insane.bluepwn.Static.TABLE_OUI;

public class OuiEntry {
    String registry;
    String assignment;
    String organizationname;
    String organizationaddress;

    OuiEntry(){}

    OuiEntry(String registry, String assignment, String organizationname, String organizationaddress){
        this.registry = registry;
        this.assignment = assignment;
        this.organizationaddress = organizationaddress;
        this.organizationname = organizationname;
    }

    void save(){
        Paper.book(TABLE_OUI).write(assignment, this);
    }

    static OuiEntry get(String assignment){
        return Paper.book(TABLE_OUI).read(assignment);
    }

    static List<OuiEntry> get(){
        List<OuiEntry> ouiEntries = new ArrayList<>();
        for(String o : Paper.book(TABLE_OUI).getAllKeys()) ouiEntries.add(Paper.book(TABLE_OUI).read(o));
        return ouiEntries;
    }
}