package tech.hadenw.shmamesbot;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import tech.hadenw.shmamesbot.brain.Brain;

public class CooldownTask extends TimerTask{
	private Brain b;
	
	public CooldownTask(Brain brain) {
		b=brain;
		
		Calendar c = Calendar.getInstance();
    	c.setTime(new Date());
    	c.add(Calendar.MINUTE, 2);
    	
    	b.setReportCooldown(true);
    	
    	Timer t = new Timer();
    	t.schedule(this, c.getTime());
	}
	
	public void run() {
		b.setReportCooldown(false);
		this.cancel();
	}
}