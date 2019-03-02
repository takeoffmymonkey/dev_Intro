package com.example.intro.model.constants;

import android.content.Context;
import android.os.Environment;

import com.example.intro.model.DbHelper;

import java.io.File;

public class Db {
    private String TAG = "INTROVERT:" + getClass().getSimpleName();

    public static String getName() {
        return "introvert";
    }


    /* ~~~~~~~~~~ DBHELPER ~~~~~~~~~~ */
    private static DbHelper sDbHelper;

    public static DbHelper getDbHelper(Context context) {
        if (sDbHelper == null) sDbHelper = new DbHelper(context);
        return sDbHelper;
    }


    /* ~~~~~~~~~~ INTERNAL/EXTERNAL ~~~~~~~~~~ */
    private static boolean sExternal;

    static boolean isExternal() {
        return sExternal;
    }

    static void setExternal(boolean external) {
        sExternal = external;
    }


    /* ~~~~~~~~~~ MAIN FOLDER ~~~~~~~~~~ */
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


    /* ~~~~~~~~~~ TABLES ~~~~~~~~~~ */
    private static Table[] sTables = new Table[]{
            new Table("Records", null),
            new Table("Tags", null),
            new Table("Templates", null),
    };

    static Table[] getTables() {
        return sTables;
    }
}