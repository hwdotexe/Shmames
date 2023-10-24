package com.hadenwatne.shmames.tasks;

import com.hadenwatne.shmames.App;
import com.hadenwatne.botcore.service.types.LogType;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.GachaUser;
import com.hadenwatne.shmames.services.GachaService;
import com.hadenwatne.botcore.service.LoggingService;
import com.hadenwatne.shmames.services.RandomService;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Gives existing Gacha users a random amount of daily credit, up to a limit if not redeemed.
 */
public class GachaTask extends TimerTask {

	public GachaTask() {
		Calendar c = Calendar.getInstance();
		Timer t = new Timer();
		
    	c.setTime(new Date());
    	
    	// Run this now, and then again every 8 hours
		t.schedule(this, c.getTime(), 8 * (60 * 60 * 1000));
	}
	
	public void run() {
		for(Brain b : App.Shmames.getStorageService().getBrainController().getBrains()) {
			long hours24 = 86400000;
			long now = new Date().getTime();
			long last = b.getGachaUserCreditDate().getTime();

			if(now - last >= hours24) {
				// Give users free credits.
				for (GachaUser gu : b.getGachaUsers()) {
					// Don't give points to users who have more than a certain amount.
					if(gu.getUserPoints() < GachaService.AUTOMATIC_MAXIMUM) {
						int randomCredit = RandomService.GetRandom(15) + 1;

						gu.addUserPoints(randomCredit);
					}
				}

				b.updateGachaUserCreditDate();
			}
		}

		LoggingService.Log(LogType.SYSTEM, "Gacha Daily Credit Increase Ran");
		LoggingService.Write();
	}
}