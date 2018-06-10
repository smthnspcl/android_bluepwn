package io.eberlein.insane.bluepwn;

import java.util.UUID;

import io.paperdb.Paper;

public class Stage {
    String id;
    String name;

    void save(){
        Paper.book("stage").write(id, this);
    }

    Stage(){
        id = UUID.randomUUID().toString();
    }

    Stage(String id, String name){
        this.id = id;
        this.name = name;
    }
}
