package com.example.android_assigment;

import java.util.List;

public class GroupChat {

    private GameTopic gameTopic;
    private Integer sumOfMember;
    private String groupDescription;
    private String groupName;

    private List<String> managementId; // מי מנהלים (למשל יוצר הקבוצה)
    private List<String> membersID;    // מי חברים
    private List<Messege> listOfMesseges;

    // ✅ חובה ל-Firebase
    public GroupChat() {}

    // ✅ קונסטרקטור נוח ליצירה
    public GroupChat(GameTopic gameTopic,
                     String groupName,
                     String groupDescription,
                     Integer sumOfMember,
                     List<String> managementId,
                     List<String> membersID,
                     List<Messege> listOfMesseges) {

        this.gameTopic = gameTopic;
        this.groupName = groupName;
        this.groupDescription = groupDescription;
        this.sumOfMember = sumOfMember;
        this.managementId = managementId;
        this.membersID = membersID;
        this.listOfMesseges = listOfMesseges;
    }

    public GameTopic getGameTopic() { return gameTopic; }
    public void setGameTopic(GameTopic gameTopic) { this.gameTopic = gameTopic; }

    public Integer getSumOfMember() { return sumOfMember; }
    public void setSumOfMember(Integer sumOfMember) { this.sumOfMember = sumOfMember; }

    public String getGroupDescription() { return groupDescription; }
    public void setGroupDescription(String groupDescription) { this.groupDescription = groupDescription; }

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

    public List<String> getManagementId() { return managementId; }
    public void setManagementId(List<String> managementId) { this.managementId = managementId; }

    public List<String> getMembersID() { return membersID; }
    public void setMembersID(List<String> membersID) { this.membersID = membersID; }

    public List<Messege> getListOfMesseges() { return listOfMesseges; }
    public void setListOfMesseges(List<Messege> listOfMesseges) { this.listOfMesseges = listOfMesseges; }
}
