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

public class MainActivity extends AppCompatActivity {


    private final static String TAG = "MainActivity";

    SantaService mService;
    SantaLogic mLogic;
    boolean mBound = false;

    Button btnManageLocation,btnDebug;
    Button btnManageTask;
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

        setContentView(R.layout.activity_main);

        //start service
        Intent intent = new Intent(MainActivity.this, SantaService.class);
        startService(intent);




        btnManageTask= (Button) findViewById(R.id.btnManageTask);
        btnManageTask.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, TaskListActivity.class);
                        startActivity(intent);
                    }
                }
        );

        btnManageLocation = (Button) findViewById(R.id.btnManageLocation);
        btnManageLocation.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, ManageLocationActivity.class);
                        startActivity(intent);
                    }
                }
        );

        btnDebug = (Button) findViewById(R.id.btnDebugActivity);
        btnDebug.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, DebugActivity.class);
                        startActivity(intent);
                    }
                }
        );
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
