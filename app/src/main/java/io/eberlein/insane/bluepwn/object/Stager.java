package io.eberlein.insane.bluepwn.object;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.paperdb.Paper;

import static io.eberlein.insane.bluepwn.Static.TABLE_STAGER;

public class Stager extends DBObject {
    private List<Stage> stages;
    private List<String> services;
    private String name;
    private String type;

    public Stager() {
        super(UUID.randomUUID().toString());
        name = "";
        type = "";
        stages = new ArrayList<>();
        services = new ArrayList<>();
    }

    public Stager(String service) {
        super(UUID.randomUUID().toString());
        services = new ArrayList<>();
        services.add(service);
        name = "";
        type = "";
    }

    public Stager(String uuid, String name, String type, Date lastModified, List<Stage> stages, List<String> services) {
        super(uuid);
        this.name = name;
        this.type = type;
        this.stages = stages;
        this.services = services;
    }

    public static Stager get(@Nullable String uuid) {
        if(uuid == null) return new Stager();
        Stager s = Paper.book(TABLE_STAGER).read(uuid);
        if(s != null) return s;
        return new Stager();
    }

    public static List<Stager> get() {
        List<Stager> stagers = new ArrayList<>();
        for(String s : Paper.book(TABLE_STAGER).getAllKeys()) stagers.add(Paper.book(TABLE_STAGER).read(s));
        return stagers;
    }

    public void save() {
        Paper.book(TABLE_STAGER).write(getUuid(), this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Stage> getStages() {
        return stages;
    }

    public void setStages(List<Stage> stages) {
        this.stages = stages;
    }

    public List<String> getServices() {
        return services;
    }

    public void setServices(List<String> services) {
        this.services = services;
    }
}
