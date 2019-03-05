package com.example.intro.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;
import java.util.List;

public class Db {
    private String TAG = "INTROVERT:" + getClass().getSimpleName();


    /* ~~~~~ NAME & VERSION ~~~~~ */
    private static final String NAME = "introvert";
    private static final int VERSION = 1;

    static String getName() {
        return NAME;
    }

    static int getVersion() {
        return VERSION;
    }


    /* ~~~~~ DBHELPER AND DATABASE ~~~~~ */
    private static DbHelper sDbHelper;

    static SQLiteDatabase getDb(Context context) {
        if (sDbHelper == null) sDbHelper = new DbHelper(context);
        return sDbHelper.getWritableDatabase();
    }


    /* ~~~~~ INTERNAL/EXTERNAL ~~~~~ */
    private static boolean sExternal;

    static boolean isExternal() {
        return sExternal;
    }

    static void setExternal(boolean external) {
        sExternal = external;
    }


    /* ~~~~~ MAIN FOLDER ~~~~~ */
    private static File sMainFolder;

    static File getMainFolder(Context context) {
        if (sMainFolder == null) {
            if (!isExternal()) sMainFolder = context.getFilesDir();
            else sMainFolder = context.getExternalFilesDir(null);
        }
        return sMainFolder;
    }

    static boolean isMainWritable() {
        if (isExternal()) return Environment.MEDIA_MOUNTED.equals
                (Environment.getExternalStorageState());
        else return true;
    }
}