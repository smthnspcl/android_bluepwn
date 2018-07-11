package io.eberlein.insane.bluepwn;

import java.util.concurrent.TimeUnit;

import io.paperdb.Paper;

import static io.eberlein.insane.bluepwn.Static.KEY_SCAN_SETTINGS;
import static io.eberlein.insane.bluepwn.Static.TABLE_SETTINGS;
import static io.eberlein.insane.bluepwn.Static.TYPE_LE;

public class ScanSettings {
    String prioritize;
    Boolean gattAutoConnect;
    Integer gattScanTimeout;
    TimeUnit gattScanTimeoutUnit;


    ScanSettings(){
        prioritize = TYPE_LE;
        gattAutoConnect = false;
        gattScanTimeout = 5;
        gattScanTimeoutUnit = TimeUnit.SECONDS;
    }

    void save(){
        Paper.book(TABLE_SETTINGS).write(KEY_SCAN_SETTINGS, this);
    }

    static ScanSettings get(){
        return Paper.book(TABLE_SETTINGS).read(KEY_SCAN_SETTINGS);
    }

    static ScanSettings getExistingOrNew(){
        ScanSettings s = get();
        if(s == null) return new ScanSettings();
        return s;
    }
}
