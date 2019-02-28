package com.example.intro.model.helpers;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class FilesHelper {
    private static String TAG = "INTROVERT:FilesHelper";


    public static boolean isLowFreeSpace(Location location, Context context) {
        long total = location.getFile(context).getTotalSpace();
        long free = location.getFile(context).getFreeSpace();
        return free < (total * .05);
    }


    /*~~~~~~~~~~~~~~~~~~~~ CREATE ~~~~~~~~~~~~~~~~~~~~ */
    public static boolean createFile(Location loc, String name, Context c) {
        File file = new File(loc.getFullPath(c) + name);
        try {
            return file.createNewFile();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
    }

    public static boolean[] createFiles(Location loc, String[] names, Context c) {
        boolean[] results = new boolean[names.length];
        for (int i = 0; i < names.length; i++) {
            results[i] = createFile(loc, names[i], c);
        }
        return results;
    }


    public static boolean createDir(Location loc, String name, Context c) {
        return new File(loc.getFullPath(c), name).mkdir();
    }


    public static boolean[] createDirs(Location loc, String[] names, Context c) {
        boolean[] results = new boolean[names.length];
        for (int i = 0; i < names.length; i++) {
            results[i] = createDir(loc, names[i], c);
        }
        return results;
    }


    /*~~~~~~~~~~~~~~~~~~~~ DELETE ~~~~~~~~~~~~~~~~~~~~ */
    public static boolean deleteFile(Location loc, String name, Context c) {
        File file = new File(loc.getFullPath(c), name);
        if (file.isFile()) return file.delete();
        else return false;
    }


    public static boolean[] deleteFiles(Location loc, String[] names, Context c) {
        boolean[] results = new boolean[names.length];
        for (int i = 0; i < names.length; i++) {
            results[i] = deleteFile(loc, names[i], c);
        }
        return results;
    }


    public static boolean deleteDir(Location loc, String name, Context c) {
        File file = new File(loc.getFullPath(c), name); // dir to delete
        if (file.isDirectory()) {
            File[] files = file.listFiles(); // files in the dir to delete
            if (files.length == 0) return file.delete(); // dir is empty
            else { // dir contains something
                for (File f : files) {
                    if (f.isFile()) f.delete(); // it's a file
                    else { // it's a folder
                        // create parent as location
                        String locat = loc.getRelativePath();
                        locat += file.getName();
                        Location l = new Location("", locat);
                        deleteDir(l, f.getName(), c);
                    }
                }
            }
        }
        return false;
    }


    public static boolean[] deleteDirs(Location loc, String[] names, Context c) {
        boolean[] results = new boolean[names.length];
        for (int i = 0; i < names.length; i++) {
            results[i] = deleteDir(loc, names[i], c);
        }
        return results;
    }


    /*~~~~~~~~~~~~~~~~~~~~ READ ~~~~~~~~~~~~~~~~~~~~ */


    /*~~~~~~~~~~~~~~~~~~~~ WRITE ~~~~~~~~~~~~~~~~~~~~ */
}