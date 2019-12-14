package io.eberlein.insane.bluepwn.object;

import com.movisens.smartgattlib.Services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.paperdb.Paper;

import static io.eberlein.insane.bluepwn.Static.TABLE_CHARACTERISTIC;
import static io.eberlein.insane.bluepwn.Static.TABLE_SERVICE;
import static io.eberlein.insane.bluepwn.Static.TABLE_STAGER;

public class Service extends DBObject {
    private String name;
    private String description;

    private List<String> characteristics;
    private List<String> stagers;

    public Service() {
        super(UUID.randomUUID().toString());
        characteristics = new ArrayList<>();
        name = "";
        description = "";
        stagers = new ArrayList<>();
    }

    public Service(UUID uuid) {
        super(uuid.toString());
        this.name = Services.lookup(uuid).getName();
        this.description = "";
        stagers = new ArrayList<>();
    }

    public Service(String uuid) {
        super(uuid);
        this.name = Services.lookup(UUID.fromString(uuid)).getName();
        this.description = "";
        characteristics = new ArrayList<>();
        stagers = new ArrayList<>();
    }

    public Service(String uuid, String name) {
        super(uuid);
        this.name = name;
        this.description = "";
        characteristics = new ArrayList<>();
        stagers = new ArrayList<>();
    }

    public Service(String uuid, String name, String description) {
        super(uuid);
        this.name = name;
        this.description = description;
        characteristics = new ArrayList<>();
        stagers = new ArrayList<>();
    }

    public Service(String uuid, String name, String description, List<String> characteristics) {
        super(uuid);
        this.name = name;
        this.description = description;
        this.characteristics = characteristics;
        stagers = new ArrayList<>();
    }

    public Service(String uuid, String name, String description, List<String> characteristics, List<String> stagers) {
        super(uuid);
        this.name = name;
        this.description = description;
        this.characteristics = characteristics;
        this.stagers = stagers;
    }

    public static Service getExistingOrNew(String uuid) {
        Service s = Paper.book(TABLE_SERVICE).read(uuid);
        if(s == null) return new Service(uuid);
        else return s;
    }

    public static Service get(String uuid) {
        if(uuid != null) return Paper.book(TABLE_SERVICE).read(uuid);
        return null;
    }

    public static List<Service> get() {
        List<Service> services = new ArrayList<>();
        for(String s : Paper.book(TABLE_SERVICE).getAllKeys()) services.add(Paper.book(TABLE_SERVICE).read(s));
        return services;
    }

    public List<Characteristic> getCharacteristics() {
        List<Characteristic> characteristics = new ArrayList<>();
        for (String c : this.characteristics)
            characteristics.add(Paper.book(TABLE_CHARACTERISTIC).read(c));
        return characteristics;
    }

    public List<Stager> getStagers() {
        List<Stager> stagers = new ArrayList<>();
        for (String s : this.stagers) stagers.add(Paper.book(TABLE_STAGER).read(s));
        return stagers;
    }

    public void save() {
        Paper.book(TABLE_SERVICE).write(getUuid(), this);
    }

    public void updateCharacteristics(Characteristic c) {
        if (!characteristics.contains(c.getUuid())) characteristics.add(c.getUuid());
    }

    void updateCharacteristics(Characteristic[] cA){
        for(Characteristic c : cA) updateCharacteristics(c);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void addStager(Stager s) {
        if (!stagers.contains(s.getUuid())) stagers.add(s.getUuid());
    }
}
