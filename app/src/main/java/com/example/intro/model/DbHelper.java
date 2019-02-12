package com.example.intro.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {
    private String TAG = "INTROVERT:" + getClass().getSimpleName();

    private SQLiteDatabase db;
    private final String ID_COLUMN = BaseColumns._ID;

    /*DATABASE*/
    private static final String DATABASE_NAME = "introvert";
    private static final int DATABASE_VERSION = 1;

    /*EVENTS TABLE*/
    public final String EVENTS_TABLE = "Events";
    public final String EVENTS_NAME_COLUMN = "Name";
    final String EVENTS_TAGS_COLUMN = "Tags";
    final String EVENTS_DATE_CREATED_COLUMN = "DateCreated";
    final String EVENTS_DATE_COMPLETE_COLUMN = "DateComplete";
    final String EVENTS_COMPLETE_COLUMN = "Complete";
    final String EVENTS_COMMENT_COLUMN = "Comment";
    final String EVENTS_PRIORITY_COLUMN = "Priority";
    final String EVENTS_ICON_COLUMN = "Icon";
    final String EVENTS_CONTENT_TYPE_COLUMN = "ContentType";
    final String EVENTS_CONTENT_COLUMN = "Content";


    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(EVENTS_TABLE, db);
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }


    /* ~~~~~~~~~~~~~~~~~~~~~ UTILITY METHODS ~~~~~~~~~~~~~~~~~~~~~ */
    /* ~~~~~~~~~~~~~~~ CREATION ~~~~~~~~~~~~~~~*/
    private void createTable(String table, SQLiteDatabase db) {
        String SQL_CREATE_TABLE = createTableCommand(table);
        db.execSQL(SQL_CREATE_TABLE);
    }


    private String createTableCommand(String table) {
        String SQL_CREATE_TABLE = null;

        switch (table) {
            case EVENTS_TABLE:
                SQL_CREATE_TABLE =
                        "CREATE TABLE " + EVENTS_TABLE + " ("
                                + ID_COLUMN + " INTEGER PRIMARY KEY, "
                                + EVENTS_NAME_COLUMN + " TEXT, "
                                + EVENTS_TAGS_COLUMN + " TEXT, "
                                + EVENTS_DATE_CREATED_COLUMN + " INTEGER, "
                                + EVENTS_DATE_COMPLETE_COLUMN + " INTEGER, "
                                + EVENTS_COMPLETE_COLUMN + " INTEGER, "
                                + EVENTS_COMMENT_COLUMN + " TEXT, "
                                + EVENTS_PRIORITY_COLUMN + " INTEGER, "
                                + EVENTS_ICON_COLUMN + " INTEGER, "
                                + EVENTS_CONTENT_TYPE_COLUMN + " INTEGER, "
                                + EVENTS_CONTENT_COLUMN + " TEXT);";
                break;
        }

        return SQL_CREATE_TABLE;
    }


    /* ~~~~~~~~~~~~~~~ CHECKING ~~~~~~~~~~~~~~~*/
    private boolean isReady() {
        if (db == null) db = getWritableDatabase();
        return true;
    }


    /* ~~~~~~~~~~~~~~~ INSERTION ~~~~~~~~~~~~~~~*/
    boolean insertRow(String table, ContentValues values) {
        List<ContentValues> contentValues = new ArrayList<>();
        contentValues.add(values);
        return insertRows(table, contentValues);
    }


    boolean insertRows(String table, List<ContentValues> values) {
        if (!isReady()) return false;

        db.beginTransaction();
        try {
            for (ContentValues value : values) {
                db.insert(table, null, value);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        return true;
    }


    /* ~~~~~~~~~~~~~~~ CURSOR ~~~~~~~~~~~~~~~*/
    public Cursor createNotesCursor() {
        if (!isReady()) return null;

        String[] projection = {ID_COLUMN, EVENTS_NAME_COLUMN};

        return db.query(
                EVENTS_TABLE,   // The table to query
                projection,     // The array of columns to return (pass null to get all)
                null,   // The columns for the WHERE clause
                null,// The values for the WHERE clause
                null,    // don't group the rows
                null,     // don't filter by row groups
                null     // The sort order
        );
    }


    /* ~~~~~~~~~~~~~~~ DEBUGGING ~~~~~~~~~~~~~~~*/
    public void dumpTable(String table) {
        if (!isReady()) return;

        Cursor c = db.query(table, null, null,
                null, null, null, null);

        String[] columns = c.getColumnNames();

        Log.i(TAG, "===================================================");
        Log.i(TAG, table + " table");
        Log.i(TAG, "Number of rows: " + c.getCount());
        Log.i(TAG, "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        c.moveToFirst();
        for (int i = 0; i < c.getCount(); i++) { // Moving through rows
            for (String column : columns) { // Moving through columns
                Log.i(TAG, column + ": " + c.getString(c.getColumnIndex
                        (column)));
            }
            Log.i(TAG, "------------------------------");
            c.moveToNext();
        }

        Log.i(TAG, "===================================================");

        c.close();
    }
}