package me.chayut.SantaHelperLogic;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;


import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import me.chayut.santaslittlehelper.AlarmActivity;
import me.zhenning.AccountGeneral;
import me.zhenning.EmailSender;

/**
 * Created by chayut on 25/03/16.
 */
public class SantaLogic {

    /** const */
    public static final String EXTRA_SANTA_TASK = "EXTRA_SANTA_TASK";
    public static final String EXTRA_SANTA_TASK_APPOINT = "EXTRA_SANTA_TASK_APPOINT";
    public static final String EXTRA_SANTA_TASK_BATT = "EXTRA_SANTA_TASK_BATT";
    public static final String EXTRA_SANTA_TASK_LOC = "EXTRA_SANTA_TASK_LOC";
    public static final String EXTRA_SANTA_ACTION = "EXTRA_SANTA_ACTION";
    public static final String EXTRA_SANTA_LOCATION= "EXTRA_SANTA_LOCATION";
    public static final String JTAG_SANTA_TASK_TYPE = "SantaTaskType";
    public static final String JTAG_SANTA_ACTION= "Action";
    public static final String JTAG_SANTA_TASK_APPOINT = "SANTA_TASK_APPOINT";
    public static final String JTAG_SANTA_DATETIME = "DateTime";
    public static final String JTAG_SANTA_TASK_BATT = "SANTA_TASK_BATT";
    public static final String JTAG_SANTA_BATT_LEVEL = "BattLevel";
    public static final String JTAG_SANTA_TASK_LOC = "SANTA_TASK_LOC";
    public static final String JTAG_SANTA_LAT = "Lat";
    public static final String JTAG_SANTA_LONG = "Long";
    public static final String JTAG_SANTA_Range = "Range";
    public static final String JTAG_SANTA_TASK_LIST = "TaskList";
    public static final String JTAG_SANTA_LOC_LIST = "locList";
    public static final String JTAG_UUID = "uuid";
    private static final String TAG = "SantaLogic";

    Context mContext;
    ScheduledThreadPoolExecutor sch;
    private ArrayList<EndPoint> endPoints;
    private ArrayList<SantaLocation> locationList;
    private ArrayList<SantaTask> taskList;
    final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            SantaLogic.this.onLocationUpdateReceived(location);
        }
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        public void onProviderEnabled(String provider) {}
        public void onProviderDisabled(String provider) {}
    };
    //location
    private LocationManager locationManager;
    private boolean isLocationListenerRegistered = false;
    private int mBattPercentage = 50;
    final Runnable periodicTask = new Runnable(){

        Handler mHandler = new Handler(Looper.getMainLooper());

        @Override
        public void run() {
            try{
                Log.d(TAG,"periodicTask.run()");

                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {


                        //TODO[note]:This section will run every minute
                        //check anything on UI thread

                        //check list for time
                        checkTimeOnTaskAppoint();


                        //location monitoring stuff
                        if(hasLocationTask()) {
                            if (!isLocationListenerRegistered) {
                                initLocMon(mBattPercentage);
                            }
                        }
                        else
                        {
                            //there is no task about location
                            if(isLocationListenerRegistered)
                            {
                                stopLocMon();
                            }
                        }


                    }
                };
                mHandler.post(myRunnable);


            }catch(Exception e){
                e.printStackTrace();
            }
        }
    };
    private String mLoadedEmail = "";
    private String mLoadedPassword = "";
    private boolean creadentialLoaded = false;
    private int mAccountNumber = 0;
    private Account mAccountSelected;

    public SantaLogic(Context context) {

        Log.d(TAG,"Santa Logic initiated");

        mContext = context;

        endPoints = new ArrayList<>();
        taskList = new ArrayList<>();
        locationList = new ArrayList<>();

        //read config on start
        readSantaConfig();

        //load user credential on start
        loadUserCredential();

        //init monitoring service
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        initBattMon();

        //start periodical task
        // Create a scheduled thread pool with 3 core threads
        sch = (ScheduledThreadPoolExecutor)
                Executors.newScheduledThreadPool(3);
        //TODO: set repetition time according to condition.
        ScheduledFuture<?> periodicFuture = sch.scheduleAtFixedRate(periodicTask, 0, 1, TimeUnit.MINUTES);

    }

    public boolean hasLocationTask()
    {
        for (SantaTask mTask : taskList)
        {
            if (mTask instanceof SantaTaskLocation) {
                return true;
            }
        }

        return false;
    }

    public boolean addEndPoint (EndPoint mEP){
        Log.d(TAG, "addEndPoint");
        endPoints.add(mEP);
        return true;
    }

    public ArrayList<EndPoint> getEndPoints() {
        return endPoints;
    }

    public ArrayList<SantaTask> getTaskList() {
        return taskList;
    }

    public ArrayList<SantaLocation> getLocationList() {
        return locationList;
    }

    //region task
    public boolean addTask (SantaTask task){

        //check if task UUID already exist, update instead

        for (int n = 0; n < taskList.size() ; n++)
        {
            SantaTask mTask = taskList.get(n);

            if (mTask instanceof SantaTaskAppoint) {

                SantaTaskAppoint task2 = (SantaTaskAppoint) mTask;
                if (task2.getUuid().equals(task.getUuid())) {

                    taskList.remove(n);
                    taskList.add(n,task);
                    writeSantaConfig();
                    return true;
                }
            }
            else if  (mTask instanceof SantaTaskLocation){
                SantaTaskLocation task2 = (SantaTaskLocation) mTask;
                if (task2.getUuid().equals(task.getUuid())) {
                    taskList.remove(n);
                    taskList.add(n,task);
                    writeSantaConfig();
                    return true;
                }

            }
            else if  (mTask instanceof SantaTaskBattery){
                SantaTaskBattery task2 = (SantaTaskBattery) mTask;
                if (task2.getUuid().equals(task.getUuid())) {
                    taskList.remove(n);
                    taskList.add(n,task);
                    writeSantaConfig();
                    return true;
                }
            }
        }

        //if not on the list
        taskList.add(task);

        //update config file
        writeSantaConfig();

        return true;
    }

    public SantaTask getTaskByUUID (String taskUUID){

        for (SantaTask mTask : taskList)
        {

            if (mTask instanceof SantaTaskAppoint) {

                SantaTaskAppoint task = (SantaTaskAppoint) mTask;
                if (task.getUuid().equals(taskUUID)) {
                    return mTask;
                }
            }
            else if  (mTask instanceof SantaTaskLocation){
                SantaTaskLocation task = (SantaTaskLocation) mTask;
                if (task.getUuid().equals(taskUUID)) {
                    return mTask;
                }

            }
            else if  (mTask instanceof SantaTaskBattery){
                SantaTaskBattery task = (SantaTaskBattery) mTask;
                if (task.getUuid().equals(taskUUID)) {
                    return mTask;
                }
            }
        }

        return null;
    }

    /**
     * @param taskUUID
     * @return true if remove success
     */
    public boolean removeTaskByUUID (String taskUUID){

        for (int n = 0; n < taskList.size() ; n++)
        {
            SantaTask mTask = taskList.get(n);

            if (mTask instanceof SantaTaskAppoint) {

                SantaTaskAppoint task = (SantaTaskAppoint) mTask;
                if (task.getUuid().equals(taskUUID)) {
                    taskList.remove(n);
                    writeSantaConfig();
                    return true;
                }
            }
            else if  (mTask instanceof SantaTaskLocation){
                SantaTaskLocation task = (SantaTaskLocation) mTask;
                if (task.getUuid().equals(taskUUID)) {
                    taskList.remove(n);
                    writeSantaConfig();
                    return true;
                }

            }
            else if  (mTask instanceof SantaTaskBattery){
                SantaTaskBattery task = (SantaTaskBattery) mTask;
                if (task.getUuid().equals(taskUUID)) {
                    taskList.remove(n);
                    writeSantaConfig();
                    return true;
                }
            }
        }

        return false;
    }

    public boolean addLocation (SantaLocation location){
        locationList.add(location);
        writeSantaConfig();
        return true;
    }
    //endregion


    //region runnable

    //
    public void loadUserCredential()
    {
        AccountManager mAccountManager;

        mAccountManager  = AccountManager.get(mContext);
        final Account availableAccounts[] = mAccountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);
        if (availableAccounts.length == 0) {
            //Toast.makeText(mContext, "No account added", Toast.LENGTH_SHORT).show();
            creadentialLoaded = false;
            return;
        }
        else {
            String name[] = new String[availableAccounts.length];
            for (int i = 0; i < availableAccounts.length; i++) {
                name[i] = availableAccounts[i].name;

                mAccountSelected = availableAccounts[0]; //TODO: just selected account
            }

            // Account picker
            /*
            mAlertDialog = new AlertDialog.Builder(mContext).setTitle("Pick A Account").setAdapter(
                    new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, name),
                    new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mAccountSelected = availableAccounts[which];
                }
            }).create();

            mAlertDialog.show();
            */
        }

        //TODO: Future: Maybe decrypt users' password
        mLoadedEmail = mAccountSelected.name;
        mLoadedPassword = mAccountManager.getPassword(mAccountSelected);
        creadentialLoaded = true;
    }

    //endregion


    //region test

    /** Test  Section */

    public void sendEmailTest(String email,String password)
    {

        try {
            EmailSender sender = new EmailSender(email, password);
            sender.sendMail("Test 2 ",
                    "Test 2",
                    "nonesecure@gmail.com",
                    "chayut_o@hotmail.com");
        } catch (Exception e) {
            Log.e("SendMail", e.getMessage(), e);
        }
    }

    public void sendEmail()
    {
        //TODO:use loadedUserCredential
        try {
            EmailSender sender = new EmailSender(mLoadedEmail, mLoadedPassword);
            sender.sendMail("Test 2 ",
                    "Test 2",
                    "nonesecure@gmail.com",
                    "chayut_o@hotmail.com");
        } catch (Exception e) {
            Log.e("SendMail", e.getMessage(), e);
        }
    }

    public void sendSMS (String phoneNumber,String message){
        SantaFunction.sendSMS(mContext, phoneNumber,message);
    }


    //region on receive update

    public void onWifi (){
        SantaFunction.setWiFiStatus(mContext,true);
    }

    //endregion

    /** on received update  Section */

    public void onLocationUpdateReceived(Location location){
        Log.d(TAG,String.format("Location: %s",location.toString()));

        //checking the tasks list
        for (SantaTask mTask : taskList)
        {
            if (mTask instanceof SantaTaskLocation) {
                SantaTaskLocation task = (SantaTaskLocation) mTask;

                if(task.isConditionMet(location)){
                    //execute action
                    SantaAction action = task.getAction();
                    executeAction(action);
                    //TODO[L]:remove from list
                }
            }

        }
    }

    public void onBatteryPercentageReceived(int percentage){
        Log.d(TAG,String.format("Batt: %d",percentage));

        mBattPercentage = percentage; //keep track of the batt for location optimization

        for (SantaTask mTask : taskList)
        {
            if (mTask instanceof SantaTaskBattery) {
                SantaTaskBattery task = (SantaTaskBattery) mTask;

                if(task.isConditionMet(percentage)){
                    //execute action
                    SantaAction action = task.getAction();
                    executeAction(action);
                }

            }

        }
    }

    public void checkTimeOnTaskAppoint(){

        Log.d(TAG,"checkTimeOnTaskAppoint()");

        for (SantaTask mTask : taskList)
        {
            if (mTask instanceof SantaTaskAppoint) {
                SantaTaskAppoint task = (SantaTaskAppoint) mTask;

                if(task.isConditionMet()){

                    //issue alarm before take action
                    Intent i = new Intent(mContext,AlarmActivity.class);
                    i.putExtra(SantaLogic.EXTRA_SANTA_TASK_APPOINT,task);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    Log.d(TAG,"HEHEHE running~");
                    mContext.startActivity(i);

                    removeTaskByUUID(mTask.uuid);
                }

            }

        }
    }

    public void onAlarmMissed(String uuid){
        Log.d(TAG,"onAlarmMissed()");
        //call this function,  alarm missed when user miss the alarm
        SantaTaskAppoint task= (SantaTaskAppoint) getTaskByUUID(uuid);

        //take action
        if(task != null ) {

            SantaAction action = task.getAction();
            executeAction(action);
            //TODO[L]:remove from list
        }

    }
    //endregion


    //region execute action
    /** Execute Action Section */
    public void executeAction(SantaAction action){

        Log.d(TAG,"executeAction()");

        switch (action.getTaskType()){
            case SantaAction.ACTION_EMAIL:
                String email = action.getEmail();
                String message = action.getMessage();

                //TODO: zhenning -> send email

                break;


            case SantaAction.ACTION_SMS:

                //get info
                String phonenumber = action.getPhoneNumber();
                String sms = action.getMessage();

                //send sms
                SantaFunction.sendSMS(mContext,phonenumber,sms);

                break;

            case SantaAction.ACTION_WIFI:

                action.getWifiState();

                if(action.getWifiState())
                {
                    //turn on wifi
                    SantaFunction.setWiFiStatus(mContext,true);
                }
                else {
                    //turn off wifi
                    SantaFunction.setWiFiStatus(mContext,false);
                }

                break;
            default:
        }

    }
    //endregion

    //region JSON config

    /** JSON Section   */

    public JSONArray getLocationListJSON() throws JSONException {

        JSONArray jArray = new JSONArray();
        for (SantaLocation mLocation : locationList)
        {
            /*
            if (mTask instanceof SantaTaskAppoint) {

                SantaTaskAppoint task = (SantaTaskAppoint) mTask;
                jArray.put(task.toJSONObject());
            }
            else if  (mTask instanceof SantaTaskLocation){
                SantaTaskLocation task = (SantaTaskLocation) mTask;
                jArray.put(task.toJSONObject());
            }
            else if  (mTask instanceof SantaTaskBattery){
                SantaTaskBattery task = (SantaTaskBattery) mTask;
                jArray.put(task.toJSONObject());
            }
            */
            Gson gson = new Gson();

            jArray.put(new JSONObject( gson.toJson(mLocation)));

        }

        return jArray;
    }

    public JSONArray getTaskListJSON(){

        JSONArray jArray = new JSONArray();
        for (SantaTask mTask : taskList)
        {
            /*
            if (mTask instanceof SantaTaskAppoint) {

                SantaTaskAppoint task = (SantaTaskAppoint) mTask;
                jArray.put(task.toJSONObject());
            }
            else if  (mTask instanceof SantaTaskLocation){
                SantaTaskLocation task = (SantaTaskLocation) mTask;
                jArray.put(task.toJSONObject());
            }
            else if  (mTask instanceof SantaTaskBattery){
                SantaTaskBattery task = (SantaTaskBattery) mTask;
                jArray.put(task.toJSONObject());
            }
            */
            jArray.put(mTask.toJSONObject());

        }

        return jArray;
    }

    public void writeSantaConfig(){

        JSONObject mObject = new JSONObject();

        try {
            mObject.put(JTAG_SANTA_TASK_LIST, getTaskListJSON());
            mObject.put(JTAG_SANTA_LOC_LIST, getLocationListJSON());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG,"writeSantaConfig()");
        Log.d(TAG,mObject.toString());
        SantaUtilities.saveConfigToFile(mObject);

    }

    //endregion

    public boolean readSantaConfig(){

        JSONObject mObject = SantaUtilities.readConfigFromFile();

        try {

            //load json into object list
            JSONArray taskArray = mObject.getJSONArray(JTAG_SANTA_TASK_LIST);

            Log.d(TAG,taskArray.toString());

            taskList.clear();

            for(int n = 0; n < taskArray.length(); n++)
            {
                JSONObject object = taskArray.getJSONObject(n);

                String taskType = object.getString(JTAG_SANTA_TASK_TYPE);

                Log.d(TAG,taskType);

                if(taskType.equals(JTAG_SANTA_TASK_APPOINT)){

                    String timeString = object.getString(JTAG_SANTA_DATETIME);
                    String uuid = object.getString(JTAG_UUID);
                    String actionString = object.getString(JTAG_SANTA_ACTION);

                    Gson gson = new Gson();
                    SantaAction action = gson.fromJson(actionString,SantaAction.class);

                    SantaTaskAppoint newTask = new SantaTaskAppoint(timeString,action);
                    newTask.setUuid(uuid);
                    taskList.add(newTask);
                    Log.d(TAG,taskType + " Added");
                }
                else if (taskType.equals(JTAG_SANTA_TASK_BATT)){

                    int battPercent = object.getInt(JTAG_SANTA_BATT_LEVEL);
                    String uuid = object.getString(JTAG_UUID);

                    String actionString = object.getString(JTAG_SANTA_ACTION);
                    Gson gson = new Gson();
                    SantaAction action = gson.fromJson(actionString,SantaAction.class);
                    SantaTaskBattery newTask = new SantaTaskBattery(battPercent,action);
                    newTask.setUuid(uuid);
                    taskList.add(newTask);
                    Log.d(TAG,taskType + " Added");
                }
                else if(taskType.equals(JTAG_SANTA_TASK_LOC)){

                    float lat = object.getLong(JTAG_SANTA_LAT);
                    float longitude = object.getLong(JTAG_SANTA_LONG);
                    float range = object.getLong(JTAG_SANTA_Range);
                    String uuid = object.getString(JTAG_UUID);

                    String actionString = object.getString(JTAG_SANTA_ACTION);
                    Gson gson = new Gson();
                    SantaAction action = gson.fromJson(actionString,SantaAction.class);

                    SantaTaskLocation newTask = new SantaTaskLocation(action,lat,longitude,range);
                    newTask.setUuid(uuid);
                    taskList.add(newTask);
                    Log.d(TAG,taskType + " Added");
                }

            }

            //load json into object list
            JSONArray locArray = mObject.getJSONArray(JTAG_SANTA_LOC_LIST);
            for(int n = 0; n < locArray.length(); n++)
            {
                JSONObject object =locArray.getJSONObject(n);
                Gson gson = new Gson();
                SantaLocation location = gson.fromJson(object.toString(),SantaLocation.class);
                locationList.add(location);
            }


            return true;

        }
        catch (JSONException e) {
            e.printStackTrace();

            return false;
        }
        catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }

    //region updater
    private void initBattMon(){

        BroadcastReceiver batteryLevel = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                context.unregisterReceiver(this);
                int currentLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);

                //Log.d(TAG,"Batt: " + currentLevel + "%");

                //report battery level to app logic
                SantaLogic.this.onBatteryPercentageReceived(currentLevel);
            }
        };

        IntentFilter batteryLevelFilter = new IntentFilter(
                Intent.ACTION_BATTERY_CHANGED);
        mContext.registerReceiver(batteryLevel, batteryLevelFilter);
    }

    private void initLocMon(int Batt){


        /* ********************************************************************************************************************************************************* */
        // Register the listener LocationManager
        try
        {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
        }
        catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void stopLocMon(){

        locationManager.removeUpdates(locationListener);

    }

    //endregion



}
