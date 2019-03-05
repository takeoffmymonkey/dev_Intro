package com.example.intro.model.db;


import java.util.ArrayList;
import java.util.List;

final class RecordsColumn extends Column {
    /* ~~~~~ COMMON FIELDS ~~~~~ */
    final static Column NAME = new Column("Name", Type.TEXT,
            false, false,
            true, true, Value.EMPTY, false);
    final static Column TAGS = new Column("Tags", Type.TEXT,
            false, false,
            true, true, Value.EMPTY, false);
    final static Column CREATED = new Column("Created", Type.INTEGER,
            false, false,
            true, true, Value.ZERO, false);
    final static Column EDITED = new Column("Edited", Type.INTEGER,
            false, false,
            true, true, Value.ZERO, false);
    final static Column COMPLETED = new Column("Completed", Type.INTEGER,
            false, false,
            true, true, Value.ZERO, false);
    final static Column COMPLETE = new Column("Complete", Type.INTEGER,
            false, false,
            true, true, Value.ZERO, false);
    final static Column TYPE = new Column("Type", Type.INTEGER,
            false, false,
            true, true, Value.ZERO, false);
    final static Column CONTENT = new Column("Content", Type.TEXT,
            false, false,
            true, true, Value.EMPTY, false);
    final static Column PRIORITY = new Column("Priority", Type.INTEGER,
            false, false,
            true, true, Value.ZERO, false);
    final static Column COMMENTS = new Column("Comments", Type.TEXT,
            false, false,
            true, true, Value.EMPTY, false);


    private static final List<Column> all = new ArrayList<>();
    static {
        all.add(NAME);
        all.add(TAGS);
        all.add(CREATED);
        all.add(EDITED);
        all.add(COMPLETED);
        all.add(COMPLETE);
        all.add(CONTENT);
        all.add(PRIORITY);
        all.add(COMMENTS);
    }

    static List<Column> getAllList() {
        return all;
    }

    static Column[] getAllArray() {
        return all.toArray(new Column[0]);
    }


    /* ~~~~~ CONSTRUCTORS ~~~~~ */
    private RecordsColumn(String mName, Type mType) {
        super(mName, mType);
    }

    private RecordsColumn(String name, Type type, boolean primaryKey, boolean autoIncrement, boolean notNull, boolean dflt, String defaultVal, boolean unique) {
        super(name, type, primaryKey, autoIncrement, notNull, dflt, defaultVal, unique);
    }
}
