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
import static io.eberlein.insane.bluepwn.Static.tableToClass;

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

    @Subscribe
    public void onGotCookie(EventGotCookie e){
        if(e.cookie != null){
            syncStatusLabel.setText("ready");
            sync.setText("sync");
            cookie = e.cookie;
        } else {
            syncStatusLabel.setText("cookie null");
            sync.setText("get cookie");
        }
    }

    @Subscribe
    public void onGotDifference(EventGotDifference e){
        TextView tv = tableToTextView(e.table);
        tv.setText(String.valueOf(e.differences));
        patchResults(e.table, e.differences);
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
        classToTable(settings.getClass());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sync, container, false);
        ButterKnife.bind(this, v);
        return v;
    }


    void patchResults(String table, JsonArray differences) {
        tableToTextView(table).setText("patching");
        for(JsonElement e : differences){

        }
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
                        EventBus.getDefault().post(new EventGotCookie(c));
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
                        if(success) {System.out.println(result.toString()); EventBus.getDefault().post(new EventGotDifference(table, result.getAsJsonArray("differences")));}
                        else EventBus.getDefault().post(new EventSyncFailed(table, "no keys"));
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
