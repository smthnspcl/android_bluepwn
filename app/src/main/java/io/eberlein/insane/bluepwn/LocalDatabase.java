package io.eberlein.insane.bluepwn;


import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.annotation.Database;

@Database(name = LocalDatabase.NAME, version = LocalDatabase.VERSION, insertConflict = ConflictAction.REPLACE, updateConflict = ConflictAction.REPLACE)
public class LocalDatabase {
    public static final String NAME = "bluepwn";
    public static final int VERSION = 1;
}
