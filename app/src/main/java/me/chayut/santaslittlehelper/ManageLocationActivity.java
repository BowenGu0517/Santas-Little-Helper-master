package me.chayut.santaslittlehelper;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import me.chayut.SantaHelperLogic.SantaLogic;
import me.chayut.SantaHelperLogic.SantaLocation;
import me.chayut.SantaHelperLogic.SantaLocationAdapter;

public class ManageLocationActivity extends AppCompatActivity {

    private final static String TAG = "ManageLocationActivity";
    private final static int REQUEST_LOCATION = 1;

    SantaService mService;
    SantaLogic mLogic;
    boolean mBound = false;

    private ListView lvLocations;
    private SantaLocationAdapter mAdapter;
    private ArrayList<SantaLocation> list = new ArrayList<SantaLocation>();

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

            UIRefresh();


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
        setContentView(R.layout.activity_manage_location);

        //Listview setup
        lvLocations = (ListView) findViewById(R.id.listViewLocations);

        lvLocations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, final int position, long id) {

                SantaLocation item = mAdapter.getItem(position);

                // 1. Instantiate an AlertDialog.Builder with its constructor
                AlertDialog.Builder builder = new AlertDialog.Builder(ManageLocationActivity.this);

                // 2. Chain together various setter methods to set the dialog characteristics
                builder.setTitle("Location: " + item.getName());

                final String[] array = {"Edit", "Delete"};

                builder.setItems(array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, String.format("Choose: %d", which));
                        switch (which) {
                            case 0:
                                if(mBound){
                                    Intent startAct2 = new Intent(ManageLocationActivity.this, SetupLocationActivity.class);
                                    startAct2.putExtra(SantaLogic.EXTRA_SANTA_LOCATION,list.get(position));
                                    startActivityForResult(startAct2,REQUEST_LOCATION);
                                    UIRefresh();

                                }
                                break;
                            case 1:
                                list.remove(position);
                                UIRefresh();
                                break;
                        }

                    }
                });
                // 3. Get the AlertDialog from create()
                AlertDialog dialog = builder.create();

                dialog.show();
            }

        });

    }

    private void ListUpdate(){
        Log.d(TAG, String.format("Endpoints = %d", list.size()));
        mAdapter = new SantaLocationAdapter(this,R.layout.row_location,list);
        lvLocations.setAdapter(mAdapter);
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

    /** Methods */

    public void onBtnClicked (View v){
        Log.d(TAG,"onBtnClicked");

        if(mBound){

            //add new location
            Intent startAct2 = new Intent(ManageLocationActivity.this, SetupLocationActivity.class);
            startActivityForResult(startAct2,REQUEST_LOCATION);
            UIRefresh();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG,String.format("onActivityResult(): %d", requestCode));

        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_LOCATION  :
                    SantaLocation mLoc =  data.getParcelableExtra(SantaLogic.EXTRA_SANTA_LOCATION);
                    mLogic.addLocation(mLoc);
                    UIRefresh();

                default:
                    break;
            }
        }
    }

    private void UIRefresh(){

        list = mService.getSantaLogic().getLocationList();
        ListUpdate();
    }
}
