package io.eberlein.insane.bluepwn;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;

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
        // todo (pelias) ?
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
        setContentView(R.layout.activity_location);
        ButterKnife.bind(this);
        String id = getIntent().getStringExtra("uuid");
        if(id != null) setContentValues(id);
        else location = new Location();
    }

    private void setContentValues(String uuid){
        location = Location.get(uuid);
        latitude.setText(String.valueOf(location.latitude));
        longitude.setText(String.valueOf(location.longitude));
        altitude.setText(String.valueOf(location.altitude));
        country.setText(location.country);
        city.setText(location.city);
        street.setText(location.street);
        note.setText(location.note);
    }
}
