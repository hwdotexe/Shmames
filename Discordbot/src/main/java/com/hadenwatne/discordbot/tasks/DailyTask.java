package com.hadenwatne.discordbot.tasks;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.hadenwatne.discordbot.storage.Brain;
import com.hadenwatne.discordbot.storage.MotherBrain;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import com.hadenwatne.discordbot.Shmames;
import com.hadenwatne.discordbot.Utils;

/**
 * Runs this code at a daily interval. This is useful for routine tasks and
 * necessary cosmetic changes.
 */
public class DailyTask extends TimerTask{
	
	public DailyTask() {
		Calendar c = Calendar.getInstance();
		Timer t = new Timer();
		
    	c.setTime(new Date());
    	
    	// Run this now, and then again every 3 hours
		t.schedule(this, c.getTime(), 18000000);
	}
	
	public void run() {
		MotherBrain mb = Shmames.getBrains().getMotherBrain();
		String action = Utils.getRandomHashMap(mb.getStatuses().keySet());
		ActivityType t = mb.getStatuses().get(action);
		Shmames.getJDA().getPresence().setActivity(Activity.of(t, action));
		
		// Save all brains
		for(Brain b : Shmames.getBrains().getBrains()) {
			Shmames.getBrains().saveBrain(b);
		}

		Shmames.getBrains().saveMotherBrain();

		System.out.println("[ AUTOSAVE TASK RAN ]");
	}
}