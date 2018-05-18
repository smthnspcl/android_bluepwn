package io.eberlein.insane.bluepwn;

import android.content.Context;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class Database {
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Device> deviceMongoCollection;
    private MongoCollection<Action> actionMongoCollection;
    private MongoCollection<OuiEntry> ouiMongoCollection;

    private static final String DEVICES_COLLECTION = "devices";
    private static final String ACTIONS_COLLECTION = "actions";
    private static final String OUI_COLLECTION = "oui";
    // private static final String BLUEBORNE_COLLECTION = "blueborne";

    private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:MM:SS", Locale.getDefault());

    Database(Context context, String username, String password, String database, String host, Integer port){
        CodecRegistry codecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        mongoClient = MongoClients.create(MongoClientSettings.builder().applyToSslSettings(builder -> builder.enabled(true)).codecRegistry(codecRegistry).credential(MongoCredential.createCredential(username, database, password.toCharArray())).applyToClusterSettings(builder -> builder.hosts(Arrays.asList(new ServerAddress(host, port)))).build());
        this.database = mongoClient.getDatabase(database);
        this.deviceMongoCollection = this.database.getCollection(DEVICES_COLLECTION, Device.class);
        this.actionMongoCollection = this.database.getCollection(ACTIONS_COLLECTION, Action.class);
        this.ouiMongoCollection = this.database.getCollection(OUI_COLLECTION, OuiEntry.class);
    }

    // todo prevent nosql injection
    // clean up/ reduce redundant functions

    void insertDevice(Device device){
        deviceMongoCollection.insertOne(device);
    }

    void insertAction(Action action){
        actionMongoCollection.insertOne(action);
    }

    List<Device> getDevices(){
        List<Device> devices = new ArrayList<>();
        for(Device d : deviceMongoCollection.find()) devices.add(d);
        return devices;
    }

    // theoretically just gets more?
    Boolean deviceCountEquals(Integer count){
        return deviceMongoCollection.count() >= count;
    }

    Date parseDateFromString(String date){
        try{
            return DATE_FORMAT.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    List<Action> getActions(){
        List<Action> actions = new ArrayList<>();
        for(Action a : actionMongoCollection.find()) actions.add(a);
        return actions;
    }

    boolean areActionsUpToDate(Date date){
        return actionMongoCollection.count(new Document("lastModified", new Document("$gte", date))) > 0;
    }

    boolean areOuiUpToDate(Date date){
        return ouiMongoCollection.count(new Document("lastModified", new Document("$gte", date))) > 0;
    }


    List<Action> getActionsByMacPrefix(String macPrefix){
        List<Action> actions = new ArrayList<>();
        for(Action a : actionMongoCollection.find(new Document("macPrefix", macPrefix))) actions.add(a);
        return actions;
    }

    List<OuiEntry> getOuiAssignments(){
        List<OuiEntry> ouiEntries = new ArrayList<>();
        for(OuiEntry ouiEntry : ouiMongoCollection.find()) ouiEntries.add(ouiEntry);
        return ouiEntries;
    }
}
