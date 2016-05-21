package me.chayut.santaslittlehelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import me.chayut.SantaHelperLogic.SantaAction;


public class BootReceiver extends BroadcastReceiver {


    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceiver: " + intent.getAction());

        Intent myIntent = new Intent(context, SantaService.class);
        context.startService(myIntent);

    }


}