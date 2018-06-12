package io.eberlein.insane.bluepwn;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.paperdb.Paper;

public class Stage {
    String id;
    String name;

    void save(){
        Paper.book("stage").write(id, this);
    }

    static Stage get(String id){
        return Paper.book("stage").read(id);
    }

    static List<Stage> get(){
        List<Stage> stages = new ArrayList<>();
        for(String s : Paper.book("stage").getAllKeys()) stages.add(Paper.book("stage").read(s));
        return stages;
    }

    Stage(){
        id = UUID.randomUUID().toString();
    }

    Stage(String id, String name){
        this.id = id;
        this.name = name;
    }
}
