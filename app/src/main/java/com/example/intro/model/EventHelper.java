package com.example.intro.model;

import android.content.ContentValues;
import android.util.Log;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import static com.example.intro.ui.MainActivity.dbHelper;

public class EventHelper {
    private static String TAG = "INTROVERT:" + "EventHelper";

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


    public static boolean isNewEvent(Event e) {
        return e.getId() == 0;
    }


    public static Event buildEvent(long id) {
        Event e = new Event();
        e.setId(id);
        boolean isOld = id != 0;

        if (isOld) {
            Map<String, String> map = dbHelper.getMappedRow(table, id);
            Set<String> keySet = map.keySet();

            for (String key : keySet) {
                switch (key) {
                    case "Name":
                        e.setName(map.get(key));
                        break;
                    case "Tags":
                        e.setTags(map.get(key));
                        break;
                    case "DateCreated":
                        e.setDateCreated(Long.parseLong(map.get(key)));
                        break;
                    case "DateComplete":
                        e.setDateComplete(Long.parseLong(map.get(key)));
                        break;
                    case "Complete":
                        // TODO: 014 14 фев 19 parse bool properly
                        //e.setComplete(Boolean.parseBoolean(map.get(key)));
                        break;
                    case "Comment":
                        e.setComment(map.get(key));
                        break;
                    case "Priority":
                        e.setPriority(Integer.parseInt(map.get(key)));
                        break;
                    case "Icon":
                        e.setIcon(Integer.parseInt(map.get(key)));
                        break;
                    case "ContentType":
                        e.setContentType(Integer.parseInt(map.get(key)));
                        break;
                    case "Content":
                        e.setContent(map.get(key));
                        break;
                }
            }
        }

        Log.i(TAG, e.toString());
        return e;
    }


    public static boolean updateDbWithEvent(Event e) {
        // Prepare data for DB
        ContentValues contentValues = new ContentValues();
        contentValues.put(nameColumn, e.getName());
        contentValues.put(tagsColumn, e.getTags());
        contentValues.put(contentColumn, e.getContent());
        contentValues.put(commentColumn, e.getComment());

        // Update DB depending on if this is a new Event
        boolean result;
        if (isNewEvent(e)) result = dbHelper.insertRow(table, contentValues);
        else result = dbHelper.updateRow(table, contentValues, e.getId());
        return result;
    }


    public static boolean deleteEventFromDb(Event e) {
        return dbHelper.deleteRow(table, e.getId());
    }
}