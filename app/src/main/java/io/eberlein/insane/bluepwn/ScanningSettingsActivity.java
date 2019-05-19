package io.eberlein.insane.bluepwn;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScanningSettingsActivity extends AppCompatActivity {

    @BindView(R.id.continuousScanningDefault) CheckBox continuousScanning;
    @BindView(R.id.discoverServices) CheckBox discoverServices;
    @BindView(R.id.autoPair) CheckBox autoPair;

    ScanSettings settings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.onCreate(this.getClass());
        setContentView(R.layout.activity_scan_settings);
        setTitle("scan settings");
        ButterKnife.bind(this);
        settings = ScanSettings.getExistingOrNew();
        continuousScanning.setChecked(settings.continuousScanning);
        discoverServices.setChecked(settings.discoverServices);
        autoPair.setChecked(settings.autoPair);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        settings.autoPair = autoPair.isChecked();
        settings.continuousScanning = continuousScanning.isChecked();
        settings.discoverServices = discoverServices.isChecked();
        Log.onDestroy(this.getClass());
        settings.save();
        Toast.makeText(this, "saved", Toast.LENGTH_SHORT).show();
    }
}
