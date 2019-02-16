package com.example.intro.model;

import android.content.ContentValues;
import android.util.Log;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.example.intro.ui.MainActivity.dbHelper;

public class EventHelper {
    private static String TAG = "INTROVERT:" + "EventHelper";

    Event e;

    static String eventsTable = dbHelper.EVENTS_TABLE;
    static String eventsNameColumn = dbHelper.EVENTS_NAME_COLUMN;
    static String eventsTagsColumn = dbHelper.EVENTS_TAGS_COLUMN;
    static String eventsCommentColumn = dbHelper.EVENTS_COMMENT_COLUMN;
    static String eventsContentColumn = dbHelper.EVENTS_CONTENT_COLUMN;

    static String tagsTable = dbHelper.TAGS_TABLE;
    static String tagsTagColumn = dbHelper.TAGS_TAG_COLUMN;


    public static boolean isNewEvent(Event e) {
        return e.getId() == 0;
    }


    public static Event buildEvent(long id) {
        Event e = new Event();
        e.setId(id);
        boolean isOld = id != 0;

        if (isOld) {
            Map<String, String> map = dbHelper.getMappedRow(eventsTable, id);
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
        contentValues.put(eventsNameColumn, e.getName());
        contentValues.put(eventsTagsColumn, e.getTags());
        contentValues.put(eventsContentColumn, e.getContent());
        contentValues.put(eventsCommentColumn, e.getComment());

        // Update DB depending on if this is a new Event
        boolean result;
        if (isNewEvent(e)) result = dbHelper.insertRow(eventsTable,
                contentValues, dbHelper.getWritableDatabase());
        else result = dbHelper.updateRow(eventsTable, contentValues,
                e.getId());
        return result;
    }


    public static boolean deleteEventFromDb(Event e) {
        return dbHelper.deleteRow(eventsTable, e.getId());
    }


    public static List<String> getTagsList() {
        String[] tags = dbHelper.getColumn(tagsTable, tagsTagColumn);
        return Arrays.asList(tags);
    }
}