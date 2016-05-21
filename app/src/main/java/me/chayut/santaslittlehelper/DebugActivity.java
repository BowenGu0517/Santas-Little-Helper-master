package me.chayut.santaslittlehelper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import me.chayut.SantaHelperLogic.SantaLogic;
import me.zhenning.AccountInfoTestActivity;
import me.zhenning.AccountSelectActivity;

public class DebugActivity extends AppCompatActivity {

    private final static String TAG = "DebugActivity";

    SantaService mService;
    SantaLogic mLogic;
    boolean mBound = false;

    Button btnTestJSON,btnReadConf,btnTestAlarm;
    Button btnSendEmail,btnSendSMS,btnWifiOn;
    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            SantaService.LocalBinder binder = (SantaService.LocalBinder) service;
            mService = binder.getService();
            mLogic = mService.getSantaLogic();
            mBound = true;


            Log.d(TAG,mService.getHello());
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");

        setContentView(R.layout.activity_debug);

        //start service
        Intent intent = new Intent(DebugActivity.this, SantaService.class);
        startService(intent);


        btnSendEmail = (Button) findViewById(R.id.btnSendEmail);
        btnSendEmail.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        if(mLogic!=null) {
                            mLogic.sendEmailTest("FOO@BAR.com", "BAR"); //test function
                        }
                    }
                }
        );


        btnSendSMS= (Button) findViewById(R.id.buttonSendSMS);
        btnSendSMS.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {

                        Log.d(TAG,"SMS Test pressed ");
                        if(mLogic!=null) {
                            mLogic.sendSMS("0478167509", "This is Santa's Message"); //test function
                        }
                    }
                }
        );


        btnWifiOn = (Button) findViewById(R.id.btnWifiOn);
        btnWifiOn.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        mLogic.onWifi(); //test function
                    }
                }
        );

        btnTestJSON = (Button) findViewById(R.id.btnJSON);
        btnTestJSON.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        mLogic.writeSantaConfig();
                    }
                }
        );

        btnReadConf = (Button) findViewById(R.id.btnConfRead);
        btnReadConf.setOnClickListener(

                new View.OnClickListener() {
                    public void onClick(View v) {
                        if(  mLogic.readSantaConfig())
                        {
                            Toast.makeText(getBaseContext(),"Read successful",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(getBaseContext(),"Read Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        btnTestAlarm = (Button) findViewById(R.id.btnTestAlarm);
        btnTestAlarm.setOnClickListener(

                new View.OnClickListener() {
                    public void onClick(View v) {

                        Calendar currentTime = Calendar.getInstance();

                        new TimePickerDialog(DebugActivity.this, 0,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view,
                                                          int hourOfDay, int minute) {

                                        AlarmManager alarmManager;
                                        PendingIntent pi;

                                        Calendar currentTime = Calendar.getInstance();

                                        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                                        Intent intent = new Intent(DebugActivity.this, AlarmActivity.class);
                                        pi = PendingIntent.getActivity(DebugActivity.this, 0, intent, 0);

                                        Calendar c = Calendar.getInstance();
                                        c.setTimeInMillis(System.currentTimeMillis());

                                        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                        c.set(Calendar.MINUTE, minute);
                                        c.set(Calendar.SECOND, 0);
                                        c.set(Calendar.MILLISECOND, 0);

                                        alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
                                        Log.e("HEHE",c.getTimeInMillis()+"");
                                        Toast.makeText(DebugActivity.this, "the alarm has been set:\n"+ c.getTime(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }, currentTime.get(Calendar.HOUR_OF_DAY), currentTime
                                .get(Calendar.MINUTE), false).show();
                    }
                }
        );

        findViewById(R.id.account_test).setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(DebugActivity.this, AccountSelectActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.AccountInfoTest).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(DebugActivity.this, AccountInfoTestActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        Log.d(TAG, "onResume()");


        if(mBound){
            Log.d(TAG,mService.getHello());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, SantaService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

}
