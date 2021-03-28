package com.hadenwatne.shmames.models;

import java.awt.Color;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;

import com.hadenwatne.shmames.Shmames;
import com.hadenwatne.shmames.Utils;
import com.hadenwatne.shmames.enums.BotSettingName;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.enums.Langs;
import com.hadenwatne.shmames.tasks.PollTask;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

public class PollModel {
	private final String question;
	private final String pollID;
	private String messageID;
	private final String channelID;
	private final Date expires;
	private final List<String> options;
	private boolean isActive;
	
	public PollModel(TextChannel ch, String q, List<String> o, int seconds, String pollID) {
		this.question = q;
		this.options = o;
		this.pollID = pollID;
		this.channelID = ch.getId();
		this.messageID = null;
		this.isActive = true;
		
		Calendar c = Calendar.getInstance();
    	c.setTime(new Date());
    	c.add(Calendar.SECOND, seconds);
		
    	this.expires = c.getTime();

		this.schedulePollExpirationTask(this.expires);
	}

	public boolean isActive() {
		return isActive;
	}
	
	public void setActive(boolean active) {
		isActive = active;
	}
	
	public String getMessageID() {
		return messageID;
	}

	public void setMessageID(String messageID) {
		this.messageID = messageID;
	}
	
	public String getChannelID() {
		return channelID;
	}
	
	public Date getExpiration() {
		return expires;
	}
	
	public String getQuestion() {
		return question;
	}
	
	public List<String> getOptions(){
		return options;
	}
	
	public String getID() {
		return pollID;
	}

	private void schedulePollExpirationTask(Date expiration) {
		Timer t = new Timer();
		t.schedule(new PollTask(this), expiration);
	}
}