package com.monuments.mnmts.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MonumentsAuthenticatorService extends Service {

    private MonumentsAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new MonumentsAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
