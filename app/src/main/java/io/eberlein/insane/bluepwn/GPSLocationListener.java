package io.eberlein.insane.bluepwn;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.eberlein.insane.bluepwn.object.ILocation;
import io.paperdb.Paper;


public class GPSLocationListener implements LocationListener {
    private ILocation currentILocation;
    private List<Callable<Void>> onLocationChangedFunctions;

    public GPSLocationListener() {
        onLocationChangedFunctions = new ArrayList<>();
    }

    @Override
    public void onLocationChanged(Location location) {
        currentILocation = new ILocation(location);
        Paper.book("location").write(currentILocation.getUuid(), currentILocation);
        for(Callable<Void> c : onLocationChangedFunctions) {
            try {
                c.call();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    public ILocation getCurrentILocation() {
        return currentILocation;
    }

    public void addOnLocationChangedFunction(Callable<Void> f) {
        onLocationChangedFunctions.add(f);
    }
}
