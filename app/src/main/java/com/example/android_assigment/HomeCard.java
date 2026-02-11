package com.example.android_assigment;

public class HomeCard {

    private String groupId;
    private String groupName;
    private String lastMessage;

    public HomeCard() {
        // נדרש אם תרצה להשתמש בזה מול Firebase או ספריות אחרות
    }

    public HomeCard(String groupId, String groupName, String lastMessage) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.lastMessage = lastMessage;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
