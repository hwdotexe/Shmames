package com.hadenwatne.shmames.tasks;

import com.hadenwatne.shmames.App;
import com.hadenwatne.shmames.enums.GachaRarity;
import com.hadenwatne.shmames.enums.LogType;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.GachaCharacter;
import com.hadenwatne.shmames.models.data.GachaUser;
import com.hadenwatne.shmames.services.GachaService;
import com.hadenwatne.shmames.services.LoggingService;
import com.hadenwatne.shmames.services.RandomService;

import java.util.*;

/**
 * Gives existing Gacha users a random amount of daily credit, up to a limit if not redeemed, and adjusts the banner.
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

				// Adjust the daily banner.
				List<String> banner = b.getGachaBanner();

				banner.clear();

				// Create a banner.
				List<GachaCharacter> poolVR = new ArrayList<>();
				List<GachaCharacter> poolL = new ArrayList<>();

				for (GachaCharacter gc : b.getGachaCharacters()) {
					if (gc.getGachaCharacterRarity() == GachaRarity.VERY_RARE) {
						poolVR.add(gc);
					} else if (gc.getGachaCharacterRarity() == GachaRarity.LEGENDARY) {
						poolL.add(gc);
					}
				}

				// Select 1 Legendary character.
				if (poolL.size() >= 1) {
					GachaCharacter r = RandomService.GetRandomObjectFromList(poolL);

					banner.add(r.getGachaCharacterID());
				}

				// Select 1 Very Rare character.
				if (poolVR.size() >= 1) {
					for (int i = 0; i < 1; i++) {
						GachaCharacter r = RandomService.GetRandomObjectFromList(poolVR);

						banner.add(r.getGachaCharacterID());
						poolVR.remove(r);
					}
				}

				b.updateGachaUserCreditDate();
			}
		}

		LoggingService.Log(LogType.SYSTEM, "Gacha Daily Credit Increase Ran");
		LoggingService.Write();
	}
}