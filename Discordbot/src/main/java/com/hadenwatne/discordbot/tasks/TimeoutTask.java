package com.hadenwatne.discordbot.tasks;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.hadenwatne.discordbot.storage.Brain;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.MessageChannel;
import com.hadenwatne.discordbot.Shmames;

public class TimeoutTask extends TimerTask{
	private String msg;
	private MessageChannel msgch;
	private Brain b;
	
	public TimeoutTask(String returnMsg, MessageChannel ch, Brain brain) {
		Calendar c = Calendar.getInstance();
		b=brain;
		Timer t = new Timer();
    	c.setTime(new Date());
    	c.add(Calendar.SECOND, 30);
    	
		t.schedule(this, c.getTime());
		
		Shmames.getJDA().getPresence().setStatus(OnlineStatus.INVISIBLE);
		b.setTimeout(true);
		
		msg = returnMsg;
		msgch = ch;
	}
	
	public void run() {
		Shmames.getJDA().getPresence().setStatus(OnlineStatus.ONLINE);
		b.setTimeout(false);
		
		msgch.sendMessage(msg).queue();
	}
}