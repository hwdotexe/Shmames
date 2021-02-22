package com.hadenwatne.shmames.tasks;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.hadenwatne.shmames.models.Brain;

public class ReportCooldownTask extends TimerTask{
	private Brain b;
	
	public ReportCooldownTask(Brain brain) {
		b = brain;
		
		Calendar c = Calendar.getInstance();
    	c.setTime(new Date());
    	c.add(Calendar.MINUTE, 5);
    	
    	b.setReportCooldown(true);
    	
    	Timer t = new Timer();
    	t.schedule(this, c.getTime());
	}
	
	public void run() {
		b.setReportCooldown(false);
		this.cancel();
	}
}