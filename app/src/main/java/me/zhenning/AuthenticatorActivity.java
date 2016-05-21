package me.zhenning;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import me.chayut.santaslittlehelper.R;

/**
 * Created by jiang on 2016/05/03.
 */
public class AuthenticatorActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    public final static String ARG_ACCOUNT_TYPE = "ARG_ACCOUNT_TYPE";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "ARG_IS_ADDING_NEW_ACCOUNT";

    private AccountManager mAccountManager;

    EditText AddEmailAccount;

    EditText AddAccountPassword;

    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "AuthenticatorActivity onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_register);

        AddAccountPassword = (EditText) findViewById(R.id.add_account_password);
        AddEmailAccount = (EditText) findViewById(R.id.add_account_email);

        mAccountManager = AccountManager.get(this);

        findViewById(R.id.Account_Create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String currentEmail = AddEmailAccount.getText().toString().trim();
                if (currentEmail.matches(emailPattern))
                    addAccountToAccountManager();
                else
                    Toast.makeText(getApplicationContext(),
                            "You need a valid email address!",
                            Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void addAccountToAccountManager(){

        String accountName = AddEmailAccount.getText().toString().trim();
        String userPassword = AddAccountPassword.getText().toString().trim();

        Account newAccount = new Account(accountName, AccountGeneral.ACCOUNT_TYPE);
        mAccountManager.addAccountExplicitly(newAccount, userPassword, null);
        mAccountManager.setAuthToken(newAccount,
                AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS,
                AccountGeneral.Santa_AuthToken);
        Toast.makeText(getApplicationContext(),
                "Account Added!",
                Toast.LENGTH_SHORT).show();
    }

}
