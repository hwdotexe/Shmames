package com.hadenwatne.shmames;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hadenwatne.shmames.enums.BotSettingName;
import com.hadenwatne.shmames.enums.BotSettingType;
import com.hadenwatne.shmames.models.PollModel;
import com.hadenwatne.shmames.models.data.*;
import com.hadenwatne.shmames.services.FileService;
import com.hadenwatne.shmames.tasks.AlarmTask;
import com.mongodb.client.MongoCursor;
import net.dv8tion.jda.api.entities.Role;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BrainController {
	private Shmames shmames;
	private final List<Brain> brains;
	private StorytimeStories stories;
	private HangmanDictionaries dictionaries;
	private final String BRAIN_TABLE = "brains";

	public BrainController(Shmames shmames) {
		this.shmames = shmames;
		brains = new ArrayList<>();

		// TODO load brains, stories, dictionaries
		loadServerBrains();
	}

	/**
	 * Retroactively loads server settings files from disk.
	 */
	private void loadServerBrains(){
		try (MongoCursor<Brain> cursor = shmames.getBotConfigService().getDatabaseService().readTable(Brain.class, BRAIN_TABLE)) {
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

			// TODO we're gonna make settings better - settings service!
			// Ensure new settings are made available for the user to change.
			for(BotSetting defaultSetting : App.Shmames.getStorageService().getDefaultSettings()) {
				boolean exists = false;

				for(BotSetting botSetting : brain.getSettings()) {
					if(botSetting.getName() == defaultSetting.getName()) {
						exists = true;
						break;
					}
				}

				if(!exists) {
					BotSetting newSetting = new BotSetting(defaultSetting.getName(), defaultSetting.getType(), defaultSetting.getAsString());

					// Before adding a new ROLE setting with the "everyone" default, set its ID to this server's public role.
					if(newSetting.getType() == BotSettingType.ROLE && newSetting.getAsString().equalsIgnoreCase("everyone")) {
						Role everyone = App.Shmames.getJDA().getGuildById(brain.getGuildID()).getPublicRole();

						newSetting.setValue(everyone.getId(), brain);
					}

					brain.getSettings().add(newSetting);
				}
			}

			// Remove any settings that are no longer supported.
			for(BotSetting bs : new ArrayList<>(brain.getSettings())) {
				boolean contains = false;

				for(BotSettingName s : BotSettingName.values()) {
					if(bs.getName()==s) {
						contains = true;
						break;
					}
				}

				if(!contains)
					brain.getSettings().remove(bs);
			}

			// Manually reset any cooldowns that don't have a task set up.
			if(brain.getReportCooldown())
				brain.setReportCooldown(false);
		}
	}

	/**
	 * Retrieves a list of server settings files currently loaded.
	 * @return A list of loaded settings files.
	 */
	public List<Brain> getBrains() {
		return brains;
	}

	/**
	 * Retrieves the settings file for a particular server.
	 * @param guildID The server to pull up.
	 * @return The server's settings file.
	 */
	public Brain getBrain(String guildID) {
		for (Brain b : brains) {
			if (b.getGuildID().equals(guildID)) {
				return b;
			}
		}

		Brain b = new Brain(guildID);
		brains.add(b);

		return b;
	}

	/**
	 * Retrieves the stories file.
	 * @return The stories file.
	 */
	public StorytimeStories getStories() {
		return stories;
	}

	/**
	 * Retrieves the hangman dictionaries file.
	 * @return The dictionaries file.
	 */
	public HangmanDictionaries getHangmanDictionaries() {
		return dictionaries;
	}

	/**
	 * Saves the global settings object to file.
	 */
	public void saveMotherBrain() {
		FileService.SaveBytesToFile(BRAIN_PARENT_DIRECTORY, MOTHER_BRAIN_FILE , gson.toJson(motherBrain).getBytes());
	}

	/**
	 * A wrapper function that saves the Brain object to disk.
	 * @param brain The Brain to save.
	 */
	public void saveBrain(Brain brain) {
		FileService.SaveBytesToFile(BRAIN_SERVER_DIRECTORY, brain.getGuildID()+ ".json", gson.toJson(brain).getBytes());
	}

	/**
	 * Saves the file that contains stories.
	 */
	public void saveStories() {
		FileService.SaveBytesToFile(BRAIN_PARENT_DIRECTORY, STORIES_FILE , gson.toJson(stories).getBytes());
	}

	/**
	 * Saves the file that contains hangman dictionaries.
	 */
	public void saveDictionaries() {
		FileService.SaveBytesToFile(BRAIN_PARENT_DIRECTORY, HANGMAN_FILE , gson.toJson(dictionaries).getBytes());
	}
}
