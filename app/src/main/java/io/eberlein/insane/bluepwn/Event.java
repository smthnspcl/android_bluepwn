package io.eberlein.insane.bluepwn;

import static io.eberlein.insane.bluepwn.Static.EVENT_DEVICE_DISCOVERED;
import static io.eberlein.insane.bluepwn.Static.EVENT_DISCOVERY_FINISHED;
import static io.eberlein.insane.bluepwn.Static.EVENT_DISCOVERY_STARTED;
import static io.eberlein.insane.bluepwn.Static.EVENT_GATT_SCAN_FINISHED;
import static io.eberlein.insane.bluepwn.Static.EVENT_SDP_SCAN_FINISHED;
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

class EventDiscoveryFinished extends Event {
    public EventDiscoveryFinished(){
        super(EVENT_DISCOVERY_FINISHED);
    }
}

class EventDeviceDiscovered extends Event {

    public final Device device;

    public EventDeviceDiscovered(Device device){
        super(EVENT_DEVICE_DISCOVERED);
        this.device = device;
    }
}

class EventSDPScanFinished extends Event {
    public EventSDPScanFinished(){
        super(EVENT_SDP_SCAN_FINISHED);
    }
}

class EventGATTScanFinished extends Event {
    public EventGATTScanFinished(){
        super(EVENT_GATT_SCAN_FINISHED);
    }
}

class EventToScanDevicesEmpty extends Event {
    public EventToScanDevicesEmpty(){
        super(EVENT_TO_SCAN_DEVICES_EMPTY);
    }
}