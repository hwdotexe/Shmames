package com.hadenwatne.shmames.models;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class AlarmModel {
    public String channelID;
    public String messageID;
    public String authorID;
    public String userMessage;
    public Date execTime;

    public AlarmModel() {}

    public AlarmModel(String channelID, String messageID, String userID, int seconds, String userMessage) {
        this.channelID = channelID;
        this.messageID = messageID;
        this.authorID = userID;
        this.userMessage = userMessage;

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.SECOND, seconds);

        this.execTime = c.getTime();
    }

    public String getAuthorID() {
        return this.authorID;
    }

    public String getChannelID() {
        return channelID;
    }

    public void setChannelID(String channelID) {
        this.channelID = channelID;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getUserMessage() {
        return Objects.requireNonNullElse(userMessage, "");
    }

    public Date getExecTime() {
        return execTime;
    }
}
