package io.eberlein.insane.bluepwn;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.List;

import static io.eberlein.insane.bluepwn.Static.EVENT_DEVICE_DISCOVERED;
import static io.eberlein.insane.bluepwn.Static.EVENT_DISCOVERY_FINISHED;
import static io.eberlein.insane.bluepwn.Static.EVENT_DISCOVERY_STARTED;
import static io.eberlein.insane.bluepwn.Static.EVENT_GATT_SCAN_FINISHED;
import static io.eberlein.insane.bluepwn.Static.EVENT_GOT_COOKIE;
import static io.eberlein.insane.bluepwn.Static.EVENT_SDP_SCAN_FINISHED;
import static io.eberlein.insane.bluepwn.Static.EVENT_START_SCANNING;
import static io.eberlein.insane.bluepwn.Static.EVENT_STOP_SCANNING;
import static io.eberlein.insane.bluepwn.Static.EVENT_STOP_SERVICE_SCAN;
import static io.eberlein.insane.bluepwn.Static.EVENT_TO_SCAN_DEVICES_EMPTY;

public class Event {
    public final Integer event;

    public Event(Integer event){
        this.event = event;
    }
}

class EventDiscoveryStarted extends Event {
    public EventDiscoveryStarted(){
        super(EVENT_DISCOVERY_STARTED);
    }
}

class EventDeviceDiscovered extends Event {

    public final Device device;

    public EventDeviceDiscovered(Device device){
        super(EVENT_DEVICE_DISCOVERED);
        this.device = device;
    }
}

class EventSDPScanFinished {
    public final Device device;
    public EventSDPScanFinished(Device device){
        this.device = device;
    }
}

class EventGATTScanFinished {
    public final Device device;

    public EventGATTScanFinished(Device device){
        this.device = device;
    }
}

class EventToScanDevicesEmpty extends Event {
    public EventToScanDevicesEmpty(){
        super(EVENT_TO_SCAN_DEVICES_EMPTY);
    }
}

class EventSetContinuousScanning {
    public final Boolean value;

    public EventSetContinuousScanning(Boolean value){
        this.value = value;
    }
}

class EventSetPrioritize {
    public final String value;

    public EventSetPrioritize(String value){
        this.value = value;
    }
}

class EventStopScanning extends Event {

    public EventStopScanning(){
        super(EVENT_STOP_SCANNING);
    }
}

class EventStartScanning extends Event {
    public EventStartScanning(){
        super(EVENT_START_SCANNING);
    }
}

class EventStopServiceScan extends Event {
    public EventStopServiceScan(){super(EVENT_STOP_SERVICE_SCAN);}
}

class EventGotCookie {
    public final String cookie;

    public EventGotCookie(String cookie){
        this.cookie = cookie;
    }
}

class EventGotDifference {
    public final String table;
    public final JsonObject results;

    public EventGotDifference(String table, JsonObject results) {
        this.table = table;
        this.results = results;
    }
}

class EventSyncFailed {
    public final String table;
    public final String msg;

    public EventSyncFailed (String table, String msg){
        this.table = table;
        this.msg = msg;
    }
}

class EventImportResults {
    public final String table;
    public final JsonArray results;

    public EventImportResults(String table, JsonArray results){
        this.table = table;
        this.results = results;
    }
}

class EventImportResultsFailed {
    public final String table;
    public final String msg;

    public EventImportResultsFailed(String table, String msg){
        this.table = table;
        this.msg = msg;
    }
}

class EventExportResult {
    public final String table;
    public final String msg;

    public EventExportResult(String table, String msg){
        this.table = table;
        this.msg = msg;
    }
}

class EventExportResultsFailed {
    public final String table;
    public final String msg;

    public EventExportResultsFailed(String table, String msg){
        this.table = table;
        this.msg = msg;
    }
}

class EventScanStarted {
    public EventScanStarted(){}
}

class EventScanStopped {
    public EventScanStopped(){}
}