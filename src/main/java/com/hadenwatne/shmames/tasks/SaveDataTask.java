package com.hadenwatne.shmames.tasks;

import java.util.*;

import com.hadenwatne.shmames.enums.HTTPVerb;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.enums.LogType;
import com.hadenwatne.shmames.models.data.MotherBrain;
import com.hadenwatne.shmames.services.LoggingService;
import com.hadenwatne.shmames.services.HTTPService;
import com.hadenwatne.shmames.services.RandomService;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import com.hadenwatne.shmames.Shmames;

/**
 * Saves data objects to disk at a regular interval, and changes the bot's status for fun.
 */
public class SaveDataTask extends TimerTask{
	
	public SaveDataTask() {
		Calendar c = Calendar.getInstance();
		Timer t = new Timer();
		
    	c.setTime(new Date());
    	
    	// Run this now, and then again every 3 hours
		t.schedule(this, c.getTime(), 18000000);
	}
	
	public void run() {
		MotherBrain mb = Shmames.getBrains().getMotherBrain();
		String action = RandomService.GetRandomHashMap(mb.getStatuses().keySet());
		ActivityType t = mb.getStatuses().get(action);

		updateRandomSeed();
		Shmames.getJDA().getPresence().setActivity(Activity.of(t, action));
		
		// Save all brains
		for(Brain b : Shmames.getBrains().getBrains()) {
			Shmames.getBrains().saveBrain(b);
		}

		Shmames.getBrains().saveMotherBrain();

		LoggingService.Log(LogType.SYSTEM, "Autosave Task Ran");
		LoggingService.Write();
	}

	private void updateRandomSeed() {
		String resp = HTTPService.SendHTTPReq(HTTPVerb.GET, "https://www.random.org/integers/?num=2&min=9999999&max=99999999&col=1&base=10&format=plain&rnd=new", null);

		if(resp != null) {
			resp = resp.trim();
			resp = resp.replaceAll("\n", "");
			long seed;

			try {
				seed = Long.parseLong(resp);
			} catch (Exception e) {
				seed = System.currentTimeMillis();
				LoggingService.LogException(e);
			}

			RandomService.GetRandomObj().setSeed(seed);
		}
	}
}