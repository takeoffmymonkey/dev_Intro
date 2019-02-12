package com.example.intro.model;

import android.content.ContentValues;

import static com.example.intro.ui.MainActivity.dbHelper;

public class EventHelper {
    Event e;

    static String table = DbHelper.EVENTS_TABLE;
    static String nameColumn = DbHelper.EVENTS_NAME_COLUMN;
    static String tagsColumn = DbHelper.EVENTS_TAGS_COLUMN;
    static String dateCreatedColumn = DbHelper.EVENTS_DATE_CREATED_COLUMN;
    static String dateCompleteColumn = DbHelper.EVENTS_DATE_COMPLETE_COLUMN;
    static String completeColumn = DbHelper.EVENTS_COMPLETE_COLUMN;
    static String commentColumn = DbHelper.EVENTS_COMMENT_COLUMN;
    static String priorityColumn = DbHelper.EVENTS_PRIORITY_COLUMN;
    static String iconColumn = DbHelper.EVENTS_ICON_COLUMN;
    static String contentTypeColumn = DbHelper.EVENTS_CONTENT_TYPE_COLUMN;
    static String contentColumn = DbHelper.EVENTS_CONTENT_COLUMN;


    public static boolean updateEvent(Event e) {
        boolean fresh = false;
        if (e.getId() == -1) fresh = true;

        ContentValues contentValues = new ContentValues();
        contentValues.put(nameColumn, e.getName());
        contentValues.put(tagsColumn, e.getTags());
        contentValues.put(contentColumn, e.getContent());
        contentValues.put(commentColumn, e.getComment());

        if (fresh) dbHelper.insertRow(table, contentValues);

        return true;
    }
}
