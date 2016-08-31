package com.monuments.mnmts.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class MonumentsProvider extends ContentProvider {

    // The URIMatcher used by content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MonumentsDbHelper mOpenHelper;

    static final int MONUMENTS = 100;
    static final int MONUMENTS_WITH_NAME = 101;

    private static final SQLiteQueryBuilder queryBuilder;

    static{
        queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(MonumentsContract.MonumentsEntry.TABLE_NAME);
    }

    // find column
    private static final String sLocationSettingSelection =
        MonumentsContract.MonumentsEntry.TABLE_NAME +
            "." + MonumentsContract.MonumentsEntry.COLUMN_NIMI + " = ? ";

    private Cursor getMonumentsCursor(Uri uri, String[] projection) {

        String nameSettingFromUri = MonumentsContract.MonumentsEntry.getMonumentsFromUri(uri);

        String[] selectionArgs;
        String selection;

        selection = sLocationSettingSelection;
        selectionArgs = new String[]{nameSettingFromUri};

        return queryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
    }

    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MonumentsContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MonumentsContract.PATH_MONUMENTS, MONUMENTS);
        matcher.addURI(authority, MonumentsContract.PATH_MONUMENTS + "/*", MONUMENTS_WITH_NAME);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MonumentsDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MONUMENTS_WITH_NAME:
                return MonumentsContract.MonumentsEntry.CONTENT_ITEM_TYPE;
            case MONUMENTS:
                return MonumentsContract.MonumentsEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        Cursor retCursor;

        switch (sUriMatcher.match(uri)) {

            case MONUMENTS_WITH_NAME: {
                retCursor = getMonumentsCursor(uri, projection);
                break;
            }
            case MONUMENTS: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MonumentsContract.MonumentsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MONUMENTS: {
                long _id = db.insert(MonumentsContract.MonumentsEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MonumentsContract.MonumentsEntry.buildMonumentsUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case MONUMENTS:
                rowsDeleted = db.delete(
                        MonumentsContract.MonumentsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MONUMENTS:
                rowsUpdated = db.update(MonumentsContract.MonumentsEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MONUMENTS:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MonumentsContract.MonumentsEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}