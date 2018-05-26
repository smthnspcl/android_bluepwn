package io.eberlein.insane.bluepwn;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.List;
import java.util.concurrent.Callable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

// todo caching

public class MapFragment extends Fragment {

    private LocationManager locationManager;
    private GPSLocationListener locationListener;

    private Marker currentPositionMarker;

    @BindView(R.id.mapView) MapView mapView;

    @OnClick(R.id.centerOnCurrentPositionButton)
    public void onCenterOnCurrentPositionButtonClicked(){
        mapView.getController().setCenter(currentPositionMarker.getPosition());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissions();
        initGPS();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, v);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapView.setBuiltInZoomControls(true);
        currentPositionMarker = new Marker(mapView);
        mapView.getOverlays().add(currentPositionMarker);
        IMapController c = mapView.getController();
        locationListener.onLocationChangedFunctions.add(new Callable<Void>() {
            @Override
            public Void call() {
                currentPositionMarker.setPosition(new GeoPoint(locationListener.currentLocation.latitude, locationListener.currentLocation.longitude));
                currentPositionMarker.setIcon(getResources().getDrawable(R.drawable.baseline_my_location_white_48));
                return null;
            }
        });
        c.setZoom(17);
        return v;
    }

    private void initGPS(){
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new GPSLocationListener();
        try{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void requestPermissions(){
        if(!(ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED))
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Static.LOCATION_RESULT);
    }
}
