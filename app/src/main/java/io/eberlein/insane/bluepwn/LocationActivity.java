package io.eberlein.insane.bluepwn;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;

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

    @OnClick(R.id.geocodeBtn)
    public void geoCodeBtnClicked(){
        Geocoder gc = new Geocoder(this, Locale.getDefault());
        try{
            List<Address> addrs = gc.getFromLocation(Double.valueOf(latitude.getText().toString()), Double.valueOf(longitude.getText().toString()), 1);
            country.setText(addrs.get(0).getCountryName());
            city.setText(addrs.get(0).getLocality());
            note.setText(addrs.get(0).getAddressLine(0));

        } catch(IOException e){
            e.printStackTrace();
        }

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
        if(location.city.isEmpty() && location.country.isEmpty()){
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
