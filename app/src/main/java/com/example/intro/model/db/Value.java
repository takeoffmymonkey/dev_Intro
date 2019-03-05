package com.example.intro.model.db;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.Nullable;

class Value {
    private String TAG = "INTROVERT:" + getClass().getSimpleName();

    static final String EMPTY = "\"\"";
    static final String ZERO = "0";

    /* ~~~~~ CONSTRUCTORS ~~~~~ */
    Value(Column.Type type, String value) {
        this.type = type;
        this.value = value;
        rowId = 0;
    }

    Value(Column.Type type, String value, long rowId, Column column) {
        this.type = type;
        this.value = value;
        this.rowId = rowId;
        this.column = column;
    }


    /* ~~~~~ TYPE ~~~~~ */
    private Column.Type type;

    Column.Type getType() {
        return type;
    }


    /* ~~~~~ VALUE ~~~~~ */
    private String value;

    String getValue() {
        return value;
    }

    int getIntValue() {
        if (type == Column.Type.INTEGER) return Integer.parseInt(getValue());
        else return -666;
    }


    /* ~~~~~ ROW ID ~~~~~ */
    private long rowId;

    long getRowId() {
        return rowId;
    }

    /* ~~~~~ FIELD ~~~~~ */
    private Column column;

    Column getColumn() {
        return column;
    }


    /* ~~~~~ CHANGED AND UPDATED ~~~~~ */
    // value changed since creation of object
    private boolean changed;
    // changed value was written to db
    private boolean updated;

    boolean isChanged() {
        return changed;
    }

    boolean isUpdated() {
        return updated;
    }


    /* ~~~~~ CONVENIENCE ~~~~~ */
    private boolean isCheckable() {
        return getRowId() != 0 && getColumn() != null;
    }

    private Cursor createCursor(Context c, Table t) {
        Column[] columns = {getColumn()};
        Column where = Column.ID;

        return t.createCursor(c, columns, where,
                new String[]{String.valueOf(getRowId())},
                null, null, null);
    }

    /**
     * Method for checking if value with such ID and Column as this object
     * represents, exists in db.
     * Attention: it doesn't compare actual value with the one that this
     * object represents (you can use this method in conjunction with
     * isCorrespond() for such purpose)
     */
    boolean isExist(Context c, Table t) {
        if (!isCheckable()) return false;
        else {
            try (Cursor cursor = createCursor(c, t)) {
                return isExist(cursor);
            }
        }
    }

    private boolean isExist(Cursor c) {
        return c != null && c.getCount() == 1;
    }

    /**
     * Method for checking if value that this object represents,
     * corresponds to actual value in db
     */
    boolean isCorrespond(Context c, Table t) {
        String actual = getActualValue(c, t);

        if (actual == null) return false;
        return actual.equals(getValue());
    }


    @Nullable
    String getActualValue(Context c, Table t) {
        if (!isCheckable()) return null;
        else {
            try (Cursor cursor = createCursor(c, t)) {
                if (!isExist(cursor)) return null;
                cursor.moveToFirst();
                return cursor.getString(cursor.getColumnIndex
                        (getColumn().getName()));
            }
        }
    }
}