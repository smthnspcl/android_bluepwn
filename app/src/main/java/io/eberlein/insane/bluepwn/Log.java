package io.eberlein.insane.bluepwn;

public class Log {
    static void log(Class c, String msg){
        android.util.Log.i(c.getCanonicalName(), msg);
    }

    static void initGPS(Class c){
        log(c, "initializing gps");
    }

    static void initGPSFinished(Class c){
        log(c, "gps initialized");
    }

    static void onResume(Class c){
        log(c, "onResume() called");
    }

    static void onStart(Class c){
        log(c, "onStart() called");
    }

    static void onStop(Class c){
        log(c, "onStop() called");
    }

    static void onCreate(Class c){
        log(c, "onCreate() called");
    }

    static void onDestroy(Class c){
        log(c, "onDestroy() called");
    }

    static void onPause(Class c){
        log(c, "onPause() called");
    }
}
