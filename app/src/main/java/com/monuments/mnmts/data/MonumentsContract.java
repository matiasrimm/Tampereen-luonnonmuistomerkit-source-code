package com.monuments.mnmts.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class MonumentsContract {

    public static final String CONTENT_AUTHORITY = "com.monuments.mnmts";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MONUMENTS = "monuments";

    public static final class MonumentsEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MONUMENTS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MONUMENTS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MONUMENTS;

        // Table name
        public static final String TABLE_NAME = "monuments";

        // Columns
        public static final String COLUMN_NIMI = "NIMI";
        public static final String COLUMN_EKAKOORDINAATTI = "EKAKOORDINAATTI";
        public static final String COLUMN_TOKAKOORDINAATTI = "TOKAKOORDINAATTI";
        public static final String COLUMN_LISATIEDOT1 = "LISATIEDOT1";
        public static final String COLUMN_LISATIEDOT2 = "LISATIEDOT2";
        public static final String COLUMN_KOHTEENKUVAUS1 = "KOHTEENKUVAUS1";
        public static final String COLUMN_KOHTEENKUVAUS2 = "KOHTEENKUVAUS2";
        public static final String COLUMN_PAATOSNUMERO = "PAATOSNUMERO";
        public static final String COLUMN_PAATOSPAIVA = "PAATOSPAIVA";

        public static Uri buildMonumentsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static Uri buildMonumentsWithName(String name) {
            return CONTENT_URI.buildUpon().appendPath(name).build();
        }
        public static String getMonumentsFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }
}