package com.example.intro.model.helpers;

import android.content.ContentValues;
import android.support.annotation.Nullable;

import com.example.intro.model.Event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.intro.ui.MainActivity.dbHelper;

public class TagsHelper {

    private static String eventsTable = dbHelper.EVENTS_TABLE;
    private static String eventsTagsColumn = dbHelper.EVENTS_TAGS_COLUMN;

    private static String tagsTable = dbHelper.TAGS_TABLE;
    private static String tagsTagColumn = dbHelper.TAGS_TAG_COLUMN;
    private static String tagsEventsColumn = dbHelper.TAGS_EVENTS_COLUMN;

    private static String idColumn = dbHelper.ID_COLUMN;

    /**
     * Method for maintaining Tags table. It controls that unused tags
     * do not exist by properly reacting on the gathered info from the
     * provided event that is being currently manipulated and the sort
     * of manipulation that this event is experiencing.
     *
     * @param e event that is being manipulated
     * @param a operation on the event
     * @return result of the operation
     */
    static boolean maintainTagsTable(Event e, EventsHelper.Action a) {
        String[] tags = e.getTags();
        long id;
        boolean result = false;

        switch (a) {
            case ADD:
                id = dbHelper.nextId(eventsTable);
                result = registerEventIdToTags(tags, id);
                break;
            case UPDATE:
                id = e.getId();
                result = updateTagsOfEvent(tags, id);
                break;
            case DELETE:
                id = e.getId();
                result = removeEventIdFromTags(tags, id);
                break;
        }
        return result;
    }


    /*~~~~~~~~~~~~ READ  ~~~~~~~~~~~~*/

    /**
     * Convenience method for getting the current list of existing
     * tags (in form of array) from the database
     *
     * @return array of existing tags
     */
    public static String[] getAllTags() {
        // TODO: 021 21 фев 19 in ascending order please
        return dbHelper.getColumn(tagsTable, tagsTagColumn);
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


    /*~~~~~~~~~~~~ UTILITY ~~~~~~~~~~~~*/

    /**
     * Convenience method for splitting and cleaning the string of
     * tags from duplicates, empty values and extra commas
     *
     * @param dirtyTags the string with tags that needs to be cleaned
     * @return cleaned string array of tags
     */
    public static String[] cleanArray(String dirtyTags) {
        // Split string
        String[] dirtyArr = dirtyTags.split(",");

        // Remove duplicates and empties
        List<String> list = new ArrayList<>();
        for (String item : dirtyArr) {
            if (!list.contains(item) && !item.equals(""))
                // TODO: 021 21 фев 19 maybe 1st capital?
                list.add(item);
        }

        // Return split array
        String[] cleanArr = new String[list.size()];
        cleanArr = list.toArray(cleanArr);
        return cleanArr;
    }


    /*~~~~~~~~~~~~ CONVERSION ~~~~~~~~~~~~*/

    /**
     * Convenience method for converting string of tag IDs to array
     * of tag names.
     * Useful for creating tags[] for Event from db.
     *
     * @param ids string of tag ids
     * @return string array of corresponding tag names
     */
    static String[] IdsToCleanNames(String ids) {
        String[] names = cleanArray(ids);
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


    /*~~~~~~~~~~~~ ADD ~~~~~~~~~~~~*/

    /**
     * Method for adding new tag to Tags table.
     * If tag already exists, it is skipped.
     *
     * @param tag tag that should be parsed and added if don't exist
     * @return result of the operation
     */
    private static boolean createNewTag(String tag) {
        long tagId = getTagId(tag);
        if (tagId == 0) { // tag doesn't exist
            // Add tag
            ContentValues cv = new ContentValues();
            cv.put(tagsTagColumn, tag);
            long result = dbHelper.insertRow(tagsTable,
                    cv, null);
            return result != -1;
        }
        return true;
    }


    /**
     * Method for registering event ID to appropriate tag in the Tags
     * table.
     * If tag doesn't exist, it is being created first.
     * If event is already registered to the tag, it is skipped.
     *
     * @param tag tag that this event should be registered to
     * @param id  id of the event that is being registered
     * @return result of the operation
     */
    private static boolean registerEventIdToTag(String tag, long id) {
        // Make sure tag exists
        long tagId = getTagId(tag);
        if (tagId == 0) {
            if (createNewTag(tag)) tagId = getTagId(tag);
            else return false; // smth bad happened
        }

        // Make sure id is not registered yet
        String ids = dbHelper.getCellValue(tagsTable,
                tagsEventsColumn, tagId);
        List<String> idsList = Arrays.asList(ids.split(","));
        String idString = String.valueOf(id);
        if (idsList.contains(idString)) return true;

        // Register id
        ContentValues cv = new ContentValues();
        cv.put(tagsEventsColumn, ids + id + ",");
        return dbHelper.updateRow(tagsTable, cv, tagId);
    }


    /**
     * Bulk method for registering event ID to appropriate tags in the
     * Tags table.
     * If tags don't exist, they are being created first.
     * If event is already registered to the tag, it is skipped.
     *
     * @param tags tags that this event should be registered to
     * @param id   id of the event that is being registered
     * @return result of the operation
     */
    private static boolean registerEventIdToTags(String[] tags, long id) {
        for (String tag : tags) {
            if (!registerEventIdToTag(tag, id)) return false;
        }
        return true;
    }


    /*~~~~~~~~~~~~ REMOVE ~~~~~~~~~~~~*/

    /**
     * Method for removing tag from Tags table.
     * IMPORTANT: before using this method, ensure that tag
     * contains 1 registered event at maximum (with help of
     * isOnlyEventOfTag(String tag, long eventId))
     *
     * @param tag tag that should be removed from the table
     * @return result of the operation
     */
    private static boolean removeTag(String tag) {
        long tagId = getTagId(tag);
        if (tagId != 0) { // tag exist
            return dbHelper.deleteRow(tagsTable, tagId);
        } else return false; // tag doesn't exist
    }


    /**
     * Bulk method for removing event from the tags register.
     * If this the last event of the tag, the tag itself will be
     * removed as well.
     * Notice, that it will return true, if the event is not
     * registered already.
     *
     * @param tags    tags from which event should be removed
     * @param eventId of the only registered to this tag event
     * @return result of the operation
     */
    static boolean removeEventIdFromTags(String[] tags, long eventId) {
        for (String tag : tags) {
            if (!removeEventFromTag(tag, eventId)) return false;
        }
        return true;
    }


    /**
     * Method for removing event from the tag register.
     * If this the last event of the tag, the tag itself will be
     * removed as well.
     * Notice, that it will return true, if the event is not
     * registered already.
     *
     * @param tag     tag from which event should be removed
     * @param eventId of the only registered to this tag event
     * @return result of the operation
     */
    private static boolean removeEventFromTag(String tag, long eventId) {
        // Make sure that tag exists
        long tagId = getTagId(tag);
        if (tagId == 0) return false;

        // Delete tag if this is its last event
        if (isOnlyEventOfTag(tag, eventId)) return removeTag(tag);

        // Remove event from tag register
        String ids = dbHelper.getCellValue(tagsTable,
                tagsEventsColumn, tagId);
        List<String> idsList = Arrays.asList(cleanArray(ids));
        String eventIdString = String.valueOf(eventId);
        if (idsList.remove(eventIdString)) { // found and removed
            // Update table with new list
            String[] updatedIds = new String[idsList.size()];
            updatedIds = idsList.toArray(updatedIds);
            String updatedIdsString = cleanNamesToIds(updatedIds);
            ContentValues cv = new ContentValues();
            cv.put(tagsEventsColumn, updatedIdsString);
            return dbHelper.updateRow(tagsTable, cv, tagId);
        }
        return true;
    }


    /*~~~~~~~~~~~~ UPDATE ~~~~~~~~~~~~*/

    /**
     * Method for updating tags. It receives a new version of tags array,
     * gets old version and compares them. Then event is being registered
     * to the tags that are new for this event (non existing yet tags are
     * being created) and is being unregistered from tags that no more
     * valid for it. In latter case, if tag has no more events registered
     * to it, it is being removed.
     *
     * @param tags    NEW version of tags of the event
     * @param eventId ID of the event, whose tags are being update
     * @return result of the operation
     */
    private static boolean updateTagsOfEvent(String[] tags, long eventId) {
        // Get new and previous tags as lists for ease of manipulation
        List<String> newTags = Arrays.asList(tags);
        String prev = dbHelper.getCellValue(eventsTable,
                eventsTagsColumn, eventId);
        List<String> prevTags = Arrays.asList(IdsToCleanNames(prev));

        // Find what was added
        List<String> added = new ArrayList<>();
        for (String tag : newTags) {
            if (!prevTags.contains(tag)) added.add(tag);
        }
        // Add these tags if needed and register event to them
        String[] addedString = new String[added.size()];
        addedString = added.toArray(addedString);
        boolean addResult = registerEventIdToTags(addedString, eventId);

        // Find what was deleted
        List<String> deleted = new ArrayList<>();
        for (String tag : prevTags) {
            if (!newTags.contains(tag)) deleted.add(tag);
        }
        // Remove the event from tags and remove empty tag if needed
        String[] deletedString = new String[deleted.size()];
        deletedString = deleted.toArray(deletedString);
        boolean deleteResult = removeEventIdFromTags(deletedString, eventId);

        return addResult && deleteResult;
    }


    /*~~~~~~~~~~~~ CHECK ~~~~~~~~~~~~*/

    /**
     * Method for checking if the provided event ID is the only
     * registered to the provided tag.
     * It is useful when performing the whole tag deletion.
     *
     * @param tag     tag that should be removed from the table
     * @param eventId of the only registered to this tag event
     * @return result of the operation
     */
    private static boolean isOnlyEventOfTag(String tag, long eventId) {
        long tagId = getTagId(tag);
        // Make sure tag exists
        if (tagId != 0) { // tag exist
            // Make sure that it has only 1 registered event
            String ids = dbHelper.getCellValue(tagsTable,
                    tagsEventsColumn, tagId);
            List<String> idsList = Arrays.asList(cleanArray(ids));
            String eventIdString = String.valueOf(eventId);
            return idsList.size() == 1 && idsList.contains(eventIdString);
        } else return false; // tag doesn't exist
    }
}
