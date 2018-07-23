package io.eberlein.insane.bluepwn;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.paperdb.Paper;

import static io.eberlein.insane.bluepwn.Static.TABLE_TERMINALSESSION;

public class TerminalSession {
    String uuid;
    List<String> cmds;

    TerminalSession(){
        uuid = UUID.randomUUID().toString();
        cmds = new ArrayList<>();
    }

    static TerminalSession get(String uuid){
        TerminalSession s = null;
        if(uuid != null) s = Paper.book(TABLE_TERMINALSESSION).read(uuid);
        if(s == null) s = new TerminalSession();
        return s;
    }

    void save(){
        Paper.book(TABLE_TERMINALSESSION).write(uuid, this);
    }
}
