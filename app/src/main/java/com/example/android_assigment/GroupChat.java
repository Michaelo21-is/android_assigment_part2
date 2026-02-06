package com.example.android_assigment;

import java.util.List;

public class GroupChat {
    private GameTopic gameTopic;

    private Integer sumOfMember;

    private String groupName;
    private List<String> managementId;

    private List<String> membersID;

    private List<Messege> ListOfMesseges;

    public GameTopic getGameTopic() {
        return gameTopic;
    }

    public void setGameTopic(GameTopic gameTopic) {
        this.gameTopic = gameTopic;
    }

    public Integer getSumOfMember() {
        return sumOfMember;
    }

    public void setSumOfMember(Integer sumOfMember) {
        this.sumOfMember = sumOfMember;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<String> getManagementId() {
        return managementId;
    }

    public void setManagementId(List<String> managementId) {
        this.managementId = managementId;
    }

    public List<String> getMembersID() {
        return membersID;
    }

    public void setMembersID(List<String> membersID) {
        this.membersID = membersID;
    }

    public List<Messege> getListOfMesseges() {
        return ListOfMesseges;
    }

    public void setListOfMesseges(List<Messege> listOfMesseges) {
        ListOfMesseges = listOfMesseges;
    }

    public GroupChat(GameTopic gameTopic, Integer sumOfMember, String groupName, List<String> membersID, List<Messege> ListOfMesseges) {
        this.gameTopic = gameTopic;
        this.sumOfMember = sumOfMember;
        this.groupName = groupName;
        this.membersID = membersID;
        this.ListOfMesseges = ListOfMesseges;
    }
}
