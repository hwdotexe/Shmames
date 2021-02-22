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
import net.dv8tion.jda.api.requests.restaction.MessageAction;

public class Poll {
	private String question;
	private List<String> options;
	private String pollID;
	private Date expires;
	private String messageID;
	private String channelID;
	private Lang lang;
	private boolean isActive;
	
	public Poll(MessageChannel ch, String q, List<String> o, int seconds, String id, Lang lang) {
		this.question = q;
		this.options = o;
		this.pollID = id;
		this.messageID = "";
		this.channelID = ch.getId();
		this.lang = lang;
		this.isActive = true;
		
		Calendar c = Calendar.getInstance();
    	c.setTime(new Date());
    	c.add(Calendar.SECOND, seconds);
		
    	this.expires = c.getTime();
		Message message = this.sendMessageEmbed(ch, c);

		this.schedulePollExpirationTask(message, c);
		this.populatePollVoteOptions(message);
		this.tryPinPollMessage(message);
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

	public String getTitleResults() {
		return lang.getMsg(Langs.POLL_TITLE_RESULTS);
	}

	private Message sendMessageEmbed(MessageChannel channel, Calendar expiration) {
		EmbedBuilder eBuilder = new EmbedBuilder();

		eBuilder.setAuthor(this.lang.getMsg(Langs.POLL_TITLE));
		eBuilder.setColor(Color.GREEN);
		eBuilder.setTitle(this.question);
		eBuilder.setFooter("#" + channel.getName() + " - Expires "+ Utils.getFriendlyDate(expiration)+" - #"+this.pollID, null);

		for(int i=0; i<this.options.size(); i++) {
			eBuilder.appendDescription("**"+(i+1)+"**: "+this.options.get(i)+"\n");
		}

		MessageEmbed messageEmbed = eBuilder.build();
		MessageAction messageAction = channel.sendMessage(messageEmbed);
		Message message = messageAction.complete();
		this.messageID = message.getId();

		return message;
	}

	private void schedulePollExpirationTask(Message message, Calendar expiration) {
		Timer t = new Timer();
		t.schedule(new PollTask(this, message), expiration.getTime());
	}

	private void populatePollVoteOptions(Message message) {
		for(int i=0; i<options.size(); i++) {
			message.addReaction(Utils.intToEmoji(i + 1)).queue();
		}
	}

	private void tryPinPollMessage(Message message) {
		Brain b = Shmames.getBrains().getBrain(message.getGuild().getId());

		if(b.getSettingFor(BotSettingName.PIN_POLLS).getValue().equalsIgnoreCase("true")) {
			try {
				message.pin().queue();
			}catch(Exception e) {
				message.getChannel().sendMessage(lang.getError(Errors.NO_PERMISSION_BOT, true)).queue();
			}
		}
	}
}
