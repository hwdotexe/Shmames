package com.hadenwatne.shmames.models;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

public class RoleMessage {
    private String roleMessageID;
    private String channelID;
    private String messageID;
    private String infoMessage;
    @SerializedName(value = "emoteRoleMap", alternate = "roleEmoteMap")
    private HashMap<String, String> emoteRoleMap;
    private HashMap<String, String> emoteTextMap;

    public RoleMessage(String postID, String channelID, String messageID, String infoMessage) {
        this.roleMessageID = postID;
        this.channelID = channelID;
        this.messageID = messageID;
        this.infoMessage = infoMessage;
        this.emoteRoleMap = new HashMap<>();
        this.emoteTextMap = new HashMap<>();
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

    public HashMap<String, String> getEmoteRoleMap() {
        return emoteRoleMap;
    }

    public HashMap<String, String> getEmoteTextMap() {
        if(this.emoteTextMap == null) {
            this.emoteTextMap = new HashMap<>();

            for(String key : this.emoteRoleMap.keySet()) {
                this.emoteTextMap.put(key, "Default description");
            }
        }

        return emoteTextMap;
    }
}
