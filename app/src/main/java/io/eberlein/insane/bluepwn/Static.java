package io.eberlein.insane.bluepwn;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Static {

    static final String ACTION_DATABASE_IMPORT_RESULTS = "database_import_results";
    static final String ACTION_DATABASE_IMPORT_RESULTS_FAILED = "database_import_results_failed";
    static final String ACTION_DATABASE_EXPORT_RESULTS = "export_results";
    static final String ACTION_DATABASE_EXPORT_RESULTS_FAILED = "export_results_failed";
    static final String ACTION_DATABASE_GOT_COOKIE = "got_cookie";
    static final String ACTION_DATABASE_GOT_DIFFERENCE = "got_difference";
    static final String ACTION_DATABASE_GET_DIFFERENCE_FAILED = "get_difference_failed";

    static final String ACTION_SCANNER_INITIALIZED = "scanner_initialized";
    static final String ACTION_START_SCAN = "start_scan";
    static final String ACTION_SCAN_STARTED = "scan_started";
    static final String ACTION_SCAN_STOPPED = "scan_stopped";
    static final String ACTION_SCAN_FINISHED = "scan_finished";
    static final String ACTION_ALREADY_SCANNING = "already_scanning";
    static final String ACTION_DEVICE_DISCOVERED = "device_discovered";
    static final String ACTION_SERVICE_DISCOVERED = "service_discovered";
    static final String ACTION_STOP_SCAN = "stop_scan";
    static final String ACTION_STOP_DISCOVERY = "stop_discovery";
    static final String ACTION_DISCOVERY_STARTED = "discovery_started";
    static final String ACTION_DISCOVERY_STOPPED = "discovery_stopped";
    static final String ACTION_DISCOVERY_FINISHED = "discovery_finished";
    static final String ACTION_DISCOVERY_ALREADY_STOPPED = "discovery_already_stopped";

    static final String ACTION_DATA_KEY = "data";

    static final String TABLE_STAGE = "stage";
    static final String TABLE_STAGER = "stager";
    static final String TABLE_SERVICE = "service";
    static final String TABLE_SCAN = "scan";
    static final String TABLE_OUI = "oui";
    static final String TABLE_LOCATION = "location";
    static final String TABLE_DEVICE = "device";
    static final String TABLE_DESCRIPTOR = "descriptor";
    static final String TABLE_CHARACTERISTIC = "characteristic";
    static final String TABLE_SETTINGS = "settings";
    static final String TABLE_NOTIFICATION = "notification";
    static final String TABLE_TERMINALSESSION = "terminalsession";

    static final String[] TABLES = {
            TABLE_STAGE, TABLE_STAGER, TABLE_SERVICE, TABLE_SCAN, TABLE_OUI, TABLE_LOCATION,
            TABLE_DEVICE, TABLE_DESCRIPTOR, TABLE_CHARACTERISTIC
    };

    static final String[] STAGE_KEYS = {"uuid", "name", "data", "dataType"};
    static final String[] STAGER_KEYS = {"uuid", "name", "type", "lastModified"};
    static final String[] SERVICE_KEYS = {"uuid", "name", "description"};
    static final String[] SCAN_KEYS = {"uuid", "timestamp"};
    static final String[] OUI_KEYS = {"registry", "assignment", "organizationname", "organizationaddress"};
    static final String[] LOCATION_KEYS = {"uuid", "speed", "country", "city", "street"};
    static final String[] DEVICE_KEYS = {"address", "name", "manufacturer", "type", "bond", "lastModified"};
    static final String[] DESCRIPTOR_KEYS = {"uuid", "name", "permissions"};
    static final String[] CHARACTERISTIC_KEYS = {"name", "writeType", "properties", "uuid"};
    static final String[] NOTIFICATION_KEYS = {"uuid", "table"};
    static final String[] TERMINALSESSION_KEYS = {"uuid", "cmds", "responses"};

    static final String DB_WANT = "want";
    static final String DB_HAVE = "have";

    static final String KEY_DELIMITER = "_";

    static Class tableToClass(String table) {
        switch (table){
            case TABLE_STAGE: return Stage.class;
            case TABLE_STAGER: return Stager.class;
            case TABLE_SERVICE: return Service.class;
            case TABLE_SCAN: return Scan.class;
            case TABLE_OUI: return OuiEntry.class;
            case TABLE_LOCATION: return Location.class;
            case TABLE_DEVICE: return Device.class;
            case TABLE_DESCRIPTOR: return Descriptor.class;
            case TABLE_CHARACTERISTIC: return Characteristic.class;
            default: return null;
        }
    }

    static String classToTable(Class c){
        switch (c.getSimpleName()){
            case "RemoteDBSettings": return TABLE_SETTINGS;
            case "Characteristic": return TABLE_CHARACTERISTIC;
            case "Descriptor": return TABLE_DESCRIPTOR;
            case "Device": return TABLE_DEVICE;
            case "Location": return TABLE_LOCATION;
            case "OuiEntry": return TABLE_OUI;
            case "Scan": return TABLE_SCAN;
            case "Service": return TABLE_SERVICE;
            case "Stager": return TABLE_STAGER;
            case "Stage": return TABLE_STAGE;
            default: return null;
        }
    }

    static final String KEY_REMOTE_DATABASE_SETTINGS = "remote";
    static final String KEY_SCAN_SETTINGS = "scan";

    static final Integer EVENT_DISCOVERY_STARTED = 0;
    static final Integer EVENT_DISCOVERY_FINISHED = 1;
    static final Integer EVENT_DEVICE_DISCOVERED = 2;
    static final Integer EVENT_SDP_SCAN_FINISHED = 3;
    static final Integer EVENT_GATT_SCAN_FINISHED = 4;
    static final Integer EVENT_TO_SCAN_DEVICES_EMPTY = 5;
    static final Integer EVENT_START_SCANNING = 6;
    static final Integer EVENT_STOP_SCANNING = 7;
    static final Integer EVENT_GOT_COOKIE = 8;
    static final Integer EVENT_STOP_SERVICE_SCAN = 9;

    static final int BLUETOOTH_RESULT = 0;
    static final int LOCATION_RESULT = 1;

    static final String TYPE_DUAL = "dual";
    static final String TYPE_LE = "le";
    static final String TYPE_CLASSIC = "classic";
    static final String TYPE_UNKNOWN = "unknown";

    static final String BOND_BONDED = "bonded";
    static final String BOND_BONDING = "bonding";
    static final String BOND_UNKNOWN = "unknown";
    static final String BOND_NONE = "none";

    static final String URL_TABLE_VARIABLE = "{{TBL}}";
    static final String URL_AUTHENTICATE = "api/authenticate";
    static final String URL_TABLE_DIFFERENCE = "api/{{TBL}}/difference";
    static final String URL_TABLE_GET = "api/{{TBL}}/get";
    static final String URL_TABLE_SET = "api/{{TBL}}/set";

    static final String DATE_FORMAT = "HH:mm:ss.SSS-dd.MM.yyyy";

    static String date2String(Date date){
        return new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(date);
    }

    static List<String> jsonArrayToStringList(JsonArray l){
        List<String> r = new ArrayList<>();
        for(JsonElement e : l) r.add(e.getAsString());
        return r;
    }

    static void send2BcR(Context c, String action, @Nullable String data){
        Intent i = new Intent(action);
        if(data != null) i.putExtra(ACTION_DATA_KEY, data);
        c.sendBroadcast(i);
    }
}
