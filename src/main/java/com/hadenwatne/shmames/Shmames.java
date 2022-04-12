package com.hadenwatne.shmames;

import java.util.ArrayList;
import java.util.List;

import com.hadenwatne.shmames.enums.BotSettingName;
import com.hadenwatne.shmames.enums.BotSettingType;
import com.hadenwatne.shmames.enums.LogType;
import com.hadenwatne.shmames.listeners.ChatListener;
import com.hadenwatne.shmames.listeners.FirstJoinListener;
import com.hadenwatne.shmames.listeners.ReactListener;
import com.hadenwatne.shmames.listeners.SlashCommandListener;
import com.hadenwatne.shmames.models.data.BotSetting;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import com.hadenwatne.shmames.music.MusicManager;
import com.hadenwatne.shmames.services.LoggingService;
import com.hadenwatne.shmames.services.RandomService;
import com.hadenwatne.shmames.tasks.SaveDataTask;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;

import javax.annotation.Nullable;

public final class Shmames {
	private static JDA jda;
	private static BrainController brains;
	private static LangLoader langs;
	private static String botName;
	private static MusicManager musicManager;
	private static CommandHandler commandHandler;
	
	public static boolean isDebug;
	public static List<BotSetting> defaultBotSettings;
	
	/**
	 * The entry point for the bot.
	 * @param args Program launch arguments.
	 */
	public static void main(String[] args) {
		LoggingService.Init();
		RandomService.Init();

		langs = new LangLoader();
		brains = new BrainController();

		loadDefaultSettings();

		try {
			if(!brains.getMotherBrain().getBotAPIKey().equals("API_KEY_HERE")) {
				if (args.length > 0 && args[0].equalsIgnoreCase("-debug")) {
					jda = JDABuilder.createDefault(brains.getMotherBrain().getBotAPIKeySecondary()).build();
					isDebug = true;
				} else {
					jda = JDABuilder.createDefault(brains.getMotherBrain().getBotAPIKey()).build();
					isDebug = false;
				}

				// Load server brains after the bot has initialized.
				jda.awaitReady();
				brains.loadServerBrains();

				// Set the bot's status.
				new SaveDataTask();

				// Set the bot name.
				botName = getJDA().getSelfUser().getName();

				// Load commands.
				commandHandler = new CommandHandler();

				// Begin listening for events.
				jda.addEventListener(new ChatListener());
				jda.addEventListener(new SlashCommandListener());
				jda.addEventListener(new ReactListener());
				jda.addEventListener(new FirstJoinListener());

				// Prepare music playing functionality.
				musicManager = new MusicManager();
				AudioSourceManagers.registerRemoteSources(musicManager.getAudioPlayerManager());
			}else{
				// Retrieve all keys to generate the values, and then save the motherbrain.
				brains.getMotherBrain().getTenorAPIKey();
				brains.getMotherBrain().getBotAPIKey();
				brains.getMotherBrain().getBotAPIKeySecondary();
				brains.getMotherBrain().getWolframAPIKey();
				brains.saveMotherBrain();

				LoggingService.Log(LogType.ERROR, "Could not read bot API key. Please ensure the value \"botAPIKey\" in \"/brains/motherBrain.json\" has a correct bot token from Discord.");
			}
		} catch (Exception e) {
			LoggingService.LogException(e);
		}
	}
	
	public static String getBotName() {
		return botName;
	}

	public static MusicManager getMusicManager() {
		return musicManager;
	}

	public static CommandHandler getCommandHandler() {
		return commandHandler;
	}
	
	/**
	 * Creates an array of currently-accepted settings per-server. Items not in this list are removed from
	 * settings files when loaded. New items are added with their default values.
	 */
	private static void loadDefaultSettings() {
		defaultBotSettings = new ArrayList<BotSetting>();
		defaultBotSettings.add(new BotSetting(BotSettingName.PIN_CHANNEL, BotSettingType.CHANNEL, "general"));
		defaultBotSettings.add(new BotSetting(BotSettingName.PIN_POLLS, BotSettingType.BOOLEAN, "false"));
		defaultBotSettings.add(new BotSetting(BotSettingName.REMOVAL_EMOTE, BotSettingType.EMOTE, "notset"));
		defaultBotSettings.add(new BotSetting(BotSettingName.APPROVAL_EMOTE, BotSettingType.EMOTE, "notset"));
		defaultBotSettings.add(new BotSetting(BotSettingName.REMOVAL_THRESHOLD, BotSettingType.NUMBER, "3"));
		defaultBotSettings.add(new BotSetting(BotSettingName.APPROVAL_THRESHOLD, BotSettingType.NUMBER, "3"));
		defaultBotSettings.add(new BotSetting(BotSettingName.ALLOW_MODIFY, BotSettingType.ROLE, "administrator"));
		defaultBotSettings.add(new BotSetting(BotSettingName.ALLOW_POLLS, BotSettingType.ROLE, "everyone"));
		defaultBotSettings.add(new BotSetting(BotSettingName.RESET_EMOTE_STATS, BotSettingType.ROLE, "administrator"));
		defaultBotSettings.add(new BotSetting(BotSettingName.MANAGE_MUSIC, BotSettingType.ROLE, "administrator"));
		defaultBotSettings.add(new BotSetting(BotSettingName.SERVER_LANG, BotSettingType.TEXT, "default"));
		defaultBotSettings.add(new BotSetting(BotSettingName.PRUNE_FW, BotSettingType.ROLE, "administrator"));
	}
	
	/**
	 * Gets a set of brains for this bot.
	 * @return A BrainController object.
	 */
	public static BrainController getBrains() {
		return brains;
	}

	/**
	 * Gets the Locale Loader for more localization options.
	 * @return A LocaleLoader object.
	 */
	public static LangLoader getLangs() {
		return langs;
	}

	/**
	 * Gets the default Locale file.
	 * @return The default Locale.
	 */
	public static Lang getDefaultLang() {
		return langs.getLang("default");
	}

	/**
	 * Gets the Locale for a server.
	 * @return The server's desired Locale, or default if none.
	 */
	public static Lang getLangFor(@Nullable Brain b) {
		if(b != null){
			Lang l = langs.getLang(b.getSettingFor(BotSettingName.SERVER_LANG).getValue());

			if(l == null){
				return getDefaultLang();
			}

			return l;
		} else {
			return getDefaultLang();
		}
	}

	/**
	 * Gets the Lang for a server.
	 * @return The server's desired Lang, or default if none.
	 */
	public static Lang getLangFor(Guild guild) {
		if(guild != null){
			return getLangFor(brains.getBrain(guild.getId()));
		}

		return getDefaultLang();
	}
	
	/**
	 * Returns the current JDA working object.
	 * @return A JDA object.
	 */
	public static JDA getJDA() {
		return jda;
	}
}
