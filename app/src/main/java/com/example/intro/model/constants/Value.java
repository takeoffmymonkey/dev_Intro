package com.example.intro.model.constants;

class Value {
    private Field.Type type;
    private String value;

    Value(Field.Type type, String value) {
        this.type = type;
        this.value = value;
    }

    Field.Type getType() {
        return type;
    }

    String getValue() {
        return value;
    }

    int getIntValue() {
        if (type == Field.Type.INTEGER) return Integer.parseInt(getValue());
        else return -666;
    }
}