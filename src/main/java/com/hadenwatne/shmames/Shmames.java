package com.hadenwatne.shmames;

import com.hadenwatne.shmames.enums.LogType;
import com.hadenwatne.shmames.listeners.ChatListener;
import com.hadenwatne.shmames.listeners.FirstJoinListener;
import com.hadenwatne.shmames.listeners.ReactListener;
import com.hadenwatne.shmames.listeners.SlashCommandListener;
import com.hadenwatne.shmames.models.data.MotherBrain;
import com.hadenwatne.shmames.music.MusicManager;
import com.hadenwatne.shmames.services.*;
import com.hadenwatne.shmames.tasks.GachaTask;
import com.hadenwatne.shmames.tasks.SaveDataTask;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class Shmames {
	private JDA jda;
	private final LanguageService languageService;
	private String botName;
	private String botAvatarUrl;
	private MusicManager musicManager;
	private CommandHandler commandHandler;
	private final StorageService storageService;
	
	public Shmames() {
		LoggingService.Init();
		RandomService.Init();
		CacheService.Init();

		this.languageService = new LanguageService();
		this.storageService = new StorageService();
	}

	/**
	 * Returns the name of this Discord bot, as returned by Discord's API.
	 * @return The Discord application name.
	 */
	public String getBotName() {
		if (this.botName != null) {
			return this.botName;
		} else {
			return "Shmames";
		}
	}

	/**
	 * Returns the URL of the bot's avatar image.
	 * @return The Discord application avatar image.
	 */
	public String getBotAvatarUrl() {
		return this.botAvatarUrl;
	}

	/**
	 * Returns the MusicManager object currently loaded.
	 * @return A MusicManager object.
	 */
	public MusicManager getMusicManager() {
		return this.musicManager;
	}

	/**
	 * Returns the CommandHandler currently loaded.
	 * @return A CommandHandler object.
	 */
	public CommandHandler getCommandHandler() {
		return this.commandHandler;
	}

	/**
	 * Gets the Locale Loader for more localization options.
	 * @return A LocaleLoader object.
	 */
	public LanguageService getLanguageService() {
		return this.languageService;
	}

	/**
	 * Gets the StorageService currently loaded.
	 * @return A StorageService object.
	 */
	public StorageService getStorageService() {
		return this.storageService;
	}
	
	/**
	 * Returns the current JDA working object.
	 * @return A JDA object.
	 */
	public JDA getJDA() {
		return this.jda;
	}

	/**
	 * Loads the bot, authenticates to Discord, starts tasks, reads data from disk, and begins accepting requests.
	 * @param isDebug Whether the bot is running in Debug mode.
	 */
	public void startup(boolean isDebug) {
		MotherBrain motherBrain = this.storageService.getMotherBrain();

		configureJDA(isDebug ? motherBrain.getBotAPIKeySecondary() : motherBrain.getBotAPIKey(), motherBrain);

		// Load Brain objects into memory.
		this.storageService.getBrainController().loadServerBrains();

		// Start automated tasks.
		new SaveDataTask();
		new GachaTask();

		// Set the bot name and avatar URL.
		this.botName = getJDA().getSelfUser().getName();
		this.botAvatarUrl = getJDA().getSelfUser().getAvatarUrl();

		// Load commands.
		this.commandHandler = new CommandHandler();

		// Begin listening for events.
		this.jda.addEventListener(new ChatListener());
		this.jda.addEventListener(new SlashCommandListener());
		this.jda.addEventListener(new ReactListener());
		this.jda.addEventListener(new FirstJoinListener());

		// Prepare music playing functionality.
		this.musicManager = new MusicManager();
	}

	private void configureJDA(String apiKey, MotherBrain motherBrain) {
		try {
			this.jda = JDABuilder.createDefault(apiKey)
					.enableIntents(GatewayIntent.MESSAGE_CONTENT)
					.enableCache(CacheFlag.EMOJI)
					.build();

			this.jda.awaitReady();
		} catch (InvalidTokenException e) {
			if(apiKey.equals("API_KEY_HERE")) {
				// Retrieve all keys to generate default values.
				motherBrain.getTenorAPIKey();
				motherBrain.getBotAPIKey();
				motherBrain.getBotAPIKeySecondary();
				motherBrain.getWolframAPIKey();

				// Save the file to disk.
				this.storageService.getBrainController().saveMotherBrain();

				LoggingService.Log(LogType.ERROR, "Could not read bot API key. Please ensure the value \"botAPIKey\" in \"/brains/motherBrain.json\" has a correct bot token from Discord.");
			} else {
				LoggingService.LogException(e);
			}
		} catch (InterruptedException e) {
			LoggingService.LogException(e);
		}
	}
}
