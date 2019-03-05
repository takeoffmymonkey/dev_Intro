package com.example.intro.model.db;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

class Column {
    private String mTAG = "INTROVERT:" + getClass().getSimpleName();


    /* ~~~~~ CONSTRUCTORS ~~~~~ */
    Column(String mName, Type mType) {
        this.mName = mName;
        this.mType = mType;
    }

    Column(String name, Type type, boolean primaryKey,
           boolean autoIncrement, boolean notNull,
           boolean dflt, String defaultVal, boolean unique) {
        this.mName = name;
        this.mType = type;
        this.mPrimaryKey = primaryKey;
        this.mAutoIncrement = autoIncrement;
        this.mNotNull = notNull;
        this.mDefault = dflt;
        this.mDefaultVal = defaultVal;
        this.mUnique = unique;
    }


    /* ~~~~~ COMMON FIELDS ~~~~~ */
    static final Column ID = new Column(BaseColumns._ID,
            Type.INTEGER, true, true, false,
            false, null, false);


    /* ~~~~~ NAME ~~~~~ */
    private String mName;

    String getName() {
        return mName;
    }


    /* ~~~~~ TYPE ~~~~~ */
    private Type mType;

    enum Type {INTEGER, TEXT}

    Type getType() {
        return mType;
    }


    /* ~~~~~ PRIMARY KEY ~~~~~ */
    private boolean mPrimaryKey;

    boolean isPrimaryKey() {
        return mPrimaryKey;
    }


    /* ~~~~~ AUTOINCREMENT ~~~~~ */
    private boolean mAutoIncrement;

    boolean isAutoIncrement() {
        return mAutoIncrement;
    }


    /* ~~~~~ NOT NULL ~~~~~ */
    private boolean mNotNull;

    boolean isNotNull() {
        return mNotNull;
    }


    /* ~~~~~ DEFAULT ~~~~~ */
    private boolean mDefault;
    private String mDefaultVal = "";

    boolean isDefault() {
        return mDefault;
    }

    String getDefaultVal() {
        return mDefaultVal;
    }


    /* ~~~~~ UNIQUE ~~~~~ */
    private boolean mUnique;

    boolean isUnique() {
        return mUnique;
    }


    /* ~~~~~ CHECK EXISTENCE /INDEX IN TABLE ~~~~~ */
    private int mIdxInTable = -1;

    /**
     * Method for getting index of the field (column) in the specified table.
     * Will return -1 if field under such name isn't found in the table
     *
     * @return int of the field (column) in the table or -1 if it is not found
     */
    int getIdxInTable(Context c, Table t) {
        if (mIdxInTable == -1) {
            String selection = null;
            String[] selectionArgs = null;
            if (!t.isEmpty(c)) { // Table is not empty
                selection = BaseColumns._ID + "=?";
                // TODO: 002 02 мар 19 there are maybe no column with id 1
                selectionArgs = new String[]{"1"};
            }
            try (Cursor cursor = Db.getDb(c).query(t.getName(), null,
                    selection, selectionArgs,
                    null, null, null)) {
                cursor.moveToFirst();
                mIdxInTable = cursor.getColumnIndex(getName());
            }
        }
        return mIdxInTable;
    }


    /* ~~~~~ READ ~~~~~ */
    @Nullable
    List<String> getColumn(Context c, Table t) {
        return getFilteredColumn(c, t, null,
                null);
    }

    @Nullable
    List<String> getFilteredColumn(Context c, Table t,
                                   Column where, String[] whereArgs) {
        // Make sure it exists
        if (getIdxInTable(c, t) == -1) return null;

        List<String> list = new ArrayList<>();

        try (Cursor cursor = t.createCursor(c, new Column[]{this},
                where, whereArgs, null, null, null)) {
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    list.add(c.getString(cursor.getColumnIndex(getName())));
                }
            }
        }

        return list;
    }
}