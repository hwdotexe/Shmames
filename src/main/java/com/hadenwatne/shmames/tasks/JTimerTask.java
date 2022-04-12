package com.hadenwatne.shmames.tasks;

import java.util.*;

import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.services.LoggingService;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import com.hadenwatne.shmames.Shmames;

public class JTimerTask {
	private final String userMention;
	private final long channelID;
	private final String messageID;
	private final String message;
	private Date execTime;
	
	public JTimerTask(String mention, long channelID, String messageID, int seconds, String msg) {
		Calendar c = Calendar.getInstance();
		Timer t = new Timer();

		c.setTime(new Date());
		c.add(Calendar.SECOND, seconds);

		this.message = msg != null ? msg : "";
		this.userMention = mention;
    	this.channelID = channelID;
    	this.messageID = messageID;
		this.execTime = c.getTime();

		t.schedule(new TimerTask() {
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

		t.schedule(new TimerTask() {
			@Override
			public void run() {
				runTimer();
			}
		}, execTime);
	}

	public void runTimer() {
		try {
			String m = ":alarm_clock: (" + userMention + "): The timer you set is finished!";

			if (message.length() > 0)
				m = m + "\n> " + message;

			TextChannel tc = Shmames.getJDA().getTextChannelById(channelID);

			if (tc != null) {
				Message originMessage = null;

				if(this.messageID != null) {
					try {
						originMessage = tc.retrieveMessageById(this.messageID).complete();
					} catch (Exception ignored) {}
				}

				if(originMessage != null) {
					final String mFinal = m;

					originMessage.reply(mFinal).queue(success -> {}, error -> tc.sendMessage(mFinal).queue());
				} else {
					tc.sendMessage(m).queue();
				}

				String id = tc.getGuild().getId();
				Brain b = Shmames.getBrains().getBrain(id);

				b.getTimers().remove(this);
			}
		}catch (Exception e){
			LoggingService.LogException(e);
		}
	}
}