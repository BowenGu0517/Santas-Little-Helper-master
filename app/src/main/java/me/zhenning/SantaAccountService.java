package me.zhenning;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by jiang on 2016/05/02.
 */
public class  SantaAccountService extends Service {

    private static final String TAG = "SantaAuthService";

    SantaAuthenticator authenticator;

    @Override
    public void onCreate() {
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, "Authentication Service started.");
        }
        authenticator = new SantaAuthenticator(this);
    }

    @Override
    public void onDestroy() {
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, "SantaAuthentication Service stopped.");
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, "getBinder()...  returning the AccountAuthenticator binder for intent "
                    + intent);
        }

        return authenticator.getIBinder();
    }
}
