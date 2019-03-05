package com.example.intro.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
    private String TAG = "INTROVERT:" + getClass().getSimpleName();


    public DbHelper(Context context) {
        super(context, Db.getName(), null, Db.getVersion());
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        for (Table t : Table.getAllArray()) {
            t.create(db);
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}