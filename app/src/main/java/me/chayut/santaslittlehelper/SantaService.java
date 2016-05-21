package me.chayut.santaslittlehelper;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import me.chayut.SantaHelperLogic.SantaLogic;

public class SantaService extends Service {

    private final static String TAG = "SantaService";
    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    private SantaLogic mSantaLogic;
    private boolean mLogicInitialized = false;

    /** Constructor */
    public SantaService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void onCreate() {
        Log.i(TAG, "Service onCreate()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "onStartCommand()");

        if(!mLogicInitialized) {
            mSantaLogic = new SantaLogic(this);
            mLogicInitialized = true;
        }

        return 1;
    }

    /** method for clients */

    public String getHello(){
        return "Santa Service Hello";
    }

    /**
     * return app logic to any binding activity
     * @return SantaLogic
     */
    public SantaLogic getSantaLogic(){
        if (!mLogicInitialized) {
            mSantaLogic = new SantaLogic(this);

        }
        return mSantaLogic;
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        SantaService getService() {
            // Return this instance of LocalService so clients can call public methods
            return SantaService.this;
        }
    }



    
}
