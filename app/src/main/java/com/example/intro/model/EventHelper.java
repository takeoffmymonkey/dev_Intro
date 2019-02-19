package com.example.intro.model;

import android.content.ContentValues;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
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
    static String tagsEventsColumn = dbHelper.TAGS_EVENTS_COLUMN;

    static String idColumn = dbHelper.ID_COLUMN;

    enum Action {ADD, UPDATE, DELETE}

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
                    case "DateCreated":
                        e.setDateCreated(Long.parseLong(map.get(key)));
                        break;
                    case "DateEdited":
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
        // Update Tags table corresponding to Event
        Action action;
        if (e.isNewEvent()) action = Action.ADD;
        else action = Action.UPDATE;
        boolean tagsUpdated = TagsHelper.maintainTagsTable(e, action);

        // Prepare data for DB
        ContentValues contentValues = new ContentValues();
        contentValues.put(eventsTagsColumn, TagsHelper.
                cleanNamesToIds(e.getTags()));
        contentValues.put(eventsNameColumn, e.getName());
        contentValues.put(eventsContentColumn, e.getContent());
        contentValues.put(eventsCommentColumn, e.getComment());

        // Update DB depending on if this is a new Event
        boolean eventUpdated;
        long id;
        if (e.isNewEvent()) {
            id = dbHelper.insertRow(eventsTable,
                    contentValues, dbHelper.getWritableDatabase());
            e.setId(id);
            eventUpdated = id != -1;
        } else eventUpdated = dbHelper.updateRow(eventsTable, contentValues,
                e.getId());

        return tagsUpdated && eventUpdated;
    }


    public static boolean deleteEventFromDb(Event e) {
        return dbHelper.deleteRow(eventsTable, e.getId());
    }


    public static class TagsHelper {
        /**
         * Convenience method for getting the current list of existing
         * tags (in form of array) from the database
         *
         * @return array of existing tags
         */
        public static String[] getAllTags() {
            return dbHelper.getColumn(tagsTable, tagsTagColumn);
        }


        /**
         * Convenience method for splitting and cleaning the string of
         * tags from duplicates, empty values and extra commas
         *
         * @param dirtyTags the string with tags that needs to be cleaned
         * @return cleaned string array of tags
         */
        public static String[] cleanTags(String dirtyTags) {
            // Split string
            String[] dirtyTagsArr = dirtyTags.split(",");

            // Remove duplicates and empties
            List<String> tagsList = new ArrayList<>();
            for (String tag : dirtyTagsArr) {
                if (!tagsList.contains(tag) && !tag.equals(""))
                    tagsList.add(tag);
            }

            // Return split array
            String[] cleanTags = new String[tagsList.size()];
            cleanTags = tagsList.toArray(cleanTags);
            return cleanTags;
        }


        /**
         * Convenience method for getting tag ID by tag name
         *
         * @param tag Tag name
         * @return ID of the tag or 0 if tag is not found
         */
        static long getTagId(String tag) {
            long tagId = 0;
            String[] column = dbHelper.getFilteredColumn(tagsTable,
                    idColumn, tagsTagColumn, tag);
            if (column != null && column.length == 1)
                tagId = Long.parseLong(column[0]);
            return tagId;
        }


        /**
         * Convenience method for getting tag name by tag ID
         *
         * @param id Tag ID
         * @return name of the tag or NULL if tag is not found
         */
        @Nullable
        private static String getTagName(long id) {
            String tagName = null;
            String[] column = dbHelper.getFilteredColumn(tagsTable,
                    tagsTagColumn, idColumn, String.valueOf(id));
            if (column != null && column.length == 1)
                tagName = column[0];
            return tagName;
        }


        /**
         * Convenience method for converting string of tag IDs to array
         * of tag names.
         * Useful for creating tags[] for Event from db.
         *
         * @param ids string of tag ids
         * @return string array of corresponding tag names
         */
        static String[] IdsToCleanNames(String ids) {
            String[] names = cleanTags(ids);
            for (int i = 0; i < names.length; i++) {
                names[i] = getTagName(Long.parseLong(names[i]));
            }
            return names;
        }


        /**
         * Convenience method for converting string array of clean tag
         * names to string of tag IDs.
         * Useful for recording array of tags to db.
         *
         * @param names clean string array of tag names
         * @return string of corresponding tag ids
         */
        static String cleanNamesToIds(String[] names) {
            StringBuilder ids = new StringBuilder();
            for (int i = 0; i < names.length; i++) {
                ids.append(getTagId(names[i]));
                if (i == names.length - 1) break;
                ids.append(",");
            }
            return ids.toString();
        }


        /**
         * Method for maintaining Tags table. It controls that unused tags
         * do not exist by properly reacting on the gathered info from the
         * provided event that is being currently manipulated and the sort
         * of manipulation that this event is experiencing.
         *
         * @param event  event that is being manipulated
         * @param action operation on the event
         * @return result of the operation
         */
        static boolean maintainTagsTable(Event event, Action action) {
            boolean result = false;

            switch (action) {
                case ADD:
                    result = maintainTagsTableAdd(event);
                    break;
                case UPDATE:
                    result = maintainTagsTableUpdate(event);
                    break;
                case DELETE:
                    result = maintainTagsTableDelete(event);
                    break;
            }
            return result;
        }


        private static boolean maintainTagsTableAdd(Event e) {
            // Get id for the new Event
            long futureId = dbHelper.nextId(eventsTable);

            // Make sure all tags exist and add event's id to them
            for (String tag : e.getTags()) {
                long tagId = getTagId(tag);
                if (tagId == 0) { // tag doesn't exist
                    // Add tag & register this event to it
                    ContentValues cv = new ContentValues();
                    cv.put(tagsTagColumn, tag);
                    cv.put(tagsEventsColumn, futureId + ",");
                    long result = dbHelper.insertRow(tagsTable,
                            cv, null);
                    if (result == -1) return false; // smth bad happened
                } else { // tag exists
                    // Add this event to other existing for this tag
                    String currentValue = dbHelper.getCellValue(tagsTable,
                            tagsEventsColumn, tagId);
                    ContentValues cv = new ContentValues();
                    cv.put(tagsEventsColumn, currentValue + futureId + ",");
                    boolean result = dbHelper.updateRow(tagsTable,
                            cv, tagId);
                    if (!result) return false; // smth bad happened
                }
            }
            return true;
        }


        private static boolean maintainTagsTableUpdate(Event e) {
            // Get new and previous tags as lists for ease of manipulation
            List<String> newTags = Arrays.asList(e.getTags());
            String prev = dbHelper.getCellValue(eventsTable,
                    eventsTagsColumn, e.getId());
            List<String> prevTags = Arrays.asList(TagsHelper.IdsToCleanNames(prev));

            // Find what was deleted
            List<String> deleted = new ArrayList<>();
            for (String tag : prevTags) {
                if (!newTags.contains(tag)) deleted.add(tag);
            }

            // Find what was added
            List<String> added = new ArrayList<>();
            for (String tag : newTags) {
                if (!prevTags.contains(tag)) added.add(tag);
            }

            return true;
        }


        private static boolean maintainTagsTableDelete(Event e) {
            return true;
        }
    }
}