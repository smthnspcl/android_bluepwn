package io.eberlein.insane.bluepwn;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

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
        Paper.book("oui").write(assignment, this);
    }

    static OuiEntry get(String assignment){
        return Paper.book("oui").read(assignment);
    }

    static List<OuiEntry> get(){
        List<OuiEntry> ouiEntries = new ArrayList<>();
        for(String o : Paper.book("oui").getAllKeys()) ouiEntries.add(Paper.book("oui").read(o));
        return ouiEntries;
    }
}