package io.eberlein.insane.bluepwn;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import org.osmdroid.config.Configuration;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    private Intent scannerServiceIntent;

    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.nav_view) NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.onCreate(this.getClass());
        scannerServiceIntent = new Intent(this, ScannerService.class);
        startService(scannerServiceIntent);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Context c = getApplicationContext();
        Configuration.getInstance().load(c, PreferenceManager.getDefaultSharedPreferences(c));
        Paper.init(this);
        navigationView.setNavigationItemSelectedListener(onNavigationItemSelectedListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new BluetoothFragment()).addToBackStack(null).commit();
        getSupportFragmentManager().addOnBackStackChangedListener(onBackStackChangedListener);
    }

    private NavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment f;
            switch (item.getItemId()){
                case R.id.MenuBluetooth:
                    f = new BluetoothFragment(); break;
                case R.id.MenuScans:
                    f = new ScansFragment(); break;
                case R.id.MenuDevices:
                    f = new DevicesFragment(); break;
                case R.id.MenuStages:
                    f = new StagesFragment(); break;
                case R.id.MenuStagers:
                    f = new StagersFragment(); break;
                case R.id.MenuUuids:
                    f = new ServicesFragment(); break;
                case R.id.MenuLocations:
                    f = new LocationsFragment(); break;
                case R.id.MenuOui:
                    f = new OuiFragment(); break;
                case R.id.MenuMap:
                    f = new MapFragment(); break;
                case R.id.MenuSync:
                    f = new SyncFragment(); break;
                case R.id.MenuSettings:
                    f = new SettingsFragment(); break;
                case R.id.MenuExit:
                    finish();
                default:
                    f = new BluetoothFragment();
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, f).addToBackStack(null).commit();
            drawerLayout.closeDrawers();
            return true;
        }
    };

    private FragmentManager.OnBackStackChangedListener onBackStackChangedListener = new FragmentManager.OnBackStackChangedListener() {
        @Override
        public void onBackStackChanged() {
            FragmentManager fm = getSupportFragmentManager();
            try{
                Fragment f = fm.findFragmentById(fm.getBackStackEntryAt(fm.getBackStackEntryCount() - 1).getId());
                if(f != null) f.onResume();
            } catch (ArrayIndexOutOfBoundsException e){
                e.printStackTrace();
                finish(); // user wants to leave the app using back
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // todo for pub release
    }

    @Override
    protected void onDestroy() {
        Log.onDestroy(this.getClass());
        stopService(scannerServiceIntent);
        super.onDestroy();
    }
}
