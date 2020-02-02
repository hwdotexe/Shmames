package tech.hadenw.discordbot.tasks;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class JTimerTask extends TimerTask{
	private User u;
	private MessageChannel msgch;
	private String message;
	
	public JTimerTask(User user, MessageChannel ch, int time, int interval, String msg) {
		Calendar c = Calendar.getInstance();
		Timer t = new Timer();
    	c.setTime(new Date());
    	message = msg;
    	
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
    	
    	u = user;
    	msgch = ch;
    	
		t.schedule(this, c.getTime());
	}
	
	public void run() {
		String m = ":alarm_clock: ("+u.getAsMention()+"): The timer you set is finished!";
		if(message.length() > 0)
			m = m+"\n_"+message+"_";
		
		msgch.sendMessage(m).queue();
	}
}