package com.hadenwatne.discordbot.tasks;

import java.util.*;

import com.hadenwatne.discordbot.storage.Brain;
import com.hadenwatne.discordbot.storage.LogType;
import com.hadenwatne.discordbot.storage.ShmamesLogger;
import net.dv8tion.jda.api.entities.TextChannel;
import com.hadenwatne.discordbot.Shmames;

public class JTimerTask {
	private String userMention;
	private long channelID;
	private String message;
	private Date execTime;
	
	public JTimerTask(String mention, long ch, int seconds, String msg) {
		Calendar c = Calendar.getInstance();
		Timer t = new Timer();

		c.setTime(new Date());
		c.add(Calendar.SECOND, seconds);

		message = msg;
    	userMention = mention;
    	channelID = ch;
    	execTime = c.getTime();

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

	private void runTimer() {
		try {
			String m = ":alarm_clock: (" + userMention + "): The timer you set is finished!";

			if (message.length() > 0)
				m = m + "\n> " + message;

			TextChannel tc = Shmames.getJDA().getTextChannelById(channelID);

			if (tc != null) {
				tc.sendMessage(m).queue();

				String id = tc.getGuild().getId();
				Brain b = Shmames.getBrains().getBrain(id);

				b.getTimers().remove(this);
			}
		}catch (Exception e){
			ShmamesLogger.logException(e);
		}
	}
}