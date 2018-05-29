package io.eberlein.insane.bluepwn;

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
}