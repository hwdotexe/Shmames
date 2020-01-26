package tech.hadenw.discordbot.tasks;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import tech.hadenw.discordbot.brain.Brain;

public class JinpingTask extends TimerTask{
	private Brain b;
	
	public JinpingTask(Brain brain) {
		Calendar c = Calendar.getInstance();
		Timer t = new Timer();
    	c.setTime(new Date());
    	c.add(Calendar.MINUTE, 1);
		t.schedule(this, c.getTime());
		
		b = brain;
		b.setJinping(true);
	}
	
	public void run() {
		b.setJinping(false);
		this.cancel();
	}
}