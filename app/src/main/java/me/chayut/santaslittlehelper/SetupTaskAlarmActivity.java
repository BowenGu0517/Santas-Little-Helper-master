package me.chayut.santaslittlehelper;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import me.chayut.SantaHelperLogic.SantaAction;
import me.chayut.SantaHelperLogic.SantaLogic;
import me.chayut.SantaHelperLogic.SantaTask;
import me.chayut.SantaHelperLogic.SantaTaskAppoint;

public class SetupTaskAlarmActivity extends AppCompatActivity implements View.OnClickListener {


    static final int REQUEST_ACTION =1;
    private final static String TAG = "SetupTaskAlarmActivity";
    static Calendar c = Calendar.getInstance();
    Button btnOK, btnCancel,btnSetAction;
    SantaTaskAppoint mTask;
    private Button btn_set_time;
    private Button btn_set_date;
    private TextView tvActionDetail;

    private boolean isSet = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_task_alarm);




        //setup UI comp
        tvActionDetail = (TextView) findViewById(R.id.tvActionDetails);

        btnOK = (Button) findViewById(R.id.btnOK);
        btnOK.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent();

                        //TODO: read value from UI before return

                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
                        String outString = sdf.format(c.getTime());
                        mTask.setTimeString(outString);

                        Calendar cal = Calendar.getInstance();
                        try {
                            cal.setTime(sdf.parse(outString));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        //TODO: verify the the user input is valid
                        intent.putExtra(SantaLogic.EXTRA_SANTA_TASK_APPOINT,mTask);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
        );
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        setResult(RESULT_CANCELED, intent);
                        finish();
                    }
                }
        );

        btnSetAction = (Button) findViewById(R.id.btnSetAction);
        btnSetAction.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(SetupTaskAlarmActivity.this,SelectActionActivity.class);
                        intent.putExtra(SantaLogic.EXTRA_SANTA_ACTION,mTask.getAction());
                        startActivityForResult(intent,REQUEST_ACTION);
                    }
                });


        btn_set_time = (Button) findViewById(R.id.btn_set_time);
        btn_set_date = (Button) findViewById(R.id.btn_set_date);
        btn_set_time.setOnClickListener(this);
        btn_set_date.setOnClickListener(this);


        //Get parcelable
        if(getIntent().hasExtra(SantaLogic.EXTRA_SANTA_TASK_APPOINT))
        {
            mTask =getIntent().getParcelableExtra(SantaLogic.EXTRA_SANTA_TASK_APPOINT);

            //if there is parcelable, load value to UI

            String DateTime = mTask.getTimeString();//TODO[UI]:load this onto UI

            tvActionDetail.setText(mTask.getAction().getTaskTypeString());

        }
        else
        {
            //if no intent parcelable, create new
            mTask = new SantaTaskAppoint();
            mTask.setAction(new SantaAction());
        }

    }

    @Override
    public void onClick(View v) {
        Calendar currentTime = Calendar.getInstance();
        switch (v.getId()){
            case R.id.btn_set_time:

                new TimePickerDialog(SetupTaskAlarmActivity.this, 0,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view,
                                                  int hourOfDay, int minute) {

                                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                c.set(Calendar.MINUTE, minute);
                                c.set(Calendar.SECOND, 0);
                                c.set(Calendar.MILLISECOND, 0);
                                TextView textView = (TextView) findViewById(R.id.textView2);
                                textView.setText("HH:MM:SS:   "+c.get(Calendar.HOUR_OF_DAY)+": "+c.get(Calendar.MINUTE)+ ": "+c.get(Calendar.SECOND));

                            }
                        }, currentTime.get(Calendar.HOUR_OF_DAY), currentTime
                        .get(Calendar.MINUTE), false).show();
                break;
            case R.id.btn_set_date:
                DatePickerDialog dpd = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                c.set(Calendar.YEAR, year);
                                c.set(Calendar.MONTH, monthOfYear);
                                c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                TextView textView = (TextView) findViewById(R.id.textView);
                                textView.setText("YY:MM:DD:  "+c.get(Calendar.YEAR)+": "+(c.get(Calendar.MONTH)+1)+ ":"+c.get(Calendar.DAY_OF_MONTH));
                            }
                        }, currentTime.get(Calendar.YEAR), currentTime.get(Calendar.MONTH),  currentTime.get(Calendar.DAY_OF_MONTH)   );
                dpd.show();
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG,String.format("onActivityResult(): %d: %d", requestCode,resultCode));

        if(resultCode == RESULT_OK) {

            switch (requestCode) {
                case REQUEST_ACTION:

                    SantaAction returnAction = (SantaAction) data.getParcelableExtra(SantaLogic.EXTRA_SANTA_ACTION);
                    mTask.setAction(returnAction);

                    //update UI according to returned action
                    tvActionDetail.setText(returnAction.getTaskTypeString());

                    Log.d(TAG, SantaLogic.EXTRA_SANTA_ACTION);
                    break;
                default:
                    break;
            }
        }
    }


}
