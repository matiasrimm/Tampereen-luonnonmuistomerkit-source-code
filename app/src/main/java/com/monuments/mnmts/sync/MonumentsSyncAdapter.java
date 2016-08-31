package com.monuments.mnmts.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;
import com.monuments.mnmts.R;
import com.monuments.mnmts.data.MonumentsContract;

/**
 * Alla olevaa kopioitu täältä
 * http://developer.android.com/training/sync-adapters/creating-authenticator.html
 */

public class MonumentsSyncAdapter extends AbstractThreadedSyncAdapter {

    public final String LOG_TAG = MonumentsSyncAdapter.class.getSimpleName();

    // sync interval 60 seconds (1 minute) * 60 * 24 = 24 hours
    public static final int SYNC_INTERVAL = 60*60*24;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

    public MonumentsSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String jsonStr = null;


        try {
            final String LUONNONMUISTOMERKIT_BASE_URL =
                    "http://opendatci.com/tampere/opendata/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=opendata:YV_LUONNONMUISTOMERKKI&outputFormat=json&srsName=EPSG:4326";

            Uri builtUri = Uri.parse(LUONNONMUISTOMERKIT_BASE_URL).buildUpon().build();
            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // nothing to do
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }
            jsonStr = buffer.toString();
            getDataFromJson(jsonStr);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return;
    }

    // parse
    private void getDataFromJson(String forecastJsonStr) throws JSONException {

        final String MON_FEATURES = "features";
        final String MON_PROPERTIES = "properties";
        final String MON_GEOMETRY = "geometry";
        final String MON_COORDINATES = "coordinates";
        final String MON_LISATIEDOT1 = "LISATIEDOT1";
        final String MON_LISATIEDOT2 = "LISATIEDOT2";
        final String MON_KOHTEENKUVAUS1 = "KOHTEENKUVAUS1";
        final String MON_KOHTEENKUVAUS2 = "KOHTEENKUVAUS2";
        final String MON_NIMI = "NIMI";
        final String MON_PAATOSNUMERO = "PAATOSNUMERO";
        final String MON_PAATOSPAIVA = "PAATOSPAIVA";

        try {
            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray monumentsArray = forecastJson.getJSONArray(MON_FEATURES);

            Vector<ContentValues> cVVector = new Vector<ContentValues>(monumentsArray.length());

            for(int i = 0; i < monumentsArray.length(); i++) {
                String KOORDINAATTI1;
                String KOORDINAATTI2;
                String LISATIEDOT1;
                String LISATIEDOT2;
                String KOHTEENKUVAUS1;
                String KOHTEENKUVAUS2;
                String NIMI;
                String PAATOSNUMERO;
                String PAATOSPAIVA;

                // whole json
                JSONObject feature = monumentsArray.getJSONObject(i);

                // geometry
                JSONObject geometry = feature.getJSONObject(MON_GEOMETRY);
                JSONArray coordinates = geometry.getJSONArray(MON_COORDINATES);
                KOORDINAATTI1 = coordinates.getString(0);
                KOORDINAATTI2 = coordinates.getString(1);

                // properties
                JSONObject properties = feature.getJSONObject(MON_PROPERTIES);
                LISATIEDOT1 = properties.getString(MON_LISATIEDOT1);
                LISATIEDOT2 = properties.getString(MON_LISATIEDOT2);
                KOHTEENKUVAUS1 = properties.getString(MON_KOHTEENKUVAUS1);
                KOHTEENKUVAUS2 = properties.getString(MON_KOHTEENKUVAUS2);
                NIMI = properties.getString(MON_NIMI);
                PAATOSNUMERO = properties.getString(MON_PAATOSNUMERO);
                PAATOSPAIVA = properties.getString(MON_PAATOSPAIVA);

                ContentValues mValues = new ContentValues();

                mValues.put(MonumentsContract.MonumentsEntry.COLUMN_NIMI, NIMI);
                mValues.put(MonumentsContract.MonumentsEntry.COLUMN_EKAKOORDINAATTI, KOORDINAATTI1);
                mValues.put(MonumentsContract.MonumentsEntry.COLUMN_TOKAKOORDINAATTI, KOORDINAATTI2);
                mValues.put(MonumentsContract.MonumentsEntry.COLUMN_LISATIEDOT1, LISATIEDOT1);
                mValues.put(MonumentsContract.MonumentsEntry.COLUMN_LISATIEDOT2, LISATIEDOT2);
                mValues.put(MonumentsContract.MonumentsEntry.COLUMN_KOHTEENKUVAUS1, KOHTEENKUVAUS1);
                mValues.put(MonumentsContract.MonumentsEntry.COLUMN_KOHTEENKUVAUS2, KOHTEENKUVAUS2);
                mValues.put(MonumentsContract.MonumentsEntry.COLUMN_PAATOSNUMERO, PAATOSNUMERO);
                mValues.put(MonumentsContract.MonumentsEntry.COLUMN_PAATOSPAIVA, PAATOSPAIVA);
                cVVector.add(mValues);
            }

            // add to database
            if ( cVVector.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                getContext().getContentResolver().delete(MonumentsContract.MonumentsEntry.CONTENT_URI,null,null);
                getContext().getContentResolver().bulkInsert(MonumentsContract.MonumentsEntry.CONTENT_URI, cvArray);
            }
            Log.d(LOG_TAG, "Sync Complete. " + cVVector.size() + " Inserted");

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    public static Account getSyncAccount(Context context) {

        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        if ( null == accountManager.getPassword(newAccount) ) {
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        MonumentsSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}