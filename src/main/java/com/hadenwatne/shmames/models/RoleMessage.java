package com.hadenwatne.shmames.models;

import java.util.HashMap;

public class RoleMessage {
    private String roleMessageID;
    private String channelID;
    private String messageID;
    private String infoMessage;
    private HashMap<String, String> roleEmoteMap;

    public RoleMessage(String postID, String channelID, String messageID, String infoMessage) {
        this.roleMessageID = postID;
        this.channelID = channelID;
        this.messageID = messageID;
        this.infoMessage = infoMessage;
        this.roleEmoteMap = new HashMap<>();
    }

    public String getRoleMessageID() {
        return roleMessageID;
    }

    public String getChannelID() {
        return channelID;
    }

    public String getMessageID() {
        return messageID;
    }

    public String getInfoMessage() {
        return infoMessage;
    }

    public HashMap<String, String> getRoleEmoteMap() {
        return roleEmoteMap;
    }
}
