package io.eberlein.insane.bluepwn;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.paperdb.Paper;


// todo
public class GPSLocationListener implements LocationListener {
    io.eberlein.insane.bluepwn.Location currentLocation;
    List<Callable<Void>> onLocationChangedFunctions;

    GPSLocationListener(){
        onLocationChangedFunctions = new ArrayList<>();
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = new io.eberlein.insane.bluepwn.Location(location);
        Paper.book("location").write(currentLocation.uuid, currentLocation);
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
}
