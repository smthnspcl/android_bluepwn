package io.eberlein.insane.bluepwn;

public class Static {
    static final String TABLE_STAGE = "stage";
    static final String TABLE_STAGER = "stager";
    static final String TABLE_SERVICE = "service";
    static final String TABLE_SCAN = "scan";
    static final String TABLE_OUI = "oui";
    static final String TABLE_LOCATION = "location";
    static final String TABLE_DEVICE = "device";
    static final String TABLE_DESCRIPTOR = "descriptor";
    static final String TABLE_CHARACTERISTIC = "characteristic";

    static final Integer EVENT_DISCOVERY_STARTED = 0;
    static final Integer EVENT_DISCOVERY_FINISHED = 1;
    static final Integer EVENT_DEVICE_DISCOVERED = 2;
    static final Integer EVENT_SDP_SCAN_FINISHED = 3;
    static final Integer EVENT_GATT_SCAN_FINISHED = 4;
    static final Integer EVENT_TO_SCAN_DEVICES_EMPTY = 5;
    static final Integer EVENT_START_SCANNING = 6;
    static final Integer EVENT_STOP_SCANNING = 7;
    static final Integer EVENT_GOT_COOKIE = 8;

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
}
