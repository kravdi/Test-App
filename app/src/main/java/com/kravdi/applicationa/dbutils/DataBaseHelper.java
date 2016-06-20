package com.kravdi.applicationa.dbutils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static DataBaseHelper mInstance = null;

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Links.db";

    public static final String LINKS_TABLE = "links";

    public static final String ID_COLUMN = "_id";
    public static final String LINK_COLUMN = "link";
    public static final String STATE_COLUMN = "state";
    public static final String TIME_COLUMN = "time";

    public static final String CREATE_LINKS_TABLE =
            "create table " + LINKS_TABLE + " ( "
                    + ID_COLUMN + " integer primary key autoincrement, "
                    + LINK_COLUMN + " text, "
                    + STATE_COLUMN + " integer, "
                    + TIME_COLUMN + " integer"
                    + " );";

    public static DataBaseHelper getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new DataBaseHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("Debug Log", "onCreate DB");
        db.execSQL(CREATE_LINKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
        onCreate(db);
    }
}
