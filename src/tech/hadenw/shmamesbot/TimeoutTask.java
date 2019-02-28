package tech.hadenw.shmamesbot;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import net.dv8tion.jda.core.OnlineStatus;

public class TimeoutTask extends TimerTask{
	public TimeoutTask() {
		Calendar c = Calendar.getInstance();
		Timer t = new Timer();
    	c.setTime(new Date());
    	c.add(Calendar.SECOND, 30);
    	
		t.schedule(this, c.getTime());
		
		Shmames.getJDA().getPresence().setStatus(OnlineStatus.INVISIBLE);
		Shmames.setIsOnTimeout(true);
	}
	
	public void run() {
		Shmames.getJDA().getPresence().setStatus(OnlineStatus.ONLINE);
		Shmames.setIsOnTimeout(false);
	}
}