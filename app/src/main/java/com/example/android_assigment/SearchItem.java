package com.example.android_assigment;

public class SearchItem {

    public enum Type {
        USER,
        GROUP
    }

    private Type type;
    private String id;          // uid או groupId
    private String displayName; // username או groupName

    public SearchItem() { }

    public SearchItem(Type type, String id, String displayName) {
        this.type = type;
        this.id = id;
        this.displayName = displayName;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}

