package me.zhenning;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import me.chayut.santaslittlehelper.R;

/**
 * Created by jiang on 2016/05/02.
 */
public class AccountSelectActivity extends AccountAuthenticatorActivity {

    private static final String STATE_DIALOG = "state_dialog";

    private String TAG = this.getClass().getSimpleName();
    private AccountManager mAccountManager;
    private AlertDialog mAlertDialog;
    private Account mAccountSelected;
    private boolean mSelected;
    TextView AccountSelectZone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_select_account);
        Button btnGetAccountList = (Button) findViewById(R.id.btnGetAccountList);
        Button btnSelectComplete = (Button) findViewById(R.id.btnSelectComplete);
        Button btnAddNewAccount = (Button) findViewById(R.id.btnAddNewAccount);
        mSelected = false;
        AccountSelectZone = (TextView) findViewById(R.id.account_select_zone);
        mAccountManager = AccountManager.get(this);

        btnGetAccountList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAccountPicker();
            }
        });

        btnSelectComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextActivity();
            }
        });

        btnAddNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performAddAccount();
            }
        });
    }

    private void performAddAccount(){
        Intent intent = new Intent(this, AuthenticatorActivity.class);
        startActivity(intent);
    }

    private void nextActivity() {
        if (!mSelected) {
            Toast.makeText(this, "Not yet selected!", Toast.LENGTH_SHORT).show();
        }
        else{
            /*

            Intent to another activity

            */
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            outState.putBoolean(STATE_DIALOG, true);
        }
    }

    private void showAccountPicker() {
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
                    AccountSelectZone.setText(mAccountSelected.name);
                    mSelected = true;
                }
            }).create();
            mAlertDialog.show();
        }
    }

    public Account getSelectedAccount() {
        if (mSelected)
            return mAccountSelected;
        else
            return null;
    }

}
