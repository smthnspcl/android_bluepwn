package io.eberlein.insane.bluepwn;

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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.paperdb.Paper;

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
import static io.eberlein.insane.bluepwn.Static.URL_TABLE_VARIABLE;

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

    private static final String[] TABLES = {
            TABLE_DEVICE, TABLE_STAGE, TABLE_OUI, TABLE_STAGER, TABLE_LOCATION, TABLE_SCAN, TABLE_SERVICE, TABLE_CHARACTERISTIC, TABLE_DESCRIPTOR
    };

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

    @Subscribe
    public void onGotCookie(EventGotCookie e){
        if(e != null){
            ouiSyncStatusLabel.setText("ready");
            sync.setText("sync");
            cookie = e.cookie;
        } else {
            syncStatusLabel.setText("cookie null");
            sync.setText("get cookie");
        }
    }

    public SyncFragment(){
        gson = new Gson();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        settings = RemoteDBSettings.get();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sync, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    void getCookie(){
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
                        EventBus.getDefault().post(new EventGotCookie(c));
                    }
                });
    }

    private List<String> getKeys(JsonArray a){
        List<String> keys = new ArrayList<>();
        System.out.println("got {{I}} keys".replace("{{I}}", String.valueOf(a.size())));
        for(JsonElement e : a) keys.add(e.getAsString());
        return keys;
    }

    private void getObjects(String table, List<String> keys){
        JsonObject r = new JsonObject();
        r.addProperty("username", settings.username);
        r.addProperty("cookie", cookie);
        r.addProperty("keys", gson.toJson(keys));
        Ion.with(getContext()).load(settings.server + URL_TABLE_GET.replace(URL_TABLE_VARIABLE, table))
                .setJsonObjectBody(r)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if(!result.isJsonNull()){
                            // todo Eventbus call / set label
                        }
                    }
                });
    }

    void sync(){
        if(cookie != null && !cookie.isEmpty()){
            syncStatusLabel.setText("syncing");
            List<String> keys = new ArrayList<>();
            for(String table : TABLES) {
                TextView tv = tableToTextView(table);
                JsonObject j = new JsonObject();
                j.addProperty("username", settings.username);
                j.addProperty("cookie", cookie);
                j.addProperty("keys", gson.toJson(Paper.book(table).getAllKeys()));
                Ion.with(getContext()).load(settings.server + URL_TABLE_DIFFERENCE.replace(URL_TABLE_VARIABLE, table))
                        .setJsonObjectBody(j)
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                if(!result.isJsonNull()){
                                    if(result.has("keys")){
                                        List<String> keys = getKeys(result.getAsJsonArray("keys"));
                                        tv.setText(String.valueOf(keys.size()));
                                        getObjects(table, keys);
                                    } else {
                                        tv.setText("failed");
                                    }

                                }
                            }
                        });
            }

        }
    }
}
