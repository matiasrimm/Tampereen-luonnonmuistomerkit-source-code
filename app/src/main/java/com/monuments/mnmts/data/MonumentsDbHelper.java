package com.monuments.mnmts.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.monuments.mnmts.data.MonumentsContract.MonumentsEntry;

public class MonumentsDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "monuments.db";

    public MonumentsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MONUMENTS_TABLE = "CREATE TABLE " + MonumentsEntry.TABLE_NAME + " (" +
                MonumentsEntry._ID + " INTEGER PRIMARY KEY," +
                MonumentsEntry.COLUMN_NIMI + " TEXT NOT NULL, " +
                MonumentsEntry.COLUMN_EKAKOORDINAATTI + " TEXT, " +
                MonumentsEntry.COLUMN_TOKAKOORDINAATTI + " TEXT, " +
                MonumentsEntry.COLUMN_LISATIEDOT1 + " TEXT, " +
                MonumentsEntry.COLUMN_LISATIEDOT2 + " TEXT, " +
                MonumentsEntry.COLUMN_KOHTEENKUVAUS1 + " TEXT, " +
                MonumentsEntry.COLUMN_KOHTEENKUVAUS2 + " TEXT, " +
                MonumentsEntry.COLUMN_PAATOSNUMERO + " TEXT, " +
                MonumentsEntry.COLUMN_PAATOSPAIVA + " TEXT " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_MONUMENTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
         sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MonumentsEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}