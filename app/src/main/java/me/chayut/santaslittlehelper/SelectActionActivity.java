package me.chayut.santaslittlehelper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import me.chayut.SantaHelperLogic.SantaAction;
import me.chayut.SantaHelperLogic.SantaLogic;
import me.chayut.SantaHelperLogic.SantaTaskAppoint;

public class SelectActionActivity extends AppCompatActivity {


    Button btnOK, btnCancel;
    SantaAction mAction;
    RadioButton rbSMS,rbEmail,rbWifi;
    private EditText etSMSRecipient, etSMSMessage;
    private EditText etEmailRecipient, etEmailContent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_action);

        etSMSRecipient = (EditText) findViewById(R.id.edit_SMS_recipient);
        etSMSMessage = (EditText) findViewById(R.id.edit_SMS_content);
        etEmailRecipient = (EditText) findViewById(R.id.edit_email_recipient);
        etEmailContent = (EditText) findViewById(R.id.edit_email_content);

        btnOK = (Button) findViewById(R.id.btnOK);
        btnOK.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent();

                        //TODO[1]: update action with value in UI before return!!!

                        if(rbEmail.isChecked())
                        {
                            mAction.setTaskType(SantaAction.ACTION_EMAIL);

                            // set value here
                            String emailRecipient = etEmailRecipient.getText().toString();
                            String emailMessage = etEmailContent.getText().toString();
                            mAction.setEmail(emailRecipient);
                            mAction.setMessage(emailMessage);

                        }
                        else if(rbSMS.isChecked())
                        {
                            mAction.setTaskType(SantaAction.ACTION_SMS);

                            // set value here
                            String SMSRecipient = etSMSRecipient.getText().toString();
                            String SMSMessage = etSMSMessage.getText().toString();
                            mAction.setPhoneNumber(SMSRecipient);
                            mAction.setMessage(SMSMessage);
                        }
                        else if (rbWifi.isChecked()) {
                            mAction.setTaskType(SantaAction.ACTION_WIFI);
                        }

                        intent.putExtra(SantaLogic.EXTRA_SANTA_ACTION,mAction);
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


        //radio button setup
        rbSMS = (RadioButton) findViewById(R.id.rbSMS);
        rbEmail = (RadioButton) findViewById(R.id.rbEmail);
        rbWifi = (RadioButton) findViewById(R.id.rbWifi);

        rbSMS.setOnClickListener(new View.OnClickListener() {
                                     public void onClick(View v) {
                                         rbSMS.setChecked(true);
                                         rbEmail.setChecked(false);
                                         rbWifi.setChecked(false);


                                     }
                                 }
        );

        rbEmail.setOnClickListener(new View.OnClickListener() {
                                     public void onClick(View v) {
                                         rbSMS.setChecked(false);
                                         rbEmail.setChecked(true);
                                         rbWifi.setChecked(false);
                                     }
                                 }
        );

        rbWifi.setOnClickListener(new View.OnClickListener() {
                                     public void onClick(View v) {
                                         rbSMS.setChecked(false);
                                         rbEmail.setChecked(false);
                                         rbWifi.setChecked(true);
                                     }
                                 }
        );


        //try to get parcelable
        if(getIntent().hasExtra(SantaLogic.EXTRA_SANTA_ACTION))
        {
            mAction = getIntent().getParcelableExtra(SantaLogic.EXTRA_SANTA_ACTION);

            //TODO: if there is parcelable, load value to UI

            //if no intent parcelable, create new


            switch (mAction.getTaskType()){
                case SantaAction.ACTION_EMAIL:
                    rbEmail.callOnClick();
                    //TODO:setup UI
                    break;
                case SantaAction.ACTION_SMS:
                    rbSMS.callOnClick();
                    //TODO:setup UI
                    break;
                case SantaAction.ACTION_WIFI:
                    rbWifi.callOnClick();
                    //TODO:setup UI
                    break;
                case SantaAction.ACTION_NULL:
                    break;
            }

        }
        else
        {
            mAction = new SantaAction();
        }


    }




}
