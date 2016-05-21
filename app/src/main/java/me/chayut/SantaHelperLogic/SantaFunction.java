package me.chayut.SantaHelperLogic;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;

/**
 * Created by chayut on 12/04/16.
 */
 public class SantaFunction {


    static public void sendSMS(Context context, String phoneNumber,String message)
    {

        final String TAG = "sendSMS";

        SmsManager sms = SmsManager.getDefault();

        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(context,0,new Intent(SENT),0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(context,0,new Intent(DELIVERED),0);

        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Log.d(TAG, "SMS sent");
                        //Toast.makeText(context,"SMS sent",Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Log.d(TAG, "Generic faiture");
                        //Toast.makeText(context,"Generic faiture",Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Log.d(TAG, "No service");
                        //Toast.makeText(getBaseContext(),"No service",Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Log.d(TAG,"Null PDU");
                        //Toast.makeText(getBaseContext(),"Null PDU",Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Log.d(TAG, "Radio off");
                        //Toast.makeText(getBaseContext(),"Radio off", Toast.LENGTH_SHORT).show();
                        break;

                }

            }
        },new IntentFilter(SENT));


        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg01) {
                switch(getResultCode())
                {
                    case Activity.RESULT_OK:
                        Log.d(TAG, "SMS delivered");
                        //Toast.makeText(getBaseContext(),"SMS delivered",Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.d(TAG, "SMS not delivered");
                        //Toast.makeText(getBaseContext(),"SMS not delivered", Toast.LENGTH_SHORT).show();
                        break;

                }
            }
        },new IntentFilter(DELIVERED));
        sms.sendTextMessage(phoneNumber,null,message,sentPI,deliveredPI);
    }


    static public  void setWiFiStatus(Context context, boolean status){
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (status == true && !wifiManager.isWifiEnabled()){
            wifiManager.setWifiEnabled(true);
        }else if (status == false && wifiManager.isWifiEnabled()){
            wifiManager.setWifiEnabled(false);
        }
    }





    static public void setBrightness (Context context,int brightness) {
        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);  //this will set the manual mode (set the automatic mode off)



        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness);  //brightness is an integer variable (0-255), but dont use 0


        try {
            int brightness1 = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);  //returns integer value 0-255
        } catch (Exception e) {
        }


        try {
            int br = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);  //this will get the information you have just set...


        } catch (Exception e) {
        }
    }

    static public void SetAutoBrightness(Context context){
        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);  //this will set the automatic mode on
    }



}
