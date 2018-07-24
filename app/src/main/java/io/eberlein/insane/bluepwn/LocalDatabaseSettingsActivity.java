package io.eberlein.insane.bluepwn;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.paperdb.Paper;

public class LocalDatabaseSettingsActivity extends AppCompatActivity {
    @BindView(R.id.filterSpinner) Spinner filterSpinner;
    @BindView(R.id.filteredItems) TextView filteredItems;

    @OnClick(R.id.filter)
    public void filterClicked(){
        String f = filterSpinner.getSelectedItem().toString();
        for(String k : Paper.book(f).getAllKeys()){

        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_database_settings);
        ButterKnife.bind(this);
        Log.onCreate(this.getClass());
        filterSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, Static.TABLES));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.onDestroy(this.getClass());
    }
}
