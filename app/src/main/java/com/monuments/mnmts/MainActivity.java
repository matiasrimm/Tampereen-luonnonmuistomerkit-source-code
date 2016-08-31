package com.monuments.mnmts;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.monuments.mnmts.sync.MonumentsSyncAdapter;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MonumentsFragment())
                    .commit();
        }
        // for syncing
        MonumentsSyncAdapter.initializeSyncAdapter(this);
    }
}