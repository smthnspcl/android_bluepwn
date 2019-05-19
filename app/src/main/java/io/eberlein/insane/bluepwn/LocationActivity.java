package io.eberlein.insane.bluepwn;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;

import org.osmdroid.bonuspack.location.GeocoderNominatim;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LocationActivity extends AppCompatActivity {
    @BindView(R.id.latitudeTextView) TextView latitude;
    @BindView(R.id.longitudeTextView) TextView longitude;
    @BindView(R.id.altitudeTextView) TextView altitude;
    @BindView(R.id.countryEditText) EditText country;
    @BindView(R.id.cityEditText) EditText city;
    @BindView(R.id.streetEditText) EditText street;
    @BindView(R.id.notesEditText) EditText note;

    class ReverseGeoCodeData {
        Double longitude;
        Double latitude;
        String country;
        String city;
        String note;
    }

    class ReverseGeoCode extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... data){
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    GeocoderNominatim gc = new GeocoderNominatim("Mozilla/5.0 (Linux; Android 8.1) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/58.0.3029.83 Mobile Safari/537.36");
                    try{
                        Log.log(this.getClass(), "geocoding: " + latitude.getText().toString() + " " + longitude.getText().toString());

                        List<Address> addrs = gc.getFromLocation(Double.valueOf(latitude.getText().toString()), Double.valueOf(longitude.getText().toString()), 1);
                        if(addrs.size() > 0){
                            country.setText(addrs.get(0).getCountryName());
                            city.setText(addrs.get(0).getLocality());
                            StringBuilder n = new StringBuilder();
                            for(int i = 0; i < addrs.get(0).getMaxAddressLineIndex(); i++){
                                n.append(addrs.get(0).getAddressLine(i));
                                n.append("\n");
                            }
                            note.setText(n.toString());
                        } else {
                            Log.log(this.getClass(), "geocoder gave no results");
                        }
                    } catch(IOException e){
                        e.printStackTrace();
                    }
                }
            });
            t.start();
            return null;
        }
    }

    @OnClick(R.id.geocodeBtn)
    public void geoCodeBtnClicked(){
        ReverseGeoCode rgc = new ReverseGeoCode();
        rgc.doInBackground();
    }

    @OnClick(R.id.saveBtn)
    public void saveBtnClicked(){
        location.country = country.getText().toString();
        location.city = city.getText().toString();
        location.street = street.getText().toString();
        location.note = note.getText().toString();
        location.save();
        finish();
    }

    private Location location;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.onCreate(this.getClass());
        setContentView(R.layout.activity_location);
        ButterKnife.bind(this);
        String id = getIntent().getStringExtra("uuid");
        if(id != null) setContentValues(id);
        else location = new Location();
    }

    private void setContentValues(String uuid){
        location = Location.get(uuid);
        if(location.city == null && location.country == null){
            setTitle(location.latitude+ ", " + location.longitude);
        } else {
            setTitle(location.city + ", " + location.country);
        }

        latitude.setText(String.valueOf(location.latitude));
        longitude.setText(String.valueOf(location.longitude));
        altitude.setText(String.valueOf(location.altitude));
        country.setText(location.country);
        city.setText(location.city);
        street.setText(location.street);
        note.setText(location.note);
    }
}
