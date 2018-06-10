package io.eberlein.insane.bluepwn;

import java.util.UUID;

public class Stage {
    String id;


    Stage(){
        id = UUID.randomUUID().toString();
    }
}
