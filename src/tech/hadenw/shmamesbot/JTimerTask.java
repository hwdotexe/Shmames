package tech.hadenw.shmamesbot;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public class JTimerTask extends TimerTask{
	private User u;
	private MessageChannel msgch;
	
	public JTimerTask(User user, MessageChannel ch, int minutes) {
		Calendar c = Calendar.getInstance();
		Timer t = new Timer();
    	c.setTime(new Date());
    	c.add(Calendar.MINUTE, minutes);
    	
    	u = user;
    	msgch = ch;
    	
		t.schedule(this, c.getTime());
	}
	
	public void run() {
		msgch.sendMessage(":alarm_clock: ("+u.getAsMention()+"): The timer you set is finished!").queue();
	}
}