package com.hadenwatne.shmames.tasks;

import com.hadenwatne.shmames.App;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.factories.EmbedFactory;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.services.LoggingService;
import com.hadenwatne.shmames.services.MessageService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

public class AlarmTask {
	private long channelID;
	private final String messageID;
	private final String userMessage;
	private final String timerUpMessage;
	private Date execTime;
	
	public AlarmTask(long channelID, String messageID, int seconds, String userMessage, String timerUpMesage) {
		Calendar c = Calendar.getInstance();
		Timer t = new Timer();

		c.setTime(new Date());
		c.add(Calendar.SECOND, seconds);

		this.channelID = channelID;
		this.userMessage = userMessage != null ? userMessage : "";
    	this.messageID = messageID;
		this.execTime = c.getTime();
		this.timerUpMessage = timerUpMesage;

		t.schedule(new java.util.TimerTask() {
			@Override
			public void run() {
				runTimer();
			}
		}, execTime);
	}

	public void rescheduleTimer(){
		Timer t = new Timer();

		// Time was in the past; give the bot time to load, and then finish this timer.
		if(execTime.getTime() <= new Date().getTime()){
			Calendar c = Calendar.getInstance();
			c.setTime(new Date());

			c.add(Calendar.SECOND, 10);

			execTime = c.getTime();
		}

		t.schedule(new java.util.TimerTask() {
			@Override
			public void run() {
				runTimer();
			}
		}, execTime);
	}

	public void runTimer() {
		try {
			EmbedBuilder response = EmbedFactory.GetEmbed(EmbedType.INFO, "timer");
			TextChannel channel = App.Shmames.getJDA().getTextChannelById(channelID);

			response.setDescription(this.timerUpMessage);

			if(userMessage != null && userMessage.length() > 0) {
				response.addField("Memo", userMessage, false);
			}

			if (channel != null) {
				Message originMessage = null;

				if(this.messageID != null) {
					try {
						originMessage = channel.retrieveMessageById(this.messageID).complete();
					} catch (Exception ignored) {}
				}

				if(originMessage != null) {
					MessageService.ReplyToMessage(originMessage, response, true);
				} else {
					MessageService.SendMessage(channel, response, true);
				}

				String serverID = channel.getGuild().getId();
				Brain brain = App.Shmames.getStorageService().getBrain(serverID);

				brain.getTimers().remove(this);
			}
		}catch (Exception e){
			LoggingService.LogException(e);
		}
	}
}