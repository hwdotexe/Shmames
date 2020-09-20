package com.hadenwatne.discordbot.tasks;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.hadenwatne.discordbot.storage.Brain;
import net.dv8tion.jda.api.entities.TextChannel;
import com.hadenwatne.discordbot.Shmames;

public class JTimerTask {
	private String userMention;
	private long channelID;
	private String message;
	private Date execTime;
	
	public JTimerTask(String mention, long ch, int time, int interval, String msg) {
		Calendar c = Calendar.getInstance();
		message = msg;

    	c.setTime(new Date());
    	
    	switch(interval) {
    	case 1:
    		c.add(Calendar.HOUR, 24*time);
    		break;
    	case 2:
    		c.add(Calendar.HOUR, time);
    		break;
    	case 3:
    		c.add(Calendar.MINUTE, time);
    		break;
    	case 4:
    		c.add(Calendar.SECOND, time);
    		break;
    	default:
    		c.add(Calendar.SECOND, 5);
    	}
    	
    	userMention = mention;
    	channelID = ch;
    	execTime = c.getTime();
		Timer t = new Timer();

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
			e.printStackTrace();
		}
	}
}