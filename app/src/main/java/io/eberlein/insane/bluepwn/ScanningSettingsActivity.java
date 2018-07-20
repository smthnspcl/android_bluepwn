package io.eberlein.insane.bluepwn;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScanningSettingsActivity extends AppCompatActivity {

    @BindView(R.id.continuousScanningDefault) CheckBox continuousScanningDefault;
    @BindView(R.id.discoverServices) CheckBox discoverServices;

    ScanSettings settings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.onCreate(this.getClass());
        setContentView(R.layout.activity_scan_settings);
        setTitle("scan settings");
        ButterKnife.bind(this);
        settings = ScanSettings.getExistingOrNew();
        continuousScanningDefault.setChecked(settings.continuousScanningDefault);
        discoverServices.setChecked(settings.discoverServices);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.onDestroy(this.getClass());
        settings.save();
        Toast.makeText(this, "saved", Toast.LENGTH_SHORT).show();
    }
}
