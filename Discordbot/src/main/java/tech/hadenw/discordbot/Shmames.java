package tech.hadenw.discordbot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import tech.hadenw.discordbot.listeners.ChatListener;
import tech.hadenw.discordbot.listeners.ReactListener;
import tech.hadenw.discordbot.storage.BotSetting;
import tech.hadenw.discordbot.storage.BotSettingName;
import tech.hadenw.discordbot.storage.BotSettingType;
import tech.hadenw.discordbot.storage.BrainController;
import tech.hadenw.discordbot.tasks.DailyTask;

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
		// Initialize bot settings and utilities.
		loadDefaultSettings();
		Utils.Init();
		
		try {
			if(args.length > 0 && args[0].toLowerCase().equals("-debug")) {
				jda = new JDABuilder(AccountType.BOT).setToken("NTI4MDc4MjI5MTYxMTE1Njcx.DztlbA.eIbCOJcRZX1ZpJ5aQ7ot8nYGmzI").build();
				isDebug = true;
			} else {
				jda = new JDABuilder(AccountType.BOT).setToken("Mzc3NjM5MDQ4NTczMDkxODYw.XPcHnQ.jdKNHXnS5Z3lkAy0GDwz1_6tmeA").build();
				isDebug = false;
			}
			
			// Load brains after the bot has initialized.
			jda.awaitReady();
			brains = new BrainController();
			
			// Set the bot's status.
			new DailyTask();
			
			// Begin listening for events.
			jda.addEventListener(new ChatListener());
			jda.addEventListener(new ReactListener());
			
			// Prepare music playing functionality.
			ocarinas = new  HashMap<String, GuildOcarina>();
			musicPlayer = new DefaultAudioPlayerManager();
			AudioSourceManagers.registerRemoteSources(musicPlayer);

			// Prepare invitation link for Console.
			List<Permission> botPerms = new ArrayList<Permission>();
			botPerms.add(Permission.CREATE_INSTANT_INVITE);
			botPerms.add(Permission.MESSAGE_ADD_REACTION);
			botPerms.add(Permission.MESSAGE_EMBED_LINKS);
			botPerms.add(Permission.MESSAGE_HISTORY);
			botPerms.add(Permission.MESSAGE_MANAGE);
			botPerms.add(Permission.MESSAGE_ATTACH_FILES);
			botPerms.add(Permission.MESSAGE_WRITE);
			botPerms.add(Permission.MESSAGE_READ);
			
			System.out.println(">>> Invite "+getBotName()+" to your server!\n"+jda.getInviteUrl(botPerms));
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
		if(ocarinas.containsKey(guildID)) {
			return ocarinas.get(guildID);
		} else {
			GuildOcarina go = new GuildOcarina(jda.getGuildById(guildID).getAudioManager());
			ocarinas.put(guildID, go);
			
			return go;
		}
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