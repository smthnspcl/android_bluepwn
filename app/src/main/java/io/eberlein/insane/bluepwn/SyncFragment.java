package io.eberlein.insane.bluepwn;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.paperdb.Paper;

import static io.eberlein.insane.bluepwn.Static.ACTION_DATABASE_EXPORT_RESULTS;
import static io.eberlein.insane.bluepwn.Static.ACTION_DATABASE_EXPORT_RESULTS_FAILED;
import static io.eberlein.insane.bluepwn.Static.ACTION_DATABASE_GET_DIFFERENCE_FAILED;
import static io.eberlein.insane.bluepwn.Static.ACTION_DATABASE_GOT_COOKIE;
import static io.eberlein.insane.bluepwn.Static.ACTION_DATABASE_GOT_DIFFERENCE;
import static io.eberlein.insane.bluepwn.Static.ACTION_DATABASE_IMPORT_RESULTS;
import static io.eberlein.insane.bluepwn.Static.ACTION_DATABASE_IMPORT_RESULTS_FAILED;
import static io.eberlein.insane.bluepwn.Static.ACTION_DATA_KEY;
import static io.eberlein.insane.bluepwn.Static.TABLES;
import static io.eberlein.insane.bluepwn.Static.TABLE_CHARACTERISTIC;
import static io.eberlein.insane.bluepwn.Static.TABLE_DESCRIPTOR;
import static io.eberlein.insane.bluepwn.Static.TABLE_DEVICE;
import static io.eberlein.insane.bluepwn.Static.TABLE_LOCATION;
import static io.eberlein.insane.bluepwn.Static.TABLE_OUI;
import static io.eberlein.insane.bluepwn.Static.TABLE_SCAN;
import static io.eberlein.insane.bluepwn.Static.TABLE_SERVICE;
import static io.eberlein.insane.bluepwn.Static.TABLE_STAGE;
import static io.eberlein.insane.bluepwn.Static.TABLE_STAGER;
import static io.eberlein.insane.bluepwn.Static.URL_AUTHENTICATE;
import static io.eberlein.insane.bluepwn.Static.URL_TABLE_DIFFERENCE;
import static io.eberlein.insane.bluepwn.Static.URL_TABLE_GET;
import static io.eberlein.insane.bluepwn.Static.URL_TABLE_SET;
import static io.eberlein.insane.bluepwn.Static.URL_TABLE_VARIABLE;
import static io.eberlein.insane.bluepwn.Static.classToTable;
import static io.eberlein.insane.bluepwn.Static.send2BcR;

public class SyncFragment extends Fragment {
    @BindView(R.id.sync) Button sync;
    @BindView(R.id.ouiSyncStatusLabel) TextView ouiSyncStatusLabel;
    @BindView(R.id.devicesSyncStatusLabel) TextView devicesSyncStatusLabel;
    @BindView(R.id.servicesSyncStatusLabel) TextView servicesSyncStatusLabel;
    @BindView(R.id.locationsSyncStatusLabel) TextView locationsSyncStatusLabel;
    @BindView(R.id.scansSyncStatusLabel) TextView scansSyncStatusLabel;
    @BindView(R.id.stagersSyncStatusLabel) TextView stagersSyncStatusLabel;
    @BindView(R.id.stagesSyncStatusLabel) TextView stagesSyncStatusLabel;
    @BindView(R.id.syncStatusLabel) TextView syncStatusLabel;
    @BindView(R.id.descriptorsSyncStatusLabel) TextView descriptorsSyncStatusLabel;
    @BindView(R.id.characteristicsSyncStatusLabel) TextView characteristicsSyncStatusLabel;

    TextView tableToTextView(String table){
        switch (table){
            case TABLE_STAGE:
                return stagesSyncStatusLabel;
            case TABLE_STAGER:
                return stagersSyncStatusLabel;
            case TABLE_SERVICE:
                return servicesSyncStatusLabel;
            case TABLE_SCAN:
                return scansSyncStatusLabel;
            case TABLE_OUI:
                return ouiSyncStatusLabel;
            case TABLE_LOCATION:
                return locationsSyncStatusLabel;
            case TABLE_DEVICE:
                return devicesSyncStatusLabel;
            case TABLE_DESCRIPTOR:
                return descriptorsSyncStatusLabel;
            case TABLE_CHARACTERISTIC:
                return characteristicsSyncStatusLabel;
        }
        return null;
    }

    private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:MM:SS", Locale.getDefault());

    private String cookie;
    private RemoteDBSettings settings;
    private Gson gson;

    // todo
    private boolean syncing; // check if syncing, cancel

    @OnClick(R.id.sync)
    public void syncBtnClicked(){
        if(cookie == null) getCookie();
        sync();
    }

    BroadcastReceiver gotCookieReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            cookie = intent.getStringExtra(ACTION_DATA_KEY);
            if(cookie != null){
                syncStatusLabel.setText("ready");
                sync.setText("sync");
            } else {
                syncStatusLabel.setText("cookie null");
                sync.setText("get cookie");
            }
        }
    };

    BroadcastReceiver gotDifferenceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            JsonObject results = gson.fromJson(intent.getStringExtra(ACTION_DATA_KEY), JsonObject.class);
            String table = intent.getStringExtra("table");
            syncStatusLabel.setText("got difference");
            JsonArray h = results.getAsJsonArray(Static.DB_HAVE);
            JsonArray w = results.getAsJsonArray(Static.DB_WANT);
            TextView tv = tableToTextView(table);
            tv.setText(String.valueOf(h.size() + w.size()));
            syncStatusLabel.setText("importing");
            if(h.size() > 0) importResults(table, h);
            syncStatusLabel.setText("exporting");
            if(w.size() > 0) exportResults(table, w);
            syncStatusLabel.setText("done");
        }
    };

    BroadcastReceiver gotExportResultReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            tableToTextView(intent.getStringExtra("table")).setText(intent.getStringExtra("msg"));
        }
    };

    BroadcastReceiver gotExportFailedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            tableToTextView(intent.getStringExtra("table")).setText(intent.getStringExtra("msg"));
        }
    };

    BroadcastReceiver gotImportResultsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            JsonArray results = gson.fromJson(intent.getStringExtra(ACTION_DATA_KEY), JsonArray.class);
            int c = 0;
            for(JsonElement i : results){
                c += 1;
                tableToTextView(intent.getStringExtra("table")).setText(String.valueOf(c) + " / " + String.valueOf(results.size()));
            }
        }
    };

    BroadcastReceiver gotImportResultsFailedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            tableToTextView(intent.getStringExtra("table")).setText(intent.getStringExtra("msg"));
        }
    };

    private BroadcastReceiver[] broadcastReceivers = {
            gotCookieReceiver, gotDifferenceReceiver, gotExportFailedReceiver, gotExportResultReceiver, gotImportResultsFailedReceiver, gotImportResultsReceiver
    };

    private void registerReceivers(){
        Context c = getContext();
        c.registerReceiver(gotCookieReceiver, new IntentFilter(ACTION_DATABASE_GOT_COOKIE));
        c.registerReceiver(gotDifferenceReceiver, new IntentFilter(ACTION_DATABASE_GOT_DIFFERENCE));
        c.registerReceiver(gotExportFailedReceiver, new IntentFilter(ACTION_DATABASE_EXPORT_RESULTS_FAILED));
        c.registerReceiver(gotExportResultReceiver, new IntentFilter(ACTION_DATABASE_EXPORT_RESULTS));
        c.registerReceiver(gotImportResultsFailedReceiver, new IntentFilter(ACTION_DATABASE_IMPORT_RESULTS_FAILED));
        c.registerReceiver(gotImportResultsReceiver, new IntentFilter(ACTION_DATABASE_IMPORT_RESULTS));
    }

    private void unregisterReceivers(){
        Context c = getContext();
        for(BroadcastReceiver bcr : broadcastReceivers) c.unregisterReceiver(bcr);
    }

    public SyncFragment(){
        gson = new Gson();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.onCreate(this.getClass());
        registerReceivers();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.onDestroy(this.getClass());
        unregisterReceivers();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.onResume(this.getClass());
        settings = RemoteDBSettings.get();
        classToTable(settings.getClass());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sync, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    void importResults(String table, JsonArray results) {
        tableToTextView(table).setText("importing: " + String.valueOf(results.size()));
        JsonObject j = generateCookieRequestBody();
        j.add("uuids", results);
        Ion.with(getContext())
                .load(settings.server + URL_TABLE_GET.replace(URL_TABLE_VARIABLE, table))
                .setJsonObjectBody(j)
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        Boolean success = false;
                        if(result != null) if(!result.isJsonNull()) success = true;
                        System.out.println(result);
                        if(success) {
                            Intent i = new Intent(ACTION_DATABASE_IMPORT_RESULTS);
                            i.putExtra(ACTION_DATA_KEY, result.toString());
                            i.putExtra("table", table);
                            getContext().sendBroadcast(i);
                        } else {
                            Intent i = new Intent(ACTION_DATABASE_IMPORT_RESULTS_FAILED);
                            i.putExtra("table", table);
                            i.putExtra("msg", "no data");
                            getContext().sendBroadcast(i);
                        }
                    }
                });
    }

    void exportResults(String table, JsonArray results) {
        tableToTextView(table).setText("exporting: " + String.valueOf(results.size()));
        JsonObject j = generateCookieRequestBody();
        List<JsonObject> r = new ArrayList<>();
        for(JsonElement e : results) r.add(Paper.book(table).read(e.getAsString()));
        j.addProperty("objs", gson.toJson(r));
        j.addProperty("table", table);
        Ion.with(getContext())
                .load(settings.server + URL_TABLE_SET.replace(URL_TABLE_VARIABLE, table))
                .setJsonObjectBody(j)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        Boolean success = false;
                        if(result != null) if(!result.isJsonNull()) success = true;
                        System.out.println(result);
                        if(success) {
                            Intent i = new Intent(ACTION_DATABASE_EXPORT_RESULTS);
                            i.putExtra(table, "successful");
                            i.putExtra(table, "failed");
                            getContext().sendBroadcast(i);
                        }
                        else {
                            Intent i = new Intent(ACTION_DATABASE_EXPORT_RESULTS_FAILED);
                            i.putExtra("table", table);
                            i.putExtra("msg", "failed");
                            getContext().sendBroadcast(i);
                        }
                    }
                });
    }

    void getCookie(){
        syncStatusLabel.setText("getting cookie");
        JsonObject j = new JsonObject();
        j.addProperty("username", settings.username);
        j.addProperty("password", settings.password);

        Ion.with(getContext())
                .load(settings.server + URL_AUTHENTICATE)
                .setJsonObjectBody(j)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        String c = null;
                        if(result != null && !result.isJsonNull()) {
                            if (result.has("cookie")) {
                                JsonElement _e = result.get("cookie");
                                if(_e != null && !_e.isJsonNull()){
                                    c = result.get("cookie").getAsString();
                                }
                            }
                        }
                        send2BcR(getContext(), ACTION_DATABASE_GOT_COOKIE, c);
                    }
                });
    }

    private JsonObject generateCookieRequestBody(){
        JsonObject r = new JsonObject();
        r.addProperty("username", settings.username);
        r.addProperty("cookie", cookie);
        return r;
    }

    private void difference(String table){
        syncStatusLabel.setText("getting difference");
        JsonObject r = generateCookieRequestBody();
        r.addProperty("keys", gson.toJson(Paper.book(table).getAllKeys()));
        Ion.with(getContext()).load(settings.server + URL_TABLE_DIFFERENCE.replace(URL_TABLE_VARIABLE, table))
                .setJsonObjectBody(r)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        Boolean success = false;
                        if(result != null) if(!result.isJsonNull()) success = true;
                        if(success) {
                            Intent i = new Intent(ACTION_DATABASE_GOT_DIFFERENCE);
                            i.putExtra("table", table);
                            i.putExtra(ACTION_DATA_KEY, result.toString());
                            getContext().sendBroadcast(i);
                        } else {
                            Intent i = new Intent(ACTION_DATABASE_GET_DIFFERENCE_FAILED);
                            i.putExtra("table", table);
                            i.putExtra(ACTION_DATA_KEY, "no keys");
                        }
                    }
                });
    }

    private void sync(){
        if(cookie != null && !cookie.isEmpty()){
            syncStatusLabel.setText("syncing");
            for(String table : TABLES) difference(table);
        } else {
            syncStatusLabel.setText("cookie null");
        }
    }
}
