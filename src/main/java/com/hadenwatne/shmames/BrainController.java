package com.hadenwatne.shmames;

import com.hadenwatne.shmames.models.PollModel;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.MotherBrain;
import com.hadenwatne.shmames.tasks.PollTask;
import com.mongodb.client.MongoCursor;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class BrainController {
	private final Shmames shmames;
	private final List<Brain> brains;
	private MotherBrain motherBrain;
	private final String BRAIN_TABLE = "brains";
	private final String MOTHER_BRAIN_TABLE = "motherbrain";

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

		motherBrain = shmames.getBotDataStorageService().getDatabaseService().readOne(MotherBrain.class, MOTHER_BRAIN_TABLE, "botName", shmames.getBotName());

		// TODO this isn't loading properly from DB?
		if(motherBrain == null) {
			motherBrain = new MotherBrain(shmames.getBotName());

			insertMotherBrain();
		}

		for(Brain brain : brains) {
			// Activate any threads that this brain may have had.
			for(PollModel poll : brain.getActivePolls()) {
				Timer t = new Timer();
				t.schedule(new PollTask(poll, brain, shmames), poll.getExpires());
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

		insertBrain(b);

		return b;
	}

	public MotherBrain getMotherBrain() {
		return motherBrain;
	}

	public void insertBrain(Brain brain) {
		shmames.getBotDataStorageService().getDatabaseService().insertRecord(Brain.class, BRAIN_TABLE, brain);
	}

	public void saveBrain(Brain brain) {
		shmames.getBotDataStorageService().getDatabaseService().updateRecord(Brain.class, BRAIN_TABLE, "guildID", brain.getGuildID(), brain);
	}

	public void insertMotherBrain() {
		shmames.getBotDataStorageService().getDatabaseService().insertRecord(MotherBrain.class, MOTHER_BRAIN_TABLE, motherBrain);
	}

	public void saveMotherBrain() {
		shmames.getBotDataStorageService().getDatabaseService().updateRecord(MotherBrain.class, MOTHER_BRAIN_TABLE, "botName", shmames.getBotName(), motherBrain);
	}
}
