package com.example.intro.model.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.support.v4.util.LongSparseArray;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DbHelper extends SQLiteOpenHelper {
    private String TAG = "INTROVERT:" + getClass().getSimpleName();

    private SQLiteDatabase db;
    final String ID_COLUMN = BaseColumns._ID;

    /*DATABASE*/
    private static final String DATABASE_NAME = "introvert";
    private static final int DATABASE_VERSION = 1;

    /*EVENTS TABLE*/
    public final String EVENTS_TABLE = "Events";
    public final String EVENTS_NAME_COLUMN = "Name";
    final String EVENTS_TAGS_COLUMN = "Tags";
    final String EVENTS_TIME_CREATED_COLUMN = "TimeCreated";
    final String EVENTS_TIME_EDITED_COLUMN = "TimeEdited";
    final String EVENTS_TIME_COMPLETE_COLUMN = "TimeComplete";
    final String EVENTS_COMPLETE_COLUMN = "Complete";
    final String EVENTS_COMMENT_COLUMN = "Comment";
    final String EVENTS_PRIORITY_COLUMN = "Priority";
    final String EVENTS_ICON_COLUMN = "Icon";
    final String EVENTS_CONTENT_TYPE_COLUMN = "ContentType";
    final String EVENTS_CONTENT_COLUMN = "Content";

    /*TAGS TABLE*/
    public final String TAGS_TABLE = "Tags";
    public final String TAGS_TAG_COLUMN = "Tag";
    public final String TAGS_EVENTS_COLUMN = "Events";


    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(EVENTS_TABLE, db);
        createTable(TAGS_TABLE, db);
        initContent(TAGS_TABLE, db);
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
                                + ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                                + EVENTS_NAME_COLUMN + " TEXT "
                                + "NOT NULL DEFAULT 0, "
                                + EVENTS_TAGS_COLUMN + " TEXT, "
                                + EVENTS_TIME_CREATED_COLUMN + " INTEGER "
                                + "NOT NULL DEFAULT 0, "
                                + EVENTS_TIME_EDITED_COLUMN + " INTEGER "
                                + "NOT NULL DEFAULT 0, "
                                + EVENTS_TIME_COMPLETE_COLUMN + " INTEGER "
                                + "NOT NULL DEFAULT 0, "
                                + EVENTS_COMPLETE_COLUMN + " INTEGER "
                                + "NOT NULL DEFAULT 0, "
                                + EVENTS_COMMENT_COLUMN + " TEXT, "
                                + EVENTS_PRIORITY_COLUMN + " INTEGER "
                                + "NOT NULL DEFAULT 0, "
                                + EVENTS_ICON_COLUMN + " INTEGER "
                                + "NOT NULL DEFAULT 0, "
                                + EVENTS_CONTENT_TYPE_COLUMN + " INTEGER "
                                + "NOT NULL DEFAULT 0, "
                                + EVENTS_CONTENT_COLUMN + " TEXT);";
                break;
            case TAGS_TABLE:
                SQL_CREATE_TABLE =
                        "CREATE TABLE " + TAGS_TABLE + " ("
                                + ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                                + TAGS_TAG_COLUMN + " TEXT UNIQUE "
                                + "NOT NULL, "
                                + TAGS_EVENTS_COLUMN + " TEXT "
                                + "NOT NULL DEFAULT " + "\"\"" + ");";
                break;
        }

        return SQL_CREATE_TABLE;
    }


    /* ~~~~~~~~~~~~~~~ INITIATION ~~~~~~~~~~~~~~~*/
    private void initContent(String table, SQLiteDatabase db) {
        insertRows(table, getInitValues(table), db);
    }


    private List<ContentValues> getInitValues(String table) {
        List<ContentValues> values = new ArrayList<>();

        switch (table) {
            case TAGS_TABLE:
                String[] tags = {"Diary", "Weight", "Joke", "Thought",
                        "Dream", "Idea", "Track", "Mix", "Spending",
                        "ASaT/ALaT"};
                for (String tag : tags) {
                    ContentValues v = new ContentValues();
                    v.put(TAGS_TAG_COLUMN, tag);
                    values.add(v);
                }
        }
        return values;
    }


    /* ~~~~~~~~~~~~~~~ CHECKING ~~~~~~~~~~~~~~~*/
    private boolean isReady() {
        if (db == null) db = getWritableDatabase();
        return true;
    }


    long nextId(String table) {
        long nextId = -1;
        if (!isReady()) return nextId;

        String[] columns = new String[]{ID_COLUMN};
        try (Cursor c = db.query(table, columns, null, null,
                null, null, null)) {
            // TODO: 021 21 фев 19 next id if last was deleted is not 1!
            if (c.getCount() == 0) return 1;
            c.moveToLast();
            nextId = Long.parseLong(c.getString(c.getColumnIndex(columns[0])));
        }

        return nextId + 1;
    }


    /* ~~~~~~~~~~~~~~~ INSERT ~~~~~~~~~~~~~~~*/
    long insertRow(String table, ContentValues values,
                   SQLiteDatabase db) {
        List<ContentValues> contentValues = new ArrayList<>();
        contentValues.add(values);
        return insertRows(table, contentValues, db)[0];
    }


    long[] insertRows(String table, List<ContentValues> values,
                      SQLiteDatabase db) {
        if (db == null) db = getWritableDatabase();

        long[] rows = new long[values.size()];
        db.beginTransaction();
        try {
            for (int i = 0; i < rows.length; i++) {
                db.insert(table, null, values.get(i));
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        return rows;
    }


    /* ~~~~~~~~~~~~~~~ READ ~~~~~~~~~~~~~~~*/
    /* ~~~~~~~~~~ Columns ~~~~~~~~~~*/
    public String[] getColumnNames(String table) {
        if (!isReady()) return null;

        String[] columns;
        try (Cursor c = db.query(table, null, null,
                null, null, null, null)) {
            columns = c.getColumnNames();
        }

        return columns;
    }


    String[] getColumn(String table, String column) {
        return getFilteredColumn(table, column, null,
                null);
    }


    String[] getFilteredColumn(String table, String column,
                               String where, String whereArg) {
        if (!isReady()) return null;

        // Precautions for null where and whereArg
        String[] whereArgum = null;
        if (where != null) {
            where = where + "=?";
            whereArgum = new String[]{whereArg};
        }

        String[] array = null;
        try (Cursor c = db.query(
                table, // Table to query
                new String[]{column}, // Array of columns
                where, // Columns for WHERE clause
                whereArgum, // Values for WHERE clause
                null, // Don't group rows
                null, // Don't filter by row groups
                null)) { // Sort order

            int count = c.getCount();
            if (count > 0) {
                array = new String[count];
                c.moveToFirst();
                for (int i = 0; i < count; i++) {
                    array[i] = c.getString(c.getColumnIndex(column));
                    c.moveToNext();
                }
            }
        }

        return array;
    }


    /* ~~~~~~~~~~ Cells ~~~~~~~~~~*/
    String getCellValue(String table, String column, long id) {
        return getFilteredColumn(table, column, ID_COLUMN,
                Long.toString(id))[0];
    }


    /* ~~~~~~~~~~ Rows ~~~~~~~~~~*/
    public String[] getRow(String table, long row) {
        return getFilteredRow(table, row, null);
    }


    private String[] getFilteredRow(String table, long row,
                                    String[] columns) {
        if (!isReady()) return null;

        // Create array for values and columns (if needed)
        if (columns == null) columns = getColumnNames(table);
        String[] values = new String[columns.length];

        try (Cursor c = db.query(table, columns,
                ID_COLUMN + "=?", new String[]{Long.toString(row)},
                null, null, null)) {
            if (c.getCount() == 1) {
                c.moveToFirst();
                for (int i = 0; i < values.length; i++) {
                    values[i] = c.getString(c.getColumnIndex(columns[i]));
                }
            }
        }

        return values;
    }


    public Map<String, String> getMappedRow(String table, long row) {
        return getMappedFilteredRow(table, row, null);
    }


    private Map<String, String> getMappedFilteredRow(
            String table, long row, String[] columns) {
        Map<String, String> map = new HashMap<>();

        if (columns == null) columns = getColumnNames(table);

        String[] values = getFilteredRow(table, row, columns);

        for (int i = 0; i < values.length; i++) {
            map.put(columns[i], values[i]);
        }

        return map;
    }


    /* ~~~~~~~~~~~~~~~ UPDATE ~~~~~~~~~~~~~~~*/
    boolean updateRow(String table, ContentValues values, long row) {
        LongSparseArray<ContentValues> array = new LongSparseArray<>();
        array.put(row, values);
        return updateRows(table, array);
    }


    private boolean updateRows(String table,
                               LongSparseArray<ContentValues> updates) {
        if (!isReady()) return false;

        long key;
        ContentValues cv;
        int rowsAmt = 0;

        db.beginTransaction();
        try {
            for (int i = 0; i < updates.size(); i++) {
                key = updates.keyAt(i);
                cv = updates.get(key);
                String[] args = new String[]{Long.toString(key)};
                Log.i(TAG, args[0]);
                rowsAmt = db.update(table, cv, ID_COLUMN + "=?",
                        new String[]{Long.toString(key)});
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        return rowsAmt != 0;
    }


    /* ~~~~~~~~~~~~~~~ DELETE ~~~~~~~~~~~~~~~*/
    public boolean deleteRow(String table, long row) {
        return deleteRows(table, new long[]{row});
    }


    private boolean deleteRows(String table, long[] rows) {
        if (!isReady()) return false;

        db.beginTransaction();
        try {
            for (long row : rows) {
                db.delete(table, ID_COLUMN + "=?",
                        new String[]{Long.toString(row)});
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        return true;
    }


    private boolean deleteTable(String table) {
        if (!isReady()) return false;

        String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + table;
        db.execSQL(SQL_DELETE_TABLE);

        return true;
    }


    /* ~~~~~~~~~~~~~~~ CURSOR ~~~~~~~~~~~~~~~*/
    public Cursor createNotesCursor() {
        if (!isReady()) return null;

        String[] projection = {ID_COLUMN, EVENTS_NAME_COLUMN};

        return db.query(
                EVENTS_TABLE,   // Table to query
                projection,     // Array of columns to return
                null,   // Columns for WHERE clause
                null,// Values for WHERE clause
                null,    // Don't group rows
                null,     // Don't filter by row groups
                null     // Sort order
        );
    }


    /* ~~~~~~~~~~~~~~~ DEBUGGING ~~~~~~~~~~~~~~~*/
    public void dumpTable(String table) {
        if (!isReady()) return;

        Cursor c = db.query(table, null, null,
                null, null, null, null);

        String[] columns = c.getColumnNames();

        Log.i(TAG, "===================================================");
        Log.i(TAG, table + " eventsTable");
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