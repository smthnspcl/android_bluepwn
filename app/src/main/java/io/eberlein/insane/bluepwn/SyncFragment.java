package io.eberlein.insane.bluepwn;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.Callable;

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

public class SyncFragment extends Fragment {
    @BindView(R.id.sync) Button sync;
    @BindView(R.id.ouiSyncStatusLabel) TextView ouiSyncStatusLabel;
    @BindView(R.id.devicesSyncStatusLabel) TextView devicesSyncStatusLabel;
    @BindView(R.id.uuidsSyncStatusLabel) TextView uuidsSyncStatusLabel;
    @BindView(R.id.locationsSyncStatusLabel) TextView locationsSyncStatusLabel;
    @BindView(R.id.scansSyncStatusLabel) TextView scansSyncStatusLabel;
    @BindView(R.id.stagersSyncStatusLabel) TextView stagersSyncStatusLabel;
    @BindView(R.id.stagesSyncStatusLabel) TextView stagesSyncStatusLabel;

    private static final String[] TABLES = {
            TABLE_DEVICE, TABLE_STAGE, TABLE_OUI, TABLE_STAGER, TABLE_LOCATION, TABLE_SCAN, TABLE_SERVICE, TABLE_CHARACTERISTIC, TABLE_DESCRIPTOR
    };

    private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:MM:SS", Locale.getDefault());

    private String cookie;
    private Context context;
    private RemoteDBSettings settings;
    private Gson gson;

    // todo
    private boolean syncing; // check if syncing, cancel
    // eventbus callbacks

    @OnClick(R.id.sync)
    public void syncBtnClicked(){
        sync();
    }

    @Subscribe
    public void onGotCookie(EventGotCookie e){
        ouiSyncStatusLabel.setText("ready");
    }

    public SyncFragment(){
        gson = new Gson();
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

        Ion.with(context)
                .load(settings.server + "/api/authenticate")
                .setJsonObjectBody(j)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if(result != null && !result.isJsonNull()) {
                            if (result.has("cookie")) {
                                Toast.makeText(context, "got cookie", Toast.LENGTH_SHORT).show();
                                cookie = result.get("cookie").getAsString();
                            }
                        } else {
                            Toast.makeText(context, "cookie is null", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void getKeys(JsonArray a){
        System.out.println("got {{I}} keys".replace("{{I}}", String.valueOf(a.size())));
        for(JsonElement e : a){
            System.out.println(e.getAsString());
        }
    }

    boolean sync(){
        if(cookie != null && !cookie.isEmpty()){
            Toast.makeText(context, "cooking not null; syncing", Toast.LENGTH_SHORT).show();
            for(String table : TABLES) {
                JsonObject j = new JsonObject();
                j.addProperty("username", settings.username);
                j.addProperty("cookie", cookie);
                j.addProperty("keys", gson.toJson(Paper.book(table).getAllKeys()));
                Ion.with(context).load(settings.server + "/api/{{TBL}}/difference".replace("{{TBL}}", table))
                        .setJsonObjectBody(j)
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                if(result != null && !result.isJsonNull()){
                                    if(result.has("keys")) getKeys(result.getAsJsonArray("keys"));
                                }
                            }
                        });
            }
            return true;
        } else {
            Toast.makeText(context, "cookie null; trying to get new cookie", Toast.LENGTH_SHORT).show();
            getCookie();
        }
        return false;
    }
}
