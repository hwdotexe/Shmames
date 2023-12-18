package com.hadenwatne.shmames.models;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class PollModel {
	public String question;
	public String channelID;
	public String messageID;
	public String authorID;
	public Date expires;
	public List<String> options;
	public HashMap<String, List<Integer>> votes;
	public boolean isActive;
	public boolean hasStarted;
	public boolean multiple;

	public PollModel(){}
	
	public PollModel(String authorID, String q, List<String> o, int seconds, boolean multiple) {
		this.question = q;
		this.options = o;
		this.authorID = authorID;
		this.isActive = true;
		this.hasStarted = false;
		this.votes = new HashMap<>();
		this.multiple = multiple;
		
		Calendar calendar = Calendar.getInstance();
    	calendar.setTime(new Date());
    	calendar.add(Calendar.SECOND, seconds);
		
    	this.expires = calendar.getTime();
	}

	public boolean isActive() {
		return isActive;
	}

	public boolean isMultiple() {
		return multiple;
	}

	public void setActive(boolean active) {
		isActive = active;
	}

	public boolean hasStarted() {
		return this.hasStarted;
	}

	public void setHasStarted(boolean hasStarted) {
		this.hasStarted = hasStarted;
	}

	public String getAuthorID() {
		return this.authorID;
	}

	public void setChannelID(String channelID) {
		this.channelID = channelID;
	}

	public void setMessageID(String messageID) {
		this.messageID = messageID;
	}

	public String getChannelID() {
		return channelID;
	}

	public String getMessageID() {
		return messageID;
	}

	public String getQuestion() {
		return question;
	}

	public List<String> getOptions(){
		return options;
	}

	public Date getExpires() {
		return expires;
	}

	public HashMap<String, List<Integer>> getVotes() {
		return votes;
	}
}
