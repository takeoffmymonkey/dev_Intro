package com.example.intro.model.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.util.LongSparseArray;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Table {
    private String TAG = "INTROVERT:" + getClass().getSimpleName();


    /* ~~~~~ CONSTRUCTORS ~~~~~ */
    Table(String name, Column[] columns) {
        mName = name;
        mColumns = columns;
    }


    /* ~~~~~ TABLES ~~~~~ */
    private static final Table RECORDS = new Table("Records",
            RecordsColumn.getAllArray());
    private static final Table TAGS = new Table("Tags",
            TagsColumn.getAllArray());

    private static final List<Table> all = new ArrayList<>();

    static {
        all.add(RECORDS);
        all.add(TAGS);
    }

    static List<Table> getAllList() {
        return all;
    }

    static Table[] getAllArray() {
        return all.toArray(new Table[0]);
    }

    static boolean addTable(Table t) {
        return all.add(t);
    }


    /* ~~~~~ NAME ~~~~~ */
    private String mName;

    String getName() {
        return mName;
    }


    /* ~~~~~ FIELDS ~~~~~ */
    private Column[] mColumns;

    Column[] getColumns() {
        return mColumns;
    }


    /* ~~~~~ FILE ~~~~~ */
    private File mFile;

    File getFile(Context c) {
        if (mFile == null) mFile = new File(Db.getMainFolder(c), mName);
        return mFile;
    }


    /* ~~~~~~~~~~~~~~~ CREATE ~~~~~~~~~~~~~~~*/
    boolean create(Context c) {
        // Create table
        Db.getDb(c).execSQL(createTableCommand());

        // Create file and add header
        return addFileHeader();
    }

    boolean create(SQLiteDatabase db) {
        // Create table
        db.execSQL(createTableCommand());

        // Create file and add header
        return addFileHeader();
    }

    private String createTableCommand() {
        Column f;
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ");
        sb.append(mName);
        sb.append(" (");
        for (int i = 0; i < mColumns.length; i++) {
            f = mColumns[i];
            // Column name
            sb.append(f.getName());
            if (f.isPrimaryKey()) sb.append(" PRIMARY KEY");
            if (f.isUnique()) sb.append(" UNIQUE");
            if (f.isAutoIncrement()) sb.append(" AUTOINCREMENT");
            if (f.isNotNull()) sb.append(" NOT NULL");
            if (f.isDefault()) {
                sb.append(" DEFAULT ");
                sb.append(f.getDefaultVal());
            }
            if (i != mColumns.length) sb.append(",");
        }
        sb.append(");");

        return sb.toString();
    }

    /**
     * Method will return Cursor for this table with specified parameters.
     * You can pass null to specify all.
     * If any parameter (field) of the list doesn't exist, it is skipped.
     * If list consists only of missing parameter, null is used.
     * Attention: there may be several existing cursors simultaneously,
     * so you need to close them once done.
     */
    Cursor createCursor(Context c, Column[] columns, Column where,
                        String[] args, String groupBy,
                        String having, String orderBy) {

        // Prepare columns String [] for query
        // If fields != null, check if this table contains such fields
        String[] colsString = null;
        if (columns != null) {
            List<Column> columnList = new ArrayList<>();
            for (Column column : columns) {
                if (column.getIdxInTable(c, this) != -1)
                    columnList.add(column);
            }

            // Convert list to String array for query (or make null if 0-length)
            int size = columnList.size();
            colsString = new String[size];
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    colsString[i] = columns[i].getName();
                }
            } else colsString = null;
        }

        // Prepare selection String for query
        // If where != null, check if this table contains it
        String selection = null;
        if (colsString != null && where != null
                && where.getIdxInTable(c, this) != -1) {
            selection = where.getName();

            // Check if there are arguments and make adjustments accordingly
            if (args != null) selection += "=?";
        }


        return Db.getDb(c).query(
                getName(),   // Table to query
                colsString,     // Array of columns to return
                selection,   // Columns for WHERE clause
                args,        // Values for WHERE clause
                groupBy,     // Don't group rows
                having,      // Don't filter by row groups
                orderBy      // Sort order
        );
    }


    /* ~~~~~~~~~~~~~~~ DELETE ~~~~~~~~~~~~~~~*/
    boolean delete(Context c) {
        // Remove table
        Db.getDb(c).execSQL("DROP TABLE IF EXISTS " + mName);

        // Remove file
        return new File(Db.getMainFolder(c), getName()).delete();
    }

    boolean deleteRow(Context c, long rowId) {
        return deleteRows(c, new long[]{rowId})[0];
    }

    boolean[] deleteRows(Context c, long[] rowsIds) {
        boolean results[] = new boolean[rowsIds.length];
        SQLiteDatabase db = Db.getDb(c);

        db.beginTransaction();
        try {
            for (int i = 0; i < rowsIds.length; i++) {
                int res = db.delete(getName(), Column.ID
                                .getName() + "=?",
                        new String[]{Long.toString(rowsIds[i])});
                if (res == 1) results[i] = true;
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        return results;
    }


    /* ~~~~~~~~~~~~~~~ WRITE ~~~~~~~~~~~~~~~*/
    private boolean addFileHeader() {
        // TODO: 002 02 мар 19 replace with better text & maybe use context
        String temp = createTableCommand();
        try (FileOutputStream out = new FileOutputStream(mFile)) {
            out.write(temp.getBytes());
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    long insertRow(Context c, ContentValues values) {
        List<ContentValues> contentValues = new ArrayList<>();
        contentValues.add(values);
        return insertRows(c, contentValues)[0];
    }

    long[] insertRows(Context c, List<ContentValues> values) {
        SQLiteDatabase db = Db.getDb(c);

        long[] rows = new long[values.size()];
        db.beginTransaction();
        try {
            for (int i = 0; i < rows.length; i++) {
                db.insert(getName(), null, values.get(i));
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        return rows;
    }


    /* ~~~~~~~~~~~~~~~ READ ~~~~~~~~~~~~~~~*/
    public List<Value> getRow(Context c, long row) {
        return getFilteredRow(c, row, null);
    }

    private List<Value> getFilteredRow(Context c, long row, Column[] columns) {
        // Create array for values and columns (if needed)
        if (columns == null) columns = getColumns();

        List<Value> values = new ArrayList<>();
        try (Cursor cursor = createCursor(c, columns, Column.ID,
                new String[]{Long.toString(row)},
                null, null, null)) {
            if (cursor != null && cursor.getCount() == 1) {
                cursor.moveToFirst();
                for (Column column : columns) {
                    String val = cursor.getString(cursor.getColumnIndex
                            (column.getName()));
                    Value v = new Value(column.getType(), val, row, column);
                    values.add(v);
                }
            }
        }

        return values;
    }

    public Map<Column, Value> getMappedRow(Context c, long row) {
        return getMappedFilteredRow(c, row, null);
    }

    private Map<Column, Value> getMappedFilteredRow(
            Context c, long row, Column[] columns) {
        Map<Column, Value> map = new HashMap<>();

        if (columns == null) columns = getColumns();

        List<Value> values = getFilteredRow(c, row, columns);

        for (int i = 0; i < values.size(); i++) {
            map.put(columns[i], values.get(i));
        }

        return map;
    }


    /* ~~~~~~~~~~~~~~~ UPDATE ~~~~~~~~~~~~~~~*/
    boolean updateRow(Context c, ContentValues values, long row) {
        LongSparseArray<ContentValues> array = new LongSparseArray<>();
        array.put(row, values);
        return updateRows(c, array);
    }


    private boolean updateRows(Context c, LongSparseArray<ContentValues> updates) {
        SQLiteDatabase db = Db.getDb(c);

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
                rowsAmt = db.update(getName(), cv, Column.ID + "=?",
                        new String[]{Long.toString(key)});
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        return rowsAmt != 0;
    }


    /* ~~~~~ CONVENIENCE ~~~~~ */
    boolean isEmpty(Context c) {
        boolean empty = true;
        try (Cursor cursor = Db.getDb(c).rawQuery
                ("SELECT COUNT(*) FROM " + mName, null)) {
            if (cursor != null && cursor.moveToFirst())
                empty = cursor.getInt(0) == 0;
        }
        return empty;
    }

    boolean exists(Context c) {
        // Check table existence
        boolean tableExists = false;
        Cursor cursor = Db.getDb(c).rawQuery
                ("select DISTINCT tbl_name from sqlite_master where" +
                        " tbl_name = '" + mName + "'", null);
        if (cursor != null) {
            // TODO: 002 02 мар 19 Does empty table return 0-length cursor
            if (cursor.getCount() > 0) tableExists = true;
            cursor.close();
        }

        // Check file existence
        boolean fileExists = new File(Db.getMainFolder(c), mName).exists();

        return tableExists && fileExists;
    }

    public void dumpTable(Context c) {
        try (Cursor cursor = createCursor(c, null, null, null,
                null, null, null)) {
            String[] columns = cursor.getColumnNames();

            Log.i(TAG, "===================================================");
            Log.i(TAG, "Table: " + getName());
            Log.i(TAG, "Number of rows: " + cursor.getCount());
            Log.i(TAG, "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) { // Moving through rows
                for (String column : columns) { // Moving through columns
                    Log.i(TAG, column + ": " + cursor.getString(cursor.getColumnIndex
                            (column)));
                }
                Log.i(TAG, "------------------------------");
                cursor.moveToNext();
            }

            Log.i(TAG, "===================================================");
        }
    }

    long nextId(Context c) {
        long nextId = -1;

        String[] columns = new String[]{Column.ID.getName()};
        try (Cursor cursor = createCursor(c, new Column[]{Column.ID}, null,
                null, null, null, null)) {
            // TODO: 021 21 фев 19 next id if last was deleted is not 1!
            if (cursor.getCount() == 0) return 1;
            cursor.moveToLast();
            nextId = Long.parseLong(c.getString(cursor.getColumnIndex(columns[0])));
        }

        return nextId + 1;
    }
}