package com.example.intro.model.helpers;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

public class Location {
    public static Location EXTERNAL
            = new Location("EXTERNAL", "");
    public static Location INTERNAL
            = new Location("INTERNAL", "");
    public static Location RECS_TXT
            = new Location("RECS_TXT", "Records.txt");
    public static Location RECS_FOLDER
            = new Location("RECS_FOLDER", "/Records");
    public static Location TAGS_TXT
            = new Location("TAGS_TXT", "Tags.txt");
    public static Location TEMPLATES_TXT
            = new Location("TEMPLATES_TXT", "Templates.txt");
    public static Location TYPES_TXT
            = new Location("TYPES_TXT", "Types.txt");
    public static Location PREFS_TXT
            = new Location("TYPES_TXT", "Preferences.txt");


    private String name;
    private String relativePath;
    private String fullPath;
    private File file;


    public Location(String name, String relativePath) {
        this.name = name;
        this.relativePath = relativePath;
    }


    public String getName() {
        return name;
    }


    public String getRelativePath() {
        return relativePath;
    }


    public String getFullPath(Context context) {
        if (fullPath == null) {
            // TODO: 028 28 фев 19 make internal available
//            String path = context.getFilesDir().getPath();
            String path = context.getExternalFilesDir(null).getPath();
            fullPath = path + relativePath;
        }
        return fullPath;
    }


    public File getFile(Context context) {
        if (file == null) file = new File(getFullPath(context));
        return file;
    }


    /**
     * Method for checking if location is ready to work with.
     * It checks whether the location exists and in case if it is
     * on external storage - whether it is writable.
     * If location doesn't exist, it gets created.
     *
     * @return result of checking and preparation of the location
     */
    public boolean isReady(Context context) {
        return accessible(context) && exists(context);
    }


    private boolean accessible(Context context) {
        return isExternalStorageWritable() && getFile(context).canWrite();
    }


    private boolean exists(Context context) {
        if (getFile(context).exists()) return true;
        else return makeNew(context);
    }


    private boolean makeNew(Context context) {
        if (getName().equals("EXTERNAL") || getName().equals("INTERNAL"))
            return false;
        else {
            try {
                return getFile(context).createNewFile();
            } catch (IOException e) {
                return false;
            }
        }
    }


    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
}