package io.eberlein.insane.bluepwn.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.eberlein.insane.bluepwn.R;
import io.eberlein.insane.bluepwn.object.ScanSettings;

public class ScanningSettingsActivity extends AppCompatActivity {

    @BindView(R.id.continuousScanningDefault) CheckBox continuousScanning;
    @BindView(R.id.discoverServices) CheckBox discoverServices;
    @BindView(R.id.autoPair) CheckBox autoPair;

    ScanSettings settings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_settings);
        setTitle("scan settings");
        ButterKnife.bind(this);
        settings = ScanSettings.getExistingOrNew();
        continuousScanning.setChecked(settings.getContinuousScanning());
        discoverServices.setChecked(settings.getDiscoverServices());
        autoPair.setChecked(settings.getAutoPair());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        settings.setAutoPair(autoPair.isChecked());
        settings.setContinuousScanning(continuousScanning.isChecked());
        settings.setDiscoverServices(discoverServices.isChecked());
        settings.save();
        Toast.makeText(this, "saved", Toast.LENGTH_SHORT).show();
    }
}
