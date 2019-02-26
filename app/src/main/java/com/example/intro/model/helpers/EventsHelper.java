package com.example.intro.model.helpers;

import android.content.ContentValues;
import android.util.Log;

import com.example.intro.model.Event;

import java.util.Map;
import java.util.Set;

import static com.example.intro.ui.MainActivity.dbHelper;

public class EventsHelper {
    private static String TAG = "INTROVERT:" + "EventsHelper";

    Event e;

    private static String eventsTable = dbHelper.EVENTS_TABLE;
    private static String eventsTimeCreatedColumn = dbHelper
            .EVENTS_TIME_CREATED_COLUMN;
    private static String eventsTimeEditedColumn = dbHelper
            .EVENTS_TIME_EDITED_COLUMN;
    private static String eventsTimeCompleteColumn = dbHelper
            .EVENTS_TIME_COMPLETE_COLUMN;
    private static String eventsNameColumn = dbHelper.EVENTS_NAME_COLUMN;
    private static String eventsTagsColumn = dbHelper.EVENTS_TAGS_COLUMN;
    private static String eventsCommentColumn = dbHelper.EVENTS_COMMENT_COLUMN;
    private static String eventsContentColumn = dbHelper.EVENTS_CONTENT_COLUMN;
    private static String eventsCompleteColumn = dbHelper.EVENTS_COMPLETE_COLUMN;
    private static String eventsPriorityColumn = dbHelper.EVENTS_PRIORITY_COLUMN;

    enum ContentType {TEXT, AUDIO, VALUE}

    enum Action {ADD, UPDATE, DELETE}


    /**
     * Method for building an Event object by use of its database id.
     * If 0 is provided, then a fresh Event is built.
     *
     * @param id db id of event that has to be build or 0 for new one
     * @return built Event object
     */
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
                        e.setTags(TagsHelper.IdsToCleanNames(map.get(key)));
                        break;
                    case "TimeCreated":
                        e.setTimeCreated(Long.parseLong(map.get(key)));
                        break;
                    case "TimeEdited":
                        e.setTimeEdited(Long.parseLong(map.get(key)));
                        break;
                    case "TimeComplete":
                        e.setTimeComplete(Long.parseLong(map.get(key)));
                        break;
                    case "Complete":
                        if (Integer.parseInt(map.get(key)) == 1) {
                            e.setComplete(true);
                        } else e.setComplete(false);
                        break;
                    case "Comment":
                        e.setComment(map.get(key));
                        break;
                    case "Priority":
                        e.setPriority(Byte.parseByte(map.get(key)));
                        break;
                    case "Icon":
                        e.setIcon(Short.parseShort(map.get(key)));
                        break;
                    case "ContentType":
                        e.setContentType(Byte.parseByte(map.get(key)));
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

    /**
     * Method for
     *
     * @param e event that is to be deleted
     * @return result of the operation
     */
    public static boolean updateDbWithEvent(Event e) {
        // Update Tags table corresponding to Event
        Action action;
        if (e.isNewEvent()) action = Action.ADD;
        else action = Action.UPDATE;
        boolean tagsUpdated = TagsHelper.maintainTagsTable(e, action);

        // Prepare data for DB
        ContentValues cv = new ContentValues();
        cv.put(eventsTimeCreatedColumn, e.getTimeCreated());
        cv.put(eventsTimeEditedColumn, e.getTimeEdited());
        cv.put(eventsTimeCompleteColumn, e.getTimeComplete());
        cv.put(eventsTagsColumn, TagsHelper
                .cleanNamesToIds(e.getTags()));
        cv.put(eventsNameColumn, e.getName());
        cv.put(eventsContentColumn, e.getContent());
        cv.put(eventsCommentColumn, e.getComment());
        cv.put(eventsPriorityColumn, e.getPriority());
        byte complete = 0;
        if (e.isComplete()) complete = 1;
        cv.put(eventsCompleteColumn, complete);

        // Update DB depending on if this is a new Event
        boolean eventUpdated;
        long id;
        if (e.isNewEvent()) {
            id = dbHelper.insertRow(eventsTable,
                    cv, dbHelper.getWritableDatabase());
            e.setId(id);
            eventUpdated = id != -1;
        } else eventUpdated = dbHelper.updateRow(eventsTable, cv,
                e.getId());

        return tagsUpdated && eventUpdated;
    }


    /**
     * Method for removing event from db. It also removes event from
     * tags table, and as a consequence may remove a tag that has no
     * connected events to it.
     *
     * @param e event that is to be deleted
     * @return result of the operation
     */
    public static boolean deleteEventFromDb(Event e) {
        boolean remove = TagsHelper
                .removeEventIdFromTags(e.getTags(), e.getId());
        return remove && dbHelper.deleteRow(eventsTable, e.getId());
    }

}