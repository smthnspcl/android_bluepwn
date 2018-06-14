package io.eberlein.insane.bluepwn;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.paperdb.Paper;

import static io.eberlein.insane.bluepwn.Static.TABLE_STAGE;

public class Stage {
    String id;
    String name;
    String data;

    String dataType;

    void save(){
        Paper.book(TABLE_STAGE).write(id, this);
    }

    static Stage get(String id){
        if(id != null) return Paper.book(TABLE_STAGE).read(id);
        return new Stage();
    }

    static List<Stage> get(){
        List<Stage> stages = new ArrayList<>();
        for(String s : Paper.book(TABLE_STAGE).getAllKeys()) stages.add(Paper.book(TABLE_STAGE).read(s));
        return stages;
    }

    Stage(){
        id = UUID.randomUUID().toString();
        name = "";
        data = "";
        dataType = "";
    }

    Stage(String id, String name){
        this.id = id;
        this.name = name;
    }
}
