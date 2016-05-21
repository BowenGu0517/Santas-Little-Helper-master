package me.chayut.santaslittlehelper;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import me.chayut.SantaHelperLogic.SantaLogic;
import me.chayut.SantaHelperLogic.SantaTaskAppoint;

public class AlarmActivity extends AppCompatActivity {


    private final static String TAG = "AlarmActivity";

    SantaService mService;
    SantaLogic mLogic;
    boolean mBound = false;

    int ButtonCancel_clicked = 0;
    private MediaPlayer mediaPlayer;

    private String taskUUID = "";
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
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        mediaPlayer = mediaPlayer.create(this,R.raw.pig);
        mediaPlayer.start();

        SantaTaskAppoint mTask;

        // load task extra parcelable

        //Get parcelable
        if(getIntent().hasExtra(SantaLogic.EXTRA_SANTA_TASK_APPOINT))
        {
            mTask =getIntent().getParcelableExtra(SantaLogic.EXTRA_SANTA_TASK_APPOINT);
            taskUUID = mTask.getUuid();
            Log.d(TAG,"parcelable for:" + taskUUID);
        }

        //set volume to max
        AudioManager am =
                (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        final int origionalVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);

        am.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                am.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                0);

        Toast.makeText(getApplicationContext(), "TIME UP", Toast.LENGTH_LONG).show();

        final Handler handler = new Handler(){
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        if(ButtonCancel_clicked==0) {

                            Toast.makeText(getApplicationContext(), "Sorry, No One Heard the Alarm ", Toast.LENGTH_LONG).show();

                            //issue alert to logic that alarm missed
                            if(mBound){
                                mLogic.onAlarmMissed(taskUUID);
                            }
                            else{
                                Toast.makeText(getBaseContext(),"communication with logic error",
                                        Toast.LENGTH_SHORT).show();
                            }

                            mediaPlayer.stop();

                            //return volume to original
                            AudioManager am =
                                    (AudioManager) getSystemService(Context.AUDIO_SERVICE);

                            am.setStreamVolume(
                                    AudioManager.STREAM_MUSIC,
                                    origionalVolume,
                                    0);


                            AlarmActivity.this.finish();

                            break;

                        }
                }
                super.handleMessage(msg);
            }
        };

        TimerTask task = new TimerTask(){
            public void run() {
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        };

        Timer timer;
        timer = new Timer(true);
        timer.schedule(task, 1000 * 1 * 15);  //  Delay 1000ms *5    5s

        new AlertDialog.Builder(AlarmActivity.this).setTitle("alarm").setMessage("time up ~")
                .setPositiveButton("close alarm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ButtonCancel_clicked = 1;
                        mediaPlayer.stop();

                        //return volume to original
                        AudioManager am =
                                (AudioManager) getSystemService(Context.AUDIO_SERVICE);

                        am.setStreamVolume(
                                AudioManager.STREAM_MUSIC,
                                origionalVolume,
                                0);

                        AlarmActivity.this.finish();


                    }
                }).show();
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        Log.d(TAG, "onResume()");

        if(mBound){
            Log.d(TAG,mService.getHello());
        }
        else
        {
            // Bind to LocalService
            Intent intent = new Intent(this, SantaService.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
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
