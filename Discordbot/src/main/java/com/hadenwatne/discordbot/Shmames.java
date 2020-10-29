package com.hadenwatne.discordbot;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import com.hadenwatne.discordbot.http.ShmamesHTTPHandler;
import com.hadenwatne.discordbot.listeners.ChatListener;
import com.hadenwatne.discordbot.listeners.FirstJoinListener;
import com.hadenwatne.discordbot.listeners.ReactListener;
import com.hadenwatne.discordbot.storage.*;
import com.hadenwatne.discordbot.tasks.SaveDataTask;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;

import com.sun.net.httpserver.HttpServer;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.annotation.Nullable;

public final class Shmames {
	private static JDA jda;
	private static BrainController brains;
	private static LangLoader langs;
	private static String botName;
	private static MusicManager musicManager;
	
	public static boolean isDebug;
	public static List<BotSetting> defaults;

	private static HttpServer httpServer;
	
	/**
	 * The entry point for the bot.
	 * @param args Program launch arguments.
	 */
	public static void main(String[] args) {
		// Initialize bot settings and utilities.
		brains = new BrainController();
		langs = new LangLoader();

		langs.loadLangs();
		loadDefaultSettings();
		Utils.Init();
		brains.loadMotherBrain();
		
		try {
			if(!brains.getMotherBrain().getBotAPIKey().equals("API_KEY_HERE")) {
				if (args.length > 0 && args[0].toLowerCase().equals("-debug")) {
					jda = JDABuilder.createDefault(brains.getMotherBrain().getBotAPIKeySecondary()).build();
					isDebug = true;
				} else {
					jda = JDABuilder.createDefault(brains.getMotherBrain().getBotAPIKey()).build();
					isDebug = false;
				}

				// Open HTTP server to take in API requests.
				httpServer = HttpServer.create(new InetSocketAddress("0.0.0.0", 8337), 0);
				httpServer.createContext("/shmames", new ShmamesHTTPHandler());
				httpServer.start();

				// Load server brains after the bot has initialized.
				jda.awaitReady();
				brains.loadServerBrains();

				// Set the bot's status.
				new SaveDataTask();

				// Set the bot name.
				botName = getJDA().getSelfUser().getName();

				// Begin listening for events.
				jda.addEventListener(new ChatListener());
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

				System.out.println("Could not read bot API key. Please ensure the value \"botAPIKey\" in \"/brains/motherBrain.json\" has a correct bot token from Discord.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String getBotName() {
		return botName;
	}

	public static MusicManager getMusicManager() {
		return musicManager;
	}
	
	/**
	 * Creates an array of currently-accepted settings per-server. Items not in this list are removed from
	 * settings files when loaded. New items are added with their default values.
	 */
	private static void loadDefaultSettings() {
		defaults = new ArrayList<BotSetting>();
		defaults.add(new BotSetting(BotSettingName.PIN_CHANNEL, BotSettingType.CHANNEL, "general"));
		defaults.add(new BotSetting(BotSettingName.PIN_POLLS, BotSettingType.BOOLEAN, "false"));
		defaults.add(new BotSetting(BotSettingName.DEV_ANNOUNCE_CHANNEL, BotSettingType.CHANNEL, "general"));
		defaults.add(new BotSetting(BotSettingName.MUTE_DEV_ANNOUNCES, BotSettingType.BOOLEAN, "false"));
		defaults.add(new BotSetting(BotSettingName.REMOVAL_EMOTE, BotSettingType.EMOTE, "notset"));
		defaults.add(new BotSetting(BotSettingName.APPROVAL_EMOTE, BotSettingType.EMOTE, "notset"));
		defaults.add(new BotSetting(BotSettingName.REMOVAL_THRESHOLD, BotSettingType.NUMBER, "3"));
		defaults.add(new BotSetting(BotSettingName.APPROVAL_THRESHOLD, BotSettingType.NUMBER, "3"));
		defaults.add(new BotSetting(BotSettingName.ALLOW_MODIFY, BotSettingType.ROLE, "administrator"));
		defaults.add(new BotSetting(BotSettingName.ALLOW_NICKNAME, BotSettingType.ROLE, "everyone"));
		defaults.add(new BotSetting(BotSettingName.ALLOW_POLLS, BotSettingType.ROLE, "everyone"));
		defaults.add(new BotSetting(BotSettingName.RESET_EMOTE_STATS, BotSettingType.ROLE, "administrator"));
		defaults.add(new BotSetting(BotSettingName.MANAGE_MUSIC, BotSettingType.ROLE, "administrator"));
		defaults.add(new BotSetting(BotSettingName.SERVER_LANG, BotSettingType.TEXT, "default"));
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
				l = langs.getLang("default");
			}

			return l;
		} else {
			return getDefaultLang();
		}
	}
	
	/**
	 * Returns the current JDA working object.
	 * @return A JDA object.
	 */
	public static JDA getJDA() {
		return jda;
	}
}
