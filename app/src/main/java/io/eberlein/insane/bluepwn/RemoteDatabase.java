package io.eberlein.insane.bluepwn;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RemoteDatabase {

    private static final String DEVICES_COLLECTION = "devices";
    private static final String ACTIONS_COLLECTION = "actions";
    private static final String OUI_COLLECTION = "oui";
    // private static final String BLUEBORNE_COLLECTION = "blueborne";

    private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:MM:SS", Locale.getDefault());

    RemoteDatabase(RemoteDBSettings settings){

    }

    // todo prevent nosql injection
    // clean up/ reduce redundant functions

    void insertDevice(Device device){

    }

    void insertAction(Action action){

    }

    List<Device> getDevices(){
        return new ArrayList<>();
    }

    // theoretically just gets more?
    Boolean deviceCountEquals(Integer count){
        return false;
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
        return new ArrayList<>();
    }

    boolean areActionsUpToDate(Date date){
        return false;
    }

    boolean areOuiUpToDate(Date date){
        return false;
    }


    List<Action> getActionsByMacPrefix(String macPrefix){
        return new ArrayList<>();
    }

    List<OuiEntry> getOuiAssignments(){
        return new ArrayList<>();
    }
}
