package com.example.intro.model.helpers;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

public class FilesHelper {
    private Context context;

    private Storage internal;
    private Storage external;
    private Storage temp;


    public FilesHelper(Context context) {
        this.context = context;
    }


    public Storage getInternal() {
        if (internal == null) {
            internal = Storage.INTERNAL;
            internal.file = context.getFilesDir();
            internal.path = internal.file.getPath();
        }
        return internal;
    }


    public Storage getExternal() throws IOException {
        if (external == null) {
            if (isExternalStorageWritable()) { // we have write access
                external = Storage.EXTERNAL;
                external.file = context.getExternalFilesDir(null);
                external.path = external.file.getPath();
            } else throw new IOException(); // we don't have write access
        }
        return external;
    }


    public Storage getTemp() {
        if (temp == null) {
            try {
                createTempDir(getExternal());
            } catch (IOException e) {
                e.printStackTrace();
                createTempDir(getInternal()); // no access to external
            }
        }
        return temp;
    }


    public enum Storage {
        INTERNAL, EXTERNAL, TEMP;

        private File file;
        private String path;


        public File getFile() {
            return file;
        }


        public String getPath() {
            return path;
        }
    }


    private File getDirFile(Storage storage) {
        return storage.file;
    }


    private String getDirPath(Storage storage) {
        return storage.path;
    }


    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }


    public boolean isLowFreeSpace(Storage storage) {
        File file = getDirFile(storage);
        long total = file.getTotalSpace();
        long free = file.getFreeSpace();
        return free < (total * .05);
    }


    public boolean deleteFile(File file) {
        return file.delete();
    }


    public boolean createFile(File file) {
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean createDir(Storage storage, String relativePath) {
        File file = new File(storage.file, relativePath);
        return file.mkdirs();
    }


    public boolean createTempDir(Storage storage) {
        if (storage == Storage.EXTERNAL)
            if (!isExternalStorageWritable()) return false;

        String tempString = "Temp";
        temp = Storage.TEMP;
        File file = new File(storage.file, tempString);
        temp.file = file;
        temp.path = file.getPath();
        return createDir(storage, tempString);
    }
}