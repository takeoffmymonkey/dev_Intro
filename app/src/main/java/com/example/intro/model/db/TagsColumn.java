package com.example.intro.model.db;

import java.util.ArrayList;
import java.util.List;

final class TagsColumn extends Column {
    /* ~~~~~ COMMON FIELDS ~~~~~ */
    final static Column TAG = new Column("Tag", Type.TEXT,
            false, false,
            true, true, Value.EMPTY, true);
    final static Column ICON = new Column("Icon", Type.INTEGER,
            false, false,
            true, true, Value.ZERO, false);
    final static Column RECORDS = new Column("Records", Type.TEXT,
            false, false,
            true, true, Value.EMPTY, false);

    private static final List<Column> all = new ArrayList<>();
    static {
        all.add(TAG);
        all.add(ICON);
        all.add(RECORDS);
    }

    static List<Column> getAllList() {
        return all;
    }

    static Column[] getAllArray() {
        return all.toArray(new Column[0]);
    }

    /* ~~~~~ CONSTRUCTORS ~~~~~ */
    private TagsColumn(String mName, Type mType) {
        super(mName, mType);
    }

    private TagsColumn(String name, Type type, boolean primaryKey, boolean autoIncrement, boolean notNull, boolean dflt, String defaultVal, boolean unique) {
        super(name, type, primaryKey, autoIncrement, notNull, dflt, defaultVal, unique);
    }
}
