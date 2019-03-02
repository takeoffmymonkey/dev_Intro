package com.example.intro.model.constants;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;

class Field implements BaseColumns {
    private String TAG = "INTROVERT:" + getClass().getSimpleName();

    enum Type {INTEGER, TEXT}

    private String mName;
    private Type mType;
    private boolean mPrimaryKey;
    private boolean mAutoIncrement;
    private boolean mNotNull;
    private boolean mDefault;
    private String mDefaultVal = "";
    private boolean mUnique;

    private int mIdxInTable = -1;

    public Field(String mName, Type mType) {
        this.mName = mName;
        this.mType = mType;
    }

    public Field(String name, Type type, boolean primaryKey,
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

    String getName() {
        return mName;
    }

    Type getType() {
        return mType;
    }

    boolean isPrimaryKey() {
        return mPrimaryKey;
    }

    boolean isAutoIncrement() {
        return mAutoIncrement;
    }

    boolean isNotNull() {
        return mNotNull;
    }

    boolean isDefault() {
        return mDefault;
    }

    String getDefaultVal() {
        return mDefaultVal;
    }

    boolean isUnique() {
        return mUnique;
    }


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
            try (Cursor cursor = Db.getDbHelper(c).getWritableDatabase()
                    .query(t.getName(), null, selection, selectionArgs,
                            null, null, null)) {
                cursor.moveToFirst();
                mIdxInTable = cursor.getColumnIndex(getName());
            }
        }
        return mIdxInTable;
    }
}