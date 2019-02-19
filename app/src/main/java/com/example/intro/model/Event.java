package com.example.intro.model;

public class Event {
    private String TAG = "INTROVERT:" + getClass().getSimpleName();

    private long id = 0;
    private String name;
    private String[] tags;
    private long dateCreated;
    private long dateEdited;
    private long dateComplete;
    private boolean complete;
    private String comment;
    private int priority;
    private int icon;
    private int contentType;
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


    public long getDateEdited() {
        return dateEdited;
    }


    public void setDateEdited(long dateEdited) {
        this.dateEdited = dateEdited;
    }


    public long getDateCreated() {
        return dateCreated;
    }


    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }


    public long getDateComplete() {
        return dateComplete;
    }


    public void setDateComplete(long dateComplete) {
        this.dateComplete = dateComplete;
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


    public int getPriority() {
        return priority;
    }


    public void setPriority(int priority) {
        this.priority = priority;
    }


    public int getIcon() {
        return icon;
    }


    public void setIcon(int icon) {
        this.icon = icon;
    }


    public int getContentType() {
        return contentType;
    }


    public void setContentType(int contentType) {
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
                ", tags='" + tags + '\'' +
                ", dateCreated=" + dateCreated +
                ", dateComplete=" + dateComplete +
                ", complete=" + complete +
                ", comment='" + comment + '\'' +
                ", priority=" + priority +
                ", icon=" + icon +
                ", contentType=" + contentType +
                ", content='" + content + '\'' +
                '}';
    }
}