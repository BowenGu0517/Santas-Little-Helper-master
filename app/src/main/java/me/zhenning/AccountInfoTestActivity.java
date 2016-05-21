package me.zhenning;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import me.chayut.santaslittlehelper.R;

/**
 * Created by jiang on 2016/05/04.
 */
public class AccountInfoTestActivity extends AppCompatActivity{

    TextView displayAccountName;
    TextView displayAccountPassword;
    Button btnGetAccountInfo;

    private String TAG = this.getClass().getSimpleName();
    private AccountManager mAccountManager;
    private AlertDialog mAlertDialog;
    private Account mAccountSelected;
    private boolean mSelected;
    private static final String STATE_DIALOG = "state_dialog";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_account_login);

        displayAccountName = (TextView) findViewById(R.id.displayAccountName);
        displayAccountPassword = (TextView) findViewById(R.id.displayPassword);
        btnGetAccountInfo = (Button) findViewById(R.id.showAccountInfo);

        mAccountManager = AccountManager.get(this);

        btnGetAccountInfo.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                getAccountInfo();
            }
        });

    }

    protected void getAccountInfo(){
        final Account availableAccounts[] = mAccountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);

        if (availableAccounts.length == 0) {
            Toast.makeText(this, "No account added", Toast.LENGTH_SHORT).show();
        } else {
            String name[] = new String[availableAccounts.length];
            for (int i = 0; i < availableAccounts.length; i++) {
                name[i] = availableAccounts[i].name;
            }

            // Account picker
            mAlertDialog = new AlertDialog.Builder(this).setTitle("Pick Account").setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, name), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mAccountSelected = availableAccounts[which];
                    displayAccountName.setText(mAccountSelected.name);
                    String userPassword = mAccountManager.getPassword(mAccountSelected);
                    displayAccountPassword.setText(userPassword);
                    mSelected = true;
                }
            }).create();
            mAlertDialog.show();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            outState.putBoolean(STATE_DIALOG, true);
        }
    }
}
