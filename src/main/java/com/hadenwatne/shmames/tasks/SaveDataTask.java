package com.hadenwatne.shmames.tasks;

import com.hadenwatne.fornax.App;
import com.hadenwatne.fornax.service.types.LogType;
import com.hadenwatne.fornax.utility.HTTPUtility;
import com.hadenwatne.fornax.utility.models.HTTPResponse;
import com.hadenwatne.shmames.Shmames;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.MotherBrain;
import com.hadenwatne.shmames.services.RandomService;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class SaveDataTask extends TimerTask {
	private Shmames shmames;

	public SaveDataTask(Shmames shmames) {
		this.shmames = shmames;

		Calendar c = Calendar.getInstance();
		Timer t = new Timer();
		
    	c.setTime(new Date());
    	
    	// Run this now, and then again every 3 hours
		t.schedule(this, c.getTime(), 18000000);
	}
	
	public void run() {
		MotherBrain mb = shmames.getBrainController().getMotherBrain();
		String action = RandomService.GetRandomFromSet(mb.getStatuses().keySet());
		ActivityType t = mb.getStatuses().get(action);

		updateRandomSeed();
		shmames.getJDA().getPresence().setActivity(Activity.of(t, action));
		
		// Save all brains
		for(Brain b : shmames.getBrainController().getBrains()) {
			shmames.getBrainController().saveBrain(b);
		}

		shmames.getBrainController().saveMotherBrain();

		App.getLogger().Log(LogType.SYSTEM, "Autosave Task Ran");
		App.getLogger().Write();
	}

	private void updateRandomSeed() {
		HTTPResponse response = HTTPUtility.get("https://www.random.org/integers/?num=2&min=9999999&max=99999999&col=1&base=10&format=plain&rnd=new");

		if(response.responseCode() == 200) {
			String resp = response.responseObject().toString();
			resp = resp.trim();
			resp = resp.replaceAll("\n", "");
			long seed;

			try {
				seed = Long.parseLong(resp);
			} catch (Exception e) {
				seed = System.currentTimeMillis();
				App.getLogger().LogException(e);
			}

			RandomService.GetRandomObj().setSeed(seed);
		}
	}
}