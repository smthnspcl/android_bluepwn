package io.eberlein.insane.bluepwn.object;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.paperdb.Paper;

import static io.eberlein.insane.bluepwn.Static.TABLE_OUI;

public class OuiEntry extends DBObject {
    private String registry;
    private String assignment;
    private String organizationname;
    private String organizationaddress;

    public OuiEntry() {
        super(UUID.randomUUID().toString());
    }

    public OuiEntry(String registry, String assignment, String organizationname, String organizationaddress) {
        super(UUID.randomUUID().toString());
        this.registry = registry;
        this.assignment = assignment;
        this.organizationaddress = organizationaddress;
        this.organizationname = organizationname;
    }

    public static OuiEntry get(String assignment) {
        return Paper.book(TABLE_OUI).read(assignment);
    }

    public static List<OuiEntry> get() {
        List<OuiEntry> ouiEntries = new ArrayList<>();
        for(String o : Paper.book(TABLE_OUI).getAllKeys()) ouiEntries.add(Paper.book(TABLE_OUI).read(o));
        return ouiEntries;
    }

    public void save() {
        Paper.book(TABLE_OUI).write(assignment, this);
    }

    public String getAssignment() {
        return assignment;
    }

    public String getOrganizationaddress() {
        return organizationaddress;
    }

    public String getOrganizationname() {
        return organizationname;
    }

    public String getRegistry() {
        return registry;
    }
}