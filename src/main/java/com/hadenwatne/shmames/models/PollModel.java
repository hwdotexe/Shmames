package com.hadenwatne.shmames.models;

import net.dv8tion.jda.api.EmbedBuilder;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class PollModel {
	private final String question;
	private String channelID;
	private String messageID;
	private final String authorID;
	private final Calendar expires;
	private final List<String> options;
	private final HashMap<Long, List<Integer>> votes;
	private boolean isActive;
	private boolean hasStarted;
	private final boolean multiple;

	private transient EmbedBuilder cachedEmbedBuilder;
	
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
		
    	this.expires = calendar;
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

	public Calendar getExpires() {
		return expires;
	}

	public HashMap<Long, List<Integer>> getVotes() {
		return votes;
	}
}
