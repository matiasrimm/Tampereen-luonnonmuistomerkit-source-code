package com.monuments.mnmts.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MonumentsSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static MonumentsSyncAdapter sMonumentsSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("MonumentsSyncService", "Muistomerkit luotu");
        synchronized (sSyncAdapterLock) {
            if (sMonumentsSyncAdapter == null) {
                sMonumentsSyncAdapter = new MonumentsSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sMonumentsSyncAdapter.getSyncAdapterBinder();
    }
}