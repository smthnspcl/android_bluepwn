package io.eberlein.insane.bluepwn.object;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.paperdb.Paper;

import static io.eberlein.insane.bluepwn.Static.TABLE_STAGE;

public class Stage extends DBObject {
    private String name;
    private String data;
    private String dataType;

    public Stage() {
        super(UUID.randomUUID().toString());
        name = "";
        data = "";
        dataType = "";
    }

    public Stage(String id, String name) {
        super(id);
        this.name = name;
    }

    public static Stage get(String id) {
        if(id != null) return Paper.book(TABLE_STAGE).read(id);
        return new Stage();
    }

    public static List<Stage> get() {
        List<Stage> stages = new ArrayList<>();
        for(String s : Paper.book(TABLE_STAGE).getAllKeys()) stages.add(Paper.book(TABLE_STAGE).read(s));
        return stages;
    }

    public void save() {
        Paper.book(TABLE_STAGE).write(getUuid(), this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
}
