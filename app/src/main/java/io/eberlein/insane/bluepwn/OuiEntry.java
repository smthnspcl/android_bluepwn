package io.eberlein.insane.bluepwn;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

@Table(database = LocalDatabase.class)
public class OuiEntry {
    @Column String registry;
    @PrimaryKey String assignment;
    @Column String organizationname;
    @Column String organizationaddress;

    OuiEntry(){}
    OuiEntry(String registry, String assignment, String organizationname, String organizationaddress){
        this.registry = registry;
        this.assignment = assignment;
        this.organizationaddress = organizationaddress;
        this.organizationname = organizationname;
    }
}