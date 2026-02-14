package com.example.android_assigment;

public class HomeCard {

    private String groupId;   // groupId לקבוצה, או chatId לצ'אט עם חבר
    private String groupName; // שם קבוצה או שם החבר (תצוגה)
    private String lastMessage;
    
    private String time;
    private boolean isGroup;
    private String friendId;   // רק לצ'אט עם חבר: ה-uid של החבר

    public HomeCard() {
        // נדרש אם תרצה להשתמש בזה מול Firebase או ספריות אחרות
    }

    /** כרטיס לקבוצה */
    public HomeCard(String groupId, String groupName, String lastMessage) {
        this(groupId, groupName, lastMessage, null);
    }

    /** כרטיס לקבוצה עם זמן (זמן הודעה אחרונה) */
    public HomeCard(String groupId, String groupName, String lastMessage, String time) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.lastMessage = lastMessage;
        this.time = time;
        this.isGroup = true;
        this.friendId = null;
    }

    /** כרטיס לצ'אט עם חבר */
    public HomeCard(String chatId, String friendDisplayName, String lastMessage, String friendId, String time) {
        this.groupId = chatId;
        this.groupName = friendDisplayName;
        this.lastMessage = lastMessage;
        this.time = time;
        this.isGroup = false;
        this.friendId = friendId;
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

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
