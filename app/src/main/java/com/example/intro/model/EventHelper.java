package com.example.intro.model;

import android.content.ContentValues;

import static com.example.intro.ui.MainActivity.dbHelper;

public class EventHelper {
    private String TAG = "INTROVERT:" + getClass().getSimpleName();

    Event e;

    static String table = dbHelper.EVENTS_TABLE;
    static String nameColumn = dbHelper.EVENTS_NAME_COLUMN;
    static String tagsColumn = dbHelper.EVENTS_TAGS_COLUMN;
    static String dateCreatedColumn = dbHelper.EVENTS_DATE_CREATED_COLUMN;
    static String dateCompleteColumn = dbHelper.EVENTS_DATE_COMPLETE_COLUMN;
    static String completeColumn = dbHelper.EVENTS_COMPLETE_COLUMN;
    static String commentColumn = dbHelper.EVENTS_COMMENT_COLUMN;
    static String priorityColumn = dbHelper.EVENTS_PRIORITY_COLUMN;
    static String iconColumn = dbHelper.EVENTS_ICON_COLUMN;
    static String contentTypeColumn = dbHelper.EVENTS_CONTENT_TYPE_COLUMN;
    static String contentColumn = dbHelper.EVENTS_CONTENT_COLUMN;


    public static boolean updateDbWithEvent(Event e) {
        // Prepare data for DB
        ContentValues contentValues = new ContentValues();
        contentValues.put(nameColumn, e.getName());
        contentValues.put(tagsColumn, e.getTags());
        contentValues.put(contentColumn, e.getContent());
        contentValues.put(commentColumn, e.getComment());

        // Update DB depending on if this is a new Event
        boolean result = false;
        if (isNewEvent(e)) result = dbHelper.insertRow(table, contentValues);
        return result;
    }


    public static boolean isNewEvent(Event e) {
        return e.getId() == 0;
    }
}