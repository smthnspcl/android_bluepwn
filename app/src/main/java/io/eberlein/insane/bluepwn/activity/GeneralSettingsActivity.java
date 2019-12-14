package io.eberlein.insane.bluepwn.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import io.eberlein.insane.bluepwn.R;

public class GeneralSettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_settings);
        setTitle("general");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
