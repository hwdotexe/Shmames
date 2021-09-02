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
import com.hadenwatne.shmames.tasks.JTimerTask;
import com.hadenwatne.shmames.tasks.PollTask;

/**
 * Responsible for serialization of server-specific data files ("brains").
 */
public class BrainController {
	private MotherBrain motherBrain;
	private Gson gson;
	private List<Brain> brains;
	private StorytimeStories stories;
	private HangmanDictionaries dictionaries;

	private final String BRAIN_PARENT_DIRECTORY = "brains";
	private final String BRAIN_SERVER_DIRECTORY = BRAIN_PARENT_DIRECTORY + File.separator + "servers";
	private final String MOTHER_BRAIN_FILE = "motherBrain.json";
	private final String STORIES_FILE = "stories.json";
	private final String HANGMAN_FILE = "hangman.json";

	public BrainController() {
		gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		brains = new ArrayList<Brain>();

		loadStories();
		loadHangmanDictionaries();
		loadMotherBrain();
	}

	public void loadStories() {
		File storiesFile = new File(BRAIN_PARENT_DIRECTORY + File.separator + STORIES_FILE);

		if(!storiesFile.exists()) {
			try {
				storiesFile.createNewFile();
			}catch (Exception ignored) {}
		}

		String storiesData = Utils.loadFileAsString(storiesFile);

		if (storiesData.length() > 0) {
			stories = gson.fromJson(storiesData, StorytimeStories.class);
		} else {
			stories = new StorytimeStories();

			stories.loadDefaults();
			saveStories();
		}
	}

	public void loadHangmanDictionaries() {
		File dictionariesFile = new File(BRAIN_PARENT_DIRECTORY + File.separator + HANGMAN_FILE);

		if(!dictionariesFile.exists()) {
			try {
				dictionariesFile.createNewFile();
			}catch (Exception ignored) {}
		}

		String dictionariesData = Utils.loadFileAsString(dictionariesFile);

		if (dictionariesData.length() > 0) {
			dictionaries = gson.fromJson(dictionariesData, HangmanDictionaries.class);
		} else {
			dictionaries = new HangmanDictionaries();

			dictionaries.loadDefaults();
			saveDictionaries();
		}
	}

	public void loadMotherBrain() {
		File motherBrainFile = new File(BRAIN_PARENT_DIRECTORY + File.separator + MOTHER_BRAIN_FILE);
		String motherBrainData = Utils.loadFileAsString(motherBrainFile);

		if (motherBrainData.length() > 0) {
			motherBrain = gson.fromJson(motherBrainData, MotherBrain.class);
		} else {
			motherBrain = new MotherBrain();

			motherBrain.loadDefaults();
			saveMotherBrain();
		}
	}

	public void loadServerBrains(){
		// Load server settings files.
		File[] brainFiles = Utils.listFilesInDirectory(BRAIN_SERVER_DIRECTORY, new JSONFileFilter());

		for(File b : brainFiles) {
			Brain brain = gson.fromJson(Utils.loadFileAsString(b), Brain.class);

			// If this brain belongs to a deleted server, remove it and continue.
			if(Shmames.getJDA().getGuildById(brain.getGuildID()) != null) {
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
				for(JTimerTask t : brain.getTimers()){
					t.rescheduleTimer();
				}
			}

			// Ensure new settings are made available for the user to change.
			for(BotSetting s : Shmames.defaultBotSettings) {
				boolean exists = false;

				for(BotSetting bs : brain.getSettings()) {
					if(bs.getName() == s.getName()) {
						exists = true;
						break;
					}
				}

				if(!exists) {
					brain.getSettings().add(new BotSetting(s.getName(), s.getType(), s.getValue()));
				}
			}

			// Remove any settings that are no longer supported.
			for(BotSetting bs : new ArrayList<BotSetting>(brain.getSettings())) {
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

			if(brain.getJinping())
				brain.setJinping(false);
		}
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
	public HangmanDictionaries getDictionaries() {
		return dictionaries;
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
	 * A wrapper function that saves the Brain object to disk.
	 * @param brain The Brain to save.
	 */
	public void saveBrain(Brain brain) {
		Utils.saveBytesToFile(BRAIN_SERVER_DIRECTORY, brain.getGuildID()+ ".json", gson.toJson(brain).getBytes());
	}
	
	/**
	 * Saves the global settings object to file.
	 */
	public void saveMotherBrain() {
		Utils.saveBytesToFile(BRAIN_PARENT_DIRECTORY, MOTHER_BRAIN_FILE , gson.toJson(motherBrain).getBytes());
	}

	/**
	 * Saves the file that contains stories.
	 */
	public void saveStories() {
		Utils.saveBytesToFile(BRAIN_PARENT_DIRECTORY, STORIES_FILE , gson.toJson(stories).getBytes());
	}

	/**
	 * Saves the file that contains hangman dictionaries.
	 */
	public void saveDictionaries() {
		Utils.saveBytesToFile(BRAIN_PARENT_DIRECTORY, HANGMAN_FILE , gson.toJson(dictionaries).getBytes());
	}
}
