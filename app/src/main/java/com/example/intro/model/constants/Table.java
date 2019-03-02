package com.example.intro.model.constants;

import android.content.Context;
import android.database.Cursor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

class Table {
    private String TAG = "INTROVERT:" + getClass().getSimpleName();

    Table(String name, Field[] fields) {
        mName = name;
        mFields = fields;
    }

    /* ~~~~~ NAME ~~~~~ */
    private String mName;

    String getName() {
        return mName;
    }


    /* ~~~~~ FIELDS ~~~~~ */
    private Field[] mFields;

    Field[] getFields() {
        return mFields;
    }


    /* ~~~~~ FILE ~~~~~ */
    private File mFile;

    File getFile(Context c) {
        if (mFile == null) mFile = new File(Db.getMainFolder(c), mName);
        return mFile;
    }


    /* ~~~~~ CONVENIENCE ~~~~~ */
    boolean isEmpty(Context c) {
        boolean empty = true;
        try (Cursor cursor = Db.getDbHelper(c).getWritableDatabase().rawQuery
                ("SELECT COUNT(*) FROM " + mName, null)) {
            if (cursor != null && cursor.moveToFirst())
                empty = cursor.getInt(0) == 0;
        }
        return empty;
    }

    boolean exists(Context c) {
        // Check table existence
        boolean tableExists = false;
        Cursor cursor = Db.getDbHelper(c).getWritableDatabase().rawQuery
                ("select DISTINCT tbl_name from sqlite_master where" +
                        " tbl_name = '" + mName + "'", null);
        if (cursor != null) {
            // TODO: 002 02 мар 19 Does empty table return 0-length cursor
            if (cursor.getCount() > 0) tableExists = true;
            cursor.close();
        } else tableExists = false;

        // Check file existence
        boolean fileExists = new File(Db.getMainFolder(c), mName).exists();

        return tableExists && fileExists;
    }

    boolean delete(Context c) {
        // Remove table
        Db.getDbHelper(c).getWritableDatabase().execSQL
                ("DROP TABLE IF EXISTS " + mName);

        // Remove file
        return new File(Db.getMainFolder(c), getName()).delete();
    }

    boolean create(Context c) {
        // Create table
        Db.getDbHelper(c).getWritableDatabase()
                .execSQL(createTableCommand());

        // Create file and add header
        return addFileHeader();
    }

    private String createTableCommand() {
        Field f;
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ");
        sb.append(mName);
        sb.append(" (");
        for (int i = 0; i < mFields.length; i++) {
            f = mFields[i];
            // Field name
            sb.append(f.getName());
            if (f.isPrimaryKey()) sb.append(" PRIMARY KEY");
            if (f.isUnique()) sb.append(" UNIQUE");
            if (f.isAutoIncrement()) sb.append(" AUTOINCREMENT");
            if (f.isNotNull()) sb.append(" NOT NULL");
            if (f.isDefault()) {
                sb.append(" DEFAULT ");
                sb.append(f.getDefaultVal());
            }
            if (i != mFields.length) sb.append(",");
        }
        sb.append(");");

        return sb.toString();
    }

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
}