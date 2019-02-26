package com.example.intro.model;

import java.util.Arrays;

public class Event {
    private String TAG = "INTROVERT:" + getClass().getSimpleName();

    private long id = 0;
    private String name;
    private String[] tags;
    private long timeCreated = System.currentTimeMillis();
    private long timeEdited = System.currentTimeMillis();
    private long timeComplete;
    private boolean complete;
    private String comment;
    private byte priority;
    private short icon;
    private byte contentType;
    private String content;


    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String[] getTags() {
        return tags;
    }


    public void setTags(String[] tags) {
        this.tags = tags;
    }


    public long getTimeEdited() {
        return timeEdited;
    }


    public void setTimeEdited(long timeEdited) {
        this.timeEdited = timeEdited;
    }


    public long getTimeCreated() {
        return timeCreated;
    }


    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }


    public long getTimeComplete() {
        return timeComplete;
    }


    public void setTimeComplete(long timeComplete) {
        this.timeComplete = timeComplete;
    }


    public boolean isComplete() {
        return complete;
    }


    public void setComplete(boolean complete) {
        this.complete = complete;
    }


    public String getComment() {
        return comment;
    }


    public void setComment(String comment) {
        this.comment = comment;
    }


    public byte getPriority() {
        return priority;
    }


    public void setPriority(byte priority) {
        this.priority = priority;
    }


    public short getIcon() {
        return icon;
    }


    public void setIcon(short icon) {
        this.icon = icon;
    }


    public byte getContentType() {
        return contentType;
    }


    public void setContentType(byte contentType) {
        this.contentType = contentType;
    }


    public String getContent() {
        return content;
    }


    public void setContent(String content) {
        this.content = content;
    }


    public boolean isNewEvent() {
        return getId() == 0;
    }


    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", tags='" + Arrays.toString(tags) + '\'' +
                ", timeCreated=" + timeCreated +
                ", timeComplete=" + timeComplete +
                ", complete=" + complete +
                ", comment='" + comment + '\'' +
                ", priority=" + priority +
                ", icon=" + icon +
                ", contentType=" + contentType +
                ", content='" + content + '\'' +
                '}';
    }
}