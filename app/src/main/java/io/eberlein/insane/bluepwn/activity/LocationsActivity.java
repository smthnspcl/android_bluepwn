package io.eberlein.insane.bluepwn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.eberlein.insane.bluepwn.R;
import io.eberlein.insane.bluepwn.adapter.LocationAdapter;
import io.eberlein.insane.bluepwn.object.ILocation;
import io.eberlein.insane.bluepwn.object.Scan;

public class LocationsActivity extends AppCompatActivity {

    @BindView(R.id.recycler) RecyclerView recycler;
    @BindView(R.id.spinner) Spinner spinner;
    @BindView(R.id.query) EditText query;
    @BindView(R.id.fabLayout)
    RapidFloatingActionLayout rfaLayout;
    @BindView(R.id.fab)
    RapidFloatingActionButton fab;

    private static final String[] searchSpinnerItems = {
            "longitude", "latitude", "timestamp", "zip", "city", "address"
    };

    private LocationAdapter locations;
    private Gson gson;

    @OnClick(R.id.fab)
    public void addRecyclerItemBtnClicked(){
        Intent i = new Intent(this, LocationActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.objectlist_search);
        ButterKnife.bind(this);
        setTitle("locations");
        gson = new Gson();
        locations = new LocationAdapter();
        String e = getIntent().getStringExtra("uuid");
        if(e != null) populateWithSuppliedLocations(e);
        else locations.addAll(ILocation.get());
        recycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recycler.setAdapter(locations);
        locations.setOnItemClickListener(new LocationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int p) {
                Intent i = new Intent(getApplicationContext(), LocationActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("uuid", locations.get(p).getUuid());
                startActivity(i);
            }
        });
        spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, searchSpinnerItems));
    }

    private void populateWithSuppliedLocations(String data){
        Scan s = Scan.get(data);
        locations.addAll(s.getLocations());
    }
}
