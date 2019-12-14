package io.eberlein.insane.bluepwn.object;

import java.util.concurrent.TimeUnit;

import io.paperdb.Paper;

import static io.eberlein.insane.bluepwn.Static.KEY_SCAN_SETTINGS;
import static io.eberlein.insane.bluepwn.Static.TABLE_SETTINGS;
import static io.eberlein.insane.bluepwn.Static.TYPE_LE;

public class ScanSettings {
    private String prioritize;
    private Boolean gattAutoConnect;
    private Integer gattScanTimeout;
    private TimeUnit gattScanTimeoutUnit;
    private Boolean discoverServices;
    private Boolean continuousScanning;
    private Boolean autoPair;

    public ScanSettings() {
        prioritize = TYPE_LE;
        gattAutoConnect = false;
        gattScanTimeout = 5;
        gattScanTimeoutUnit = TimeUnit.SECONDS;
        discoverServices = true;
        continuousScanning = false;
        autoPair = false;
    }

    public static ScanSettings get() {
        return Paper.book(TABLE_SETTINGS).read(KEY_SCAN_SETTINGS);
    }

    public static ScanSettings getExistingOrNew() {
        ScanSettings s = get();
        if(s == null) return new ScanSettings();
        return s;
    }

    public void save() {
        Paper.book(TABLE_SETTINGS).write(KEY_SCAN_SETTINGS, this);
    }

    public Boolean getAutoPair() {
        return autoPair;
    }

    public void setAutoPair(Boolean autoPair) {
        this.autoPair = autoPair;
    }

    public Boolean getContinuousScanning() {
        return continuousScanning;
    }

    public void setContinuousScanning(Boolean continuousScanning) {
        this.continuousScanning = continuousScanning;
    }

    public Boolean getDiscoverServices() {
        return discoverServices;
    }

    public void setDiscoverServices(Boolean discoverServices) {
        this.discoverServices = discoverServices;
    }

    public Boolean getGattAutoConnect() {
        return gattAutoConnect;
    }

    public void setGattAutoConnect(Boolean gattAutoConnect) {
        this.gattAutoConnect = gattAutoConnect;
    }

    public Integer getGattScanTimeout() {
        return gattScanTimeout;
    }

    public void setGattScanTimeout(Integer gattScanTimeout) {
        this.gattScanTimeout = gattScanTimeout;
    }

    public String getPrioritize() {
        return prioritize;
    }

    public void setPrioritize(String prioritize) {
        this.prioritize = prioritize;
    }

    public TimeUnit getGattScanTimeoutUnit() {
        return gattScanTimeoutUnit;
    }

    public void setGattScanTimeoutUnit(TimeUnit gattScanTimeoutUnit) {
        this.gattScanTimeoutUnit = gattScanTimeoutUnit;
    }
}
