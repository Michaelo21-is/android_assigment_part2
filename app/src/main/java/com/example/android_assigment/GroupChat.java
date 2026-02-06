package com.example.android_assigment;

import java.util.List;

public class GroupChat {
    private GameTopic gameTopic;

    private Integer sumOfMember;

    private String groupName;
    private List<String> managementId;

    private List<String> membersID;

    private List<Messege> ListOfMesseges;

    public GroupChat(GameTopic gameTopic, Integer sumOfMember, String groupName, List<String> membersID, List<Messege> ListOfMesseges) {
        this.gameTopic = gameTopic;
        this.sumOfMember = sumOfMember;
        this.groupName = groupName;
        this.membersID = membersID;
        this.ListOfMesseges = ListOfMesseges;
    }
}
