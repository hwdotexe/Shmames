package tech.hadenw.shmamesbot;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.MessageChannel;

public class TimeoutTask extends TimerTask{
	private String msg;
	private MessageChannel msgch;
	
	public TimeoutTask(String returnMsg, MessageChannel ch) {
		Calendar c = Calendar.getInstance();
		Timer t = new Timer();
    	c.setTime(new Date());
    	c.add(Calendar.SECOND, 30);
    	
		t.schedule(this, c.getTime());
		
		Shmames.getJDA().getPresence().setStatus(OnlineStatus.INVISIBLE);
		Shmames.setIsOnTimeout(true);
		
		msg = returnMsg;
		msgch = ch;
	}
	
	public void run() {
		Shmames.getJDA().getPresence().setStatus(OnlineStatus.ONLINE);
		Shmames.setIsOnTimeout(false);
		
		msgch.sendMessage(msg).queue();
	}
}