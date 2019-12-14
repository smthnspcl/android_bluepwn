package io.eberlein.insane.bluepwn.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import io.eberlein.insane.bluepwn.R;

public class DatabaseSettingsActivity extends AppCompatActivity {

    // todo pass getStringExtra(dbms)
    // dbms can be redis/mongodb
    // switch case getStringExtra
    // on finish() call save and write into database settings

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_database_settings);
        ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // save
    }
}
