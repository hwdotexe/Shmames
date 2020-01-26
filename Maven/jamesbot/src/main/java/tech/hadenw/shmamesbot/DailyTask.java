package tech.hadenw.shmamesbot;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import tech.hadenw.shmamesbot.brain.Brain;
import tech.hadenw.shmamesbot.brain.MotherBrain;

/**
 * Runs this code at a daily interval. This is useful for routine tasks and
 * necessary cosmetic changes.
 */
public class DailyTask extends TimerTask{
	
	public DailyTask() {
		Calendar c = Calendar.getInstance();
		Timer t = new Timer();
		
    	c.setTime(new Date());
    	
    	// Run this now, and then again every 24 hours
		t.schedule(this, c.getTime(), 86400000);
	}
	
	public void run() {
		MotherBrain mb = Shmames.getBrains().getMotherBrain();
		String action = Utils.randomItem(mb.getStatuses().keySet());
		ActivityType t = mb.getStatuses().get(action);
		Shmames.getJDA().getPresence().setActivity(Activity.of(t, action));
		
		// Save all brains
		for(Brain b : Shmames.getBrains().getBrains()) {
			Shmames.getBrains().saveBrain(b);
			Shmames.getBrains().saveMotherBrain();
		}
	}
}