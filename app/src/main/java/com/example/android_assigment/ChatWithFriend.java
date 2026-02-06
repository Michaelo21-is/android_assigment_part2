package com.example.android_assigment;

import java.util.List;

public class ChatWithFriend {
    private String friendId;
    private String userId;
    private List<Messege> ListOfMesseges;
    private String ChatId;
    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<Messege> getListOfMesseges() {
        return ListOfMesseges;
    }

    public void setListOfMesseges(List<Messege> listOfMesseges) {
        ListOfMesseges = listOfMesseges;
    }

    public ChatWithFriend(String friendId, String userId, List<Messege> listOfMesseges) {
        this.friendId = friendId;
        this.userId = userId;
        ListOfMesseges = listOfMesseges;
    }
}
