package io.eberlein.insane.bluepwn;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.Callable;

import io.paperdb.Paper;

public class RemoteDatabase {
    private static final String DEVICE_TABLE = "device";
    private static final String STAGE_TABLE = "stage";
    private static final String STAGER_TABLE = "stager";
    private static final String LOCATION_TABLE = "location";
    private static final String UUID_TABLE = "uuid";
    private static final String SCAN_TABLE = "scan";
    private static final String OUI_TABLE = "oui";
    // private static final String BLUEBORNE_COLLECTION = "blueborne";

    private static final String[] TABLES = {
            DEVICE_TABLE, STAGE_TABLE, OUI_TABLE, STAGER_TABLE, LOCATION_TABLE, SCAN_TABLE, UUID_TABLE
    };

    private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:MM:SS", Locale.getDefault());

    private RemoteDBSettings settings;
    private String cookie;
    private Context context;
    private Gson gson;

    RemoteDatabase(Context context, RemoteDBSettings settings){
        this.context = context;
        this.settings = settings;
        gson = new Gson();
    }

    void getCookie(){
        JsonObject j = new JsonObject();
        j.addProperty("username", settings.getUsername());
        j.addProperty("password", settings.getPassword());

        Ion.with(context)
                .load(settings.getServer() + "/api/authenticate")
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
                j.addProperty("username", settings.getUsername());
                j.addProperty("cookie", cookie);
                j.addProperty("keys", gson.toJson(Paper.book(table).getAllKeys()));
                Ion.with(context).load(settings.getServer() + "/api/{{TBL}}/difference".replace("{{TBL}}", table))
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
