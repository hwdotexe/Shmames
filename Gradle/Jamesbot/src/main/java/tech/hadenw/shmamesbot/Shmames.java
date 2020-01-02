package tech.hadenw.shmamesbot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import tech.hadenw.shmamesbot.brain.BotSetting;
import tech.hadenw.shmamesbot.brain.BotSettingName;
import tech.hadenw.shmamesbot.brain.BotSettingType;
import tech.hadenw.shmamesbot.brain.BrainController;

public final class Shmames {
	private static JDA jda;
	private static BrainController brains;
	
	public static boolean isDebug;
	public static List<BotSetting> defaults;
	
	public static AudioPlayerManager musicPlayer;
	public static HashMap<String, GuildOcarina> ocarinas;
	
	/**
	 * The entry point for the bot.
	 * @param args Program launch arguments.
	 */
	public static void main(String[] args) {
		setDefaults();
		
		isDebug = false;
		
		Utils.Init();
		
		try {
			// Use the TestBot if the debug argument is supplied at launch.
			if(args.length > 0 && args[0].toLowerCase().equals("debug")) {
				jda = new JDABuilder(AccountType.BOT).setToken("NTI4MDc4MjI5MTYxMTE1Njcx.DztlbA.eIbCOJcRZX1ZpJ5aQ7ot8nYGmzI").build();
				isDebug = true;
			} else {
				// Real Bot
				jda = new JDABuilder(AccountType.BOT).setToken("Mzc3NjM5MDQ4NTczMDkxODYw.XPcHnQ.jdKNHXnS5Z3lkAy0GDwz1_6tmeA").build();
			}
			
			// Load brains after the bot has initialized.
			jda.awaitReady();
			brains = new BrainController();
			
			// Set the bot's status.
			new DailyTask();
			
			// Begin listening for events.
			jda.addEventListener(new Chat());
			jda.addEventListener(new ReactListener());
			
			// Prepare music playing functionality.
			ocarinas = new  HashMap<String, GuildOcarina>();
			musicPlayer = new DefaultAudioPlayerManager();
			AudioSourceManagers.registerRemoteSources(musicPlayer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String getBotName() {
		return getJDA().getSelfUser().getName();
	}
	
	public static AudioPlayerManager getAudioPlayer() {
		return musicPlayer;
	}
	
	public static GuildOcarina getOcarina(String guildID) {
		if(ocarinas.containsKey(guildID))
			return ocarinas.get(guildID);
		else {
			GuildOcarina go = new GuildOcarina(jda.getGuildById(guildID).getAudioManager());
			ocarinas.put(guildID, go);
			
			return go;
		}
	}
	
	private static void setDefaults() {
		// Default settings for the bot. New settings are inserted automatically to existing servers.
		// To create a new default, simply add to this list or change the existing value.
		// These can be completely removed, and existing brains should load normally.
		defaults = new ArrayList<BotSetting>();
		defaults.add(new BotSetting(BotSettingName.PIN_CHANNEL, BotSettingType.CHANNEL, "general"));
		defaults.add(new BotSetting(BotSettingName.PIN_POLLS, BotSettingType.BOOLEAN, "false"));
		defaults.add(new BotSetting(BotSettingName.DEV_ANNOUNCE_CHANNEL, BotSettingType.CHANNEL, "general"));
		defaults.add(new BotSetting(BotSettingName.MUTE_DEV_ANNOUNCES, BotSettingType.BOOLEAN, "false"));
		defaults.add(new BotSetting(BotSettingName.REMOVAL_EMOTE, BotSettingType.EMOTE, "roygun"));
		defaults.add(new BotSetting(BotSettingName.APPROVAL_EMOTE, BotSettingType.EMOTE, "dedede"));
		defaults.add(new BotSetting(BotSettingName.REMOVAL_THRESHOLD, BotSettingType.NUMBER, "3"));
		defaults.add(new BotSetting(BotSettingName.APPROVAL_THRESHOLD, BotSettingType.NUMBER, "3"));
		defaults.add(new BotSetting(BotSettingName.ALLOW_MODIFY, BotSettingType.ROLE, "administrator"));
	}
	
	/**
	 * Gets a set of brains for this bot.
	 * @return A BrainController object.
	 */
	public static BrainController getBrains() {
		return brains;
	}
	
	/**
	 * Returns the current JDA working object.
	 * @return A JDA object.
	 */
	public static JDA getJDA() {
		return jda;
	}
}