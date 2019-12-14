package io.eberlein.insane.bluepwn.object;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

import static io.eberlein.insane.bluepwn.Static.KEY_DELIMITER;
import static io.eberlein.insane.bluepwn.Static.TABLE_NOTIFICATION;

public class Notification extends DBObject {
    private String table;

    public Notification(String uuid, String table) {
        super(uuid);
        this.table = table;
    }

    public static List<Notification> getByTable(String table) {
        List<Notification> notifications = new ArrayList<>();
        for(String k : Paper.book(TABLE_NOTIFICATION).getAllKeys()) notifications.add(Paper.book(TABLE_NOTIFICATION).read(k));
        return notifications;
    }

    public static Notification get(String table, String uuid) {
        Notification n = Paper.book(TABLE_NOTIFICATION).read(table + KEY_DELIMITER + uuid);
        if(n == null) n = new Notification(uuid, table);
        return n;
    }

    public static boolean exists(String table, String uuid) {
        return Paper.book(TABLE_NOTIFICATION).read(table + KEY_DELIMITER + uuid) != null;
    }

    public static void delete(String table, String uuid) {
        Paper.book(TABLE_NOTIFICATION).delete(table + KEY_DELIMITER + uuid);
    }

    public void save() {
        Paper.book(TABLE_NOTIFICATION).write(this.table + KEY_DELIMITER + getUuid(), this);
    }
}
