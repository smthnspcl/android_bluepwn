package io.eberlein.insane.bluepwn;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.eberlein.insane.bluepwn.object.Characteristic;
import io.eberlein.insane.bluepwn.object.Descriptor;
import io.eberlein.insane.bluepwn.object.Device;
import io.eberlein.insane.bluepwn.object.ILocation;
import io.eberlein.insane.bluepwn.object.OuiEntry;
import io.eberlein.insane.bluepwn.object.Scan;
import io.eberlein.insane.bluepwn.object.Service;
import io.eberlein.insane.bluepwn.object.Stage;
import io.eberlein.insane.bluepwn.object.Stager;

public class Static {
    public static final String TABLE_STAGE = "stage";
    public static final String TABLE_STAGER = "stager";
    public static final String TABLE_SERVICE = "service";
    public static final String TABLE_SCAN = "scan";
    public static final String TABLE_OUI = "oui";
    public static final String TABLE_LOCATION = "location";
    public static final String TABLE_DEVICE = "device";
    public static final String TABLE_DESCRIPTOR = "descriptor";
    public static final String TABLE_CHARACTERISTIC = "characteristic";
    public static final String TABLE_SETTINGS = "settings";
    public static final String TABLE_NOTIFICATION = "notification";
    public static final String TABLE_TERMINALSESSION = "terminalsession";
    public static final String[] TABLES = {
            TABLE_STAGE, TABLE_STAGER, TABLE_SERVICE, TABLE_SCAN, TABLE_OUI, TABLE_LOCATION,
            TABLE_DEVICE, TABLE_DESCRIPTOR, TABLE_CHARACTERISTIC
    };
    public static final String[] STAGE_KEYS = {"uuid", "name", "data", "dataType"};
    public static final String[] STAGER_KEYS = {"uuid", "name", "type", "lastModified"};
    public static final String[] SERVICE_KEYS = {"uuid", "name", "description"};
    public static final String[] SCAN_KEYS = {"uuid", "timestamp"};
    public static final String[] OUI_KEYS = {"registry", "assignment", "organizationname", "organizationaddress"};
    public static final String[] LOCATION_KEYS = {"uuid", "speed", "country", "city", "street"};
    public static final String[] DEVICE_KEYS = {"address", "name", "manufacturer", "type", "bond", "lastModified"};
    public static final String[] DESCRIPTOR_KEYS = {"uuid", "name", "permissions"};
    public static final String[] CHARACTERISTIC_KEYS = {"name", "writeType", "properties", "uuid"};
    public static final String[] NOTIFICATION_KEYS = {"uuid", "table"};
    public static final String[] TERMINALSESSION_KEYS = {"uuid", "cmds", "responses"};
    public static final String DB_WANT = "want";
    public static final String DB_HAVE = "have";
    public static final String KEY_DELIMITER = "_";
    public static final String KEY_REMOTE_DATABASE_SETTINGS = "remote";
    public static final String KEY_SCAN_SETTINGS = "scan";
    public static final int BLUETOOTH_RESULT = 0;
    public static final int LOCATION_RESULT = 1;
    public static final String TYPE_DUAL = "dual";
    public static final String TYPE_LE = "le";
    public static final String TYPE_CLASSIC = "classic";
    public static final String TYPE_UNKNOWN = "unknown";
    public static final String BOND_BONDED = "bonded";
    public static final String BOND_BONDING = "bonding";
    public static final String BOND_UNKNOWN = "unknown";
    public static final String BOND_NONE = "none";
    public static final String URL_TABLE_VARIABLE = "{{TBL}}";
    public static final String URL_AUTHENTICATE = "api/authenticate";
    public static final String URL_TABLE_DIFFERENCE = "api/{{TBL}}/difference";
    public static final String URL_TABLE_GET = "api/{{TBL}}/get";
    public static final String URL_TABLE_SET = "api/{{TBL}}/set";
    public static final String DATE_FORMAT = "HH:mm:ss.SSS-dd.MM.yyyy";

    public static Class tableToClass(String table) {
        switch (table){
            case TABLE_STAGE: return Stage.class;
            case TABLE_STAGER: return Stager.class;
            case TABLE_SERVICE: return Service.class;
            case TABLE_SCAN: return Scan.class;
            case TABLE_OUI: return OuiEntry.class;
            case TABLE_LOCATION:
                return ILocation.class;
            case TABLE_DEVICE: return Device.class;
            case TABLE_DESCRIPTOR: return Descriptor.class;
            case TABLE_CHARACTERISTIC: return Characteristic.class;
            default: return null;
        }
    }

    public static String classToTable(Class c) {
        switch (c.getSimpleName()){
            case "RemoteDBSettings": return TABLE_SETTINGS;
            case "Characteristic": return TABLE_CHARACTERISTIC;
            case "Descriptor": return TABLE_DESCRIPTOR;
            case "Device": return TABLE_DEVICE;
            case "ILocation":
                return TABLE_LOCATION;
            case "OuiEntry": return TABLE_OUI;
            case "Scan": return TABLE_SCAN;
            case "Service": return TABLE_SERVICE;
            case "Stager": return TABLE_STAGER;
            case "Stage": return TABLE_STAGE;
            default: return null;
        }
    }

    public static String date2String(Date date) {
        return new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(date);
    }

    public static List<String> jsonArrayToStringList(JsonArray l) {
        List<String> r = new ArrayList<>();
        for(JsonElement e : l) r.add(e.getAsString());
        return r;
    }

    public class action {
        public static final String PKGNAME = "io.eberlein.bluepwn.";
        public static final String ACTION_DATA_KEY = "data";
        public static final String ACTION_CODE_KEY = "action_code";

        public class scanner {
            public static final String ACTION_SCANNER_INFO = PKGNAME + "scanner_info";
            public static final String ACTION_SCANNER_CMD = PKGNAME + "scanner_command";

            public class codes {
                public static final String P = "scanner_info_";

                public static final String ACTION_CODE_START_DISCOVERY = PKGNAME + P + "start_discovery";
                public static final String ACTION_CODE_STOP_DISCOVERY = PKGNAME + P + "stop_discovery";
                public static final String ACTION_CODE_STOP_SCAN = PKGNAME + P + "stop_scan";
                public static final String ACTION_CODE_GET_CURRENT_SCAN = PKGNAME + P + "get_current_scan";

                public static final String ACTION_CODE_CURRENT_SCAN = PKGNAME + P + "current_scan";
                public static final String ACTION_CODE_SCANNING_STARTED = PKGNAME + P + "scanning_started";
                public static final String ACTION_CODE_SCANNING_STOPPED = PKGNAME + P + "scanning_stopped";
                public static final String ACTION_CODE_SCANNING_FINISHED = PKGNAME + P + "scanning_finished";
                public static final String ACTION_CODE_DISCOVERY_STARTED = PKGNAME + P + "discovery_started";
                public static final String ACTION_CODE_DISCOVERY_STOPPED = PKGNAME + P + "discovery_stopped";
                public static final String ACTION_CODE_DISCOVERY_FINISHED = PKGNAME + P + "discovery_finished";
                public static final String ACTION_CODE_DEVICE_DISCOVERED = PKGNAME + P + "device_discovered";
                public static final String ACTION_CODE_SERVICE_DISCOVERED = PKGNAME + P + "service_discovered";

                public static final String ACTION_CODE_SERVICE_INITIALIZED = PKGNAME + P + "service_initialized";
            }
        }

        public class sync {
            public static final String P = "database_info_";

            public static final String ACTION_DATABASE_INFO = "database_info";

            public static final String ACTION_DATABASE_IMPORT_RESULTS = "database_import_results";
            public static final String ACTION_DATABASE_IMPORT_RESULTS_FAILED = "database_import_results_failed";
            public static final String ACTION_DATABASE_EXPORT_RESULTS = "export_results";
            public static final String ACTION_DATABASE_EXPORT_RESULTS_FAILED = "export_results_failed";
            public static final String ACTION_DATABASE_GOT_COOKIE = "got_cookie";
            public static final String ACTION_DATABASE_GOT_DIFFERENCE = "got_difference";
            public static final String ACTION_DATABASE_GET_DIFFERENCE_FAILED = "get_difference_failed";
        }
    }
}
