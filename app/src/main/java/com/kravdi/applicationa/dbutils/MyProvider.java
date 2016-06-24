package com.kravdi.applicationa.dbutils;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.kravdi.applicationa.fragments.HistorySectionFragment;
import com.kravdi.applicationa.models.Links;

public class MyProvider extends ContentProvider {

    private DataBaseHelper dataBaseHelper;
    private SQLiteDatabase db;

    private static final String AUTHORITY = "com.kravdi.applicationa.provider";
    private static final String LINKS_PATH = "links";

    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

    public static final Uri LINKS_CONTENT_URI = Uri.withAppendedPath(BASE_URI, LINKS_PATH);

    static final String LINKS_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
            + AUTHORITY + "." + LINKS_PATH;

    static final String LINKS_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."
            + AUTHORITY + "." + LINKS_PATH;


    static final int URI_LINKS = 1;
    static final int URI_LINKS_ID = 2;

    private static final UriMatcher uriMatcher;
   // public static MyContentObserver contentObserver;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, LINKS_PATH, URI_LINKS);
        uriMatcher.addURI(AUTHORITY, LINKS_PATH + "/#", URI_LINKS_ID);
    }

    @Override
    public boolean onCreate() {
        dataBaseHelper = DataBaseHelper.getInstance(getContext());
//        contentObserver = new MyContentObserver(new Handler());
//        getContext().getContentResolver().registerContentObserver(LINKS_CONTENT_URI, true, contentObserver);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        switch (uriMatcher.match(uri)) {
            case URI_LINKS: // общий Uri

                break;
            case URI_LINKS_ID: // Uri с ID
                String id = uri.getLastPathSegment();

                if (TextUtils.isEmpty(selection)) {
                    selection = DataBaseHelper.ID_COLUMN + " = " + id;
                } else {
                    selection = selection + " AND " + DataBaseHelper.ID_COLUMN + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = dataBaseHelper.getWritableDatabase();
        Cursor cursor = db.query(DataBaseHelper.LINKS_TABLE, projection, selection,
                selectionArgs, null, null, sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(),
                LINKS_CONTENT_URI);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        switch (uriMatcher.match(uri)) {
            case URI_LINKS:
                return LINKS_CONTENT_TYPE;
            case URI_LINKS_ID:
                return LINKS_CONTENT_ITEM_TYPE;
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        if (uriMatcher.match(uri) != URI_LINKS)
            throw new IllegalArgumentException("Wrong URI: " + uri);

        db = dataBaseHelper.getWritableDatabase();
        long rowID = db.insert(DataBaseHelper.LINKS_TABLE, null, values);
        Uri resultUri = ContentUris.withAppendedId(LINKS_CONTENT_URI, rowID);
        getContext().sendBroadcast(new Intent(HistorySectionFragment.ACTION_DATABASE_CHANGED));
        getContext().getContentResolver().notifyChange(resultUri, null);
        return resultUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        switch (uriMatcher.match(uri)) {
            case URI_LINKS:
                Log.d("TAG", "URI_CONTACTS");
                break;
            case URI_LINKS_ID:
                String id = uri.getLastPathSegment();
                Log.d("TAG", "URI_CONTACTS_ID, " + id);
                if (TextUtils.isEmpty(selection)) {
                    selection = DataBaseHelper.ID_COLUMN + " = " + id;
                } else {
                    selection = selection + " AND " + DataBaseHelper.ID_COLUMN + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = dataBaseHelper.getWritableDatabase();
        int cnt = db.delete(DataBaseHelper.LINKS_TABLE, selection, selectionArgs);
        getContext().sendBroadcast(new Intent(HistorySectionFragment.ACTION_DATABASE_CHANGED));
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        switch (uriMatcher.match(uri)) {
            case URI_LINKS:
                Log.d("TAG", "URI_CONTACTS");

                break;
            case URI_LINKS_ID:
                String id = uri.getLastPathSegment();

                if (TextUtils.isEmpty(selection)) {
                    selection = DataBaseHelper.ID_COLUMN + " = " + id;
                } else {
                    selection = selection + " AND " + DataBaseHelper.ID_COLUMN + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = dataBaseHelper.getWritableDatabase();
        int cnt = db.update(DataBaseHelper.LINKS_TABLE, values, selection, selectionArgs);
        getContext().sendBroadcast(new Intent(HistorySectionFragment.ACTION_DATABASE_CHANGED));
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    private class MyContentObserver extends ContentObserver {
        public MyContentObserver(Handler h) {
            super(h);
        }

        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }

        @Override
        public void onChange(boolean selfChange) {
            Log.d("TAG", "MyContentObserver.onChange(" + selfChange + ")");
            super.onChange(selfChange);
            HistorySectionFragment.linksList.clear();

            Cursor cursor = db.query(DataBaseHelper.LINKS_TABLE, null, null,
                    null, null, null, null);

            if (cursor.moveToFirst()) {
                do {
                    Links link = HistorySectionFragment.cursorToLink(cursor);
                    HistorySectionFragment.linksList.add(link);
                } while (cursor.moveToNext());
            }
            cursor.close();

            HistorySectionFragment.adapter.notifyDataSetChanged();
        }
    }
}
