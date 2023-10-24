package com.hadenwatne.shmames;

import com.hadenwatne.shmames.models.PollModel;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.tasks.AlarmTask;
import com.mongodb.client.MongoCursor;

import java.util.ArrayList;
import java.util.List;

public class BrainController {
	private final Shmames shmames;
	private final List<Brain> brains;
	private final String BRAIN_TABLE = "brains";

	public BrainController(Shmames shmames) {
		this.shmames = shmames;
		brains = new ArrayList<>();

		loadServerBrains();
	}

	private void loadServerBrains(){
		try (MongoCursor<Brain> cursor = shmames.getBotDataStorageService().getDatabaseService().readTable(Brain.class, BRAIN_TABLE)) {
			while(cursor.hasNext()) {
				Brain brain = cursor.next();

				if(shmames.getJDA().getGuildById(brain.getGuildID()) != null) {
					this.brains.add(brain);
				}

				// TODO if null, we aren't a member - should we delete it?
			}
		}

		for(Brain brain : brains) {
			// Activate any threads that this brain may have had.
			if(!brain.getActivePolls().isEmpty()) {
				for(PollModel pollModel : brain.getActivePolls()) {
					pollModel.startPollInstrumentation();
				}
			}

			if(!brain.getTimers().isEmpty()){
				for(AlarmTask alarmTask : brain.getTimers()){
					alarmTask.rescheduleTimer();
				}
			}

			// Manually reset any cooldowns that don't have a task set up.
			if(brain.getReportCooldown()) {
				brain.setReportCooldown(false);
			}

			// Validate settings.
			shmames.getSettingsService().validateBrainSettings(brain);
		}
	}

	public List<Brain> getBrains() {
		return brains;
	}

	public Brain getBrain(String guildID) {
		for (Brain b : brains) {
			if (b.getGuildID().equals(guildID)) {
				return b;
			}
		}

		Brain b = new Brain(guildID);
		b.getSettings().addAll(shmames.getSettingsService().getDefaultSettings());
		brains.add(b);

		return b;
	}

	public void saveBrain(Brain brain) {
		shmames.getBotDataStorageService().getDatabaseService().updateRecord(Brain.class, BRAIN_TABLE, "guildID", brain.getGuildID(), brain);
	}
}
