package com.hadenwatne.shmames.tasks;

import com.hadenwatne.shmames.App;
import com.hadenwatne.shmames.enums.HTTPVerb;
import com.hadenwatne.fornax.service.types.LogType;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.MotherBrain;
import com.hadenwatne.shmames.services.HTTPService;
import com.hadenwatne.fornax.service.LoggingService;
import com.hadenwatne.shmames.services.RandomService;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Saves data objects to disk at a regular interval, and changes the bot's status for fun.
 */
public class SaveDataTask extends TimerTask {
	
	public SaveDataTask() {
		Calendar c = Calendar.getInstance();
		Timer t = new Timer();
		
    	c.setTime(new Date());
    	
    	// Run this now, and then again every 3 hours
		t.schedule(this, c.getTime(), 18000000);
	}
	
	public void run() {
		MotherBrain mb = App.Shmames.getStorageService().getMotherBrain();
		String action = RandomService.GetRandomFromSet(mb.getStatuses().keySet());
		ActivityType t = mb.getStatuses().get(action);

		updateRandomSeed();
		App.Shmames.getJDA().getPresence().setActivity(Activity.of(t, action));
		
		// Save all brains
		for(Brain b : App.Shmames.getStorageService().getBrainController().getBrains()) {
			App.Shmames.getStorageService().getBrainController().saveBrain(b);
		}

		App.Shmames.getStorageService().getBrainController().saveMotherBrain();

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