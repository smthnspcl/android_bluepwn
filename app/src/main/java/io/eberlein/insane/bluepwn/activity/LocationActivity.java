package io.eberlein.insane.bluepwn.activity;

import android.location.Address;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import org.osmdroid.bonuspack.location.GeocoderNominatim;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.eberlein.insane.bluepwn.R;
import io.eberlein.insane.bluepwn.object.ILocation;

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

    private ILocation ILocation;

    @OnClick(R.id.geocodeBtn)
    public void geoCodeBtnClicked() {
        ReverseGeoCode rgc = new ReverseGeoCode();
        rgc.doInBackground();
    }

    @OnClick(R.id.saveBtn)
    public void saveBtnClicked() {
        ILocation.setCountry(country.getText().toString());
        ILocation.setCity(city.getText().toString());
        ILocation.setStreet(street.getText().toString());
        ILocation.setNote(note.getText().toString());
        ILocation.save();
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        ButterKnife.bind(this);
        String id = getIntent().getStringExtra("uuid");
        if (id != null) setContentValues(id);
        else ILocation = new ILocation();
    }

    private void setContentValues(String uuid) {
        ILocation = io.eberlein.insane.bluepwn.object.ILocation.get(uuid);
        if (ILocation.getCity() == null && ILocation.getCountry() == null) {
            setTitle(ILocation.getLatitude() + ", " + ILocation.getLongitude());
        } else {
            setTitle(ILocation.getCity() + ", " + ILocation.getCountry());
        }

        latitude.setText(String.valueOf(ILocation.getLatitude()));
        longitude.setText(String.valueOf(ILocation.getLongitude()));
        altitude.setText(String.valueOf(ILocation.getAltitude()));
        country.setText(ILocation.getCountry());
        city.setText(ILocation.getCity());
        street.setText(ILocation.getStreet());
        note.setText(ILocation.getNote());
    }

    class ReverseGeoCode extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... data){
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    GeocoderNominatim gc = new GeocoderNominatim("Mozilla/5.0 (Linux; Android 8.1) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/58.0.3029.83 Mobile Safari/537.36");
                    try{
                        Log.d(this.getClass().toString(), "geocoding: " + latitude.getText().toString() + " " + longitude.getText().toString());

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
                            Log.d(this.getClass().toString(), "geocoder gave no results");
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
}
