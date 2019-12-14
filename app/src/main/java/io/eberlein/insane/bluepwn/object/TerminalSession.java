package io.eberlein.insane.bluepwn.object;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.paperdb.Paper;

import static io.eberlein.insane.bluepwn.Static.TABLE_TERMINALSESSION;

public class TerminalSession extends DBObject {
    private List<String> cmds;
    private List<String> responses;

    public TerminalSession() {
        super(UUID.randomUUID().toString());
        cmds = new ArrayList<>();
        responses = new ArrayList<>();
    }

    public static TerminalSession get(String uuid) {
        TerminalSession s = null;
        if(uuid != null) s = Paper.book(TABLE_TERMINALSESSION).read(uuid);
        if(s == null) s = new TerminalSession();
        return s;
    }

    public void save() {
        Paper.book(TABLE_TERMINALSESSION).write(getUuid(), this);
    }

    public void addCommand(String cmd) {
        cmds.add(cmd);
    }

    public List<String> getResponses() {
        return responses;
    }
}
