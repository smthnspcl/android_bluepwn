package io.eberlein.insane.bluepwn;

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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.paperdb.Paper;

public class LocationsActivity extends AppCompatActivity {

    @BindView(R.id.locationsRecycler) RecyclerView locationsRecycler;
    @BindView(R.id.searchSpinner) Spinner searchSpinner;
    @BindView(R.id.searchEditText) EditText searchEditText;

    private static final String[] searchSpinnerItems = {
            "longitude", "latitude", "timestamp", "zip", "city", "address"
    };

    private LocationAdapter locations;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activtiy_location);
        ButterKnife.bind(this);
        locations = new LocationAdapter();
        String e = getIntent().getStringExtra("ids");
        if(e != null) populateWithSuppliedLocations(e);
        else locations.addAll(LocalDatabase.getAllLocations());
        locationsRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        locationsRecycler.setAdapter(locations);
        locations.setOnItemClickListener(new LocationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int p) {
                Intent i = new Intent(getApplicationContext(), LocationsActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("id", locations.get(p).id);
                startActivity(i);
            }
        });
        searchSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, searchSpinnerItems));
    }

    private void populateWithSuppliedLocations(String data){
        for(Location l : JSON.parseObject(data, new TypeReference<List<Location>>(){})){ locations.add(l); }
    }
}
