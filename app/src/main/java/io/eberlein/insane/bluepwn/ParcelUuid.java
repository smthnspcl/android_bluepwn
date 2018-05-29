package io.eberlein.insane.bluepwn;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ParcelUuid {
    String name;
    String description;
    String protocol; // new action: specify uuid and use protocol space holders // list of rows ( stages ) ?
    Date lastModified;
    android.os.ParcelUuid uuid;

    List<String> actions;

    ParcelUuid(){
        actions = new ArrayList<>();
    }

    ParcelUuid(android.os.ParcelUuid parcelUuid){
        uuid = parcelUuid;
        lastModified = new Date();
        actions = new ArrayList<>();
    }
}
