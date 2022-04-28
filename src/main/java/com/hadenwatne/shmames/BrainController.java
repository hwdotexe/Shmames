package com.hadenwatne.shmames;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import com.google.gson.Gson;

import com.google.gson.GsonBuilder;
import com.hadenwatne.shmames.enums.BotSettingName;
import com.hadenwatne.shmames.models.*;
import com.hadenwatne.shmames.models.data.*;
import com.hadenwatne.shmames.services.FileService;
import com.hadenwatne.shmames.tasks.AlarmTask;
import com.hadenwatne.shmames.tasks.PollTask;

/**
 * Responsible for serialization of server-specific data files ("brains").
 */
public class BrainController {
	private final Gson gson;
	private final List<Brain> brains;

	private MotherBrain motherBrain;
	private StorytimeStories stories;
	private HangmanDictionaries dictionaries;

	private final String BRAIN_PARENT_DIRECTORY = "brains";
	private final String BRAIN_SERVER_DIRECTORY = BRAIN_PARENT_DIRECTORY + File.separator + "servers";
	private final String MOTHER_BRAIN_FILE = "motherBrain.json";
	private final String STORIES_FILE = "stories.json";
	private final String HANGMAN_FILE = "hangman.json";

	public BrainController() {
		gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		brains = new ArrayList<>();

		loadStories();
		loadHangmanDictionaries();
		loadMotherBrain();
	}

	/**
	 * Loads the primary bot settings file into memory.
	 */
	public void loadMotherBrain() {
		File motherBrainFile = new File(BRAIN_PARENT_DIRECTORY + File.separator + MOTHER_BRAIN_FILE);
		String motherBrainData = FileService.LoadFileAsString(motherBrainFile);

		if (motherBrainData.length() > 0) {
			motherBrain = gson.fromJson(motherBrainData, MotherBrain.class);
		} else {
			motherBrain = new MotherBrain();

			motherBrain.loadDefaults();
			saveMotherBrain();
		}
	}

	/**
	 * Retroactively loads server settings files from disk.
	 */
	public void loadServerBrains(){
		// Load server settings files.
		File[] brainFiles = FileService.ListFilesInDirectory(BRAIN_SERVER_DIRECTORY, new JSONFileFilter());

		for(File b : brainFiles) {
			Brain brain = gson.fromJson(FileService.LoadFileAsString(b), Brain.class);

			// If this brain belongs to a deleted server, remove it and continue.
			if(App.Shmames.getJDA().getGuildById(brain.getGuildID()) != null) {
				brains.add(brain);
			} else {
				b.delete();

				continue;
			}

			// Activate any threads that this brain may have had.
			if(brain.getActivePolls().size() > 0) {
				for(PollModel p : brain.getActivePolls()) {
					// Create new task
					Timer t = new Timer();
					t.schedule(new PollTask(p), p.getExpiration());
				}
			}

			if(brain.getTimers().size() > 0){
				for(AlarmTask t : brain.getTimers()){
					t.rescheduleTimer();
				}
			}

			// Ensure new settings are made available for the user to change.
			for(BotSetting s : App.Shmames.getStorageService().getDefaultSettings()) {
				boolean exists = false;

				for(BotSetting bs : brain.getSettings()) {
					if(bs.getName() == s.getName()) {
						exists = true;
						break;
					}
				}

				if(!exists) {
					brain.getSettings().add(new BotSetting(s.getName(), s.getType(), s.getAsString()));
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
	 * Loads the stories file from disk into memory.
	 */
	public void loadStories() {
		File storiesFile = new File(BRAIN_PARENT_DIRECTORY + File.separator + STORIES_FILE);

		if(!storiesFile.exists()) {
			try {
				storiesFile.createNewFile();
			}catch (Exception ignored) {}
		}

		String storiesData = FileService.LoadFileAsString(storiesFile);

		if (storiesData.length() > 0) {
			stories = gson.fromJson(storiesData, StorytimeStories.class);
		} else {
			stories = new StorytimeStories();

			stories.loadDefaults();
			saveStories();
		}
	}

	/**
	 * Loads the hangman dictionary file from disk into memory.
	 */
	public void loadHangmanDictionaries() {
		File dictionariesFile = new File(BRAIN_PARENT_DIRECTORY + File.separator + HANGMAN_FILE);

		if(!dictionariesFile.exists()) {
			try {
				dictionariesFile.createNewFile();
			}catch (Exception ignored) {}
		}

		String dictionariesData = FileService.LoadFileAsString(dictionariesFile);

		if (dictionariesData.length() > 0) {
			dictionaries = gson.fromJson(dictionariesData, HangmanDictionaries.class);
		} else {
			dictionaries = new HangmanDictionaries();

			dictionaries.loadDefaults();
			saveDictionaries();
		}
	}

	/**
	 * Retrieves the global settings file for the bot.
	 * @return The global settings file.
	 */
	public MotherBrain getMotherBrain() {
		return motherBrain;
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
