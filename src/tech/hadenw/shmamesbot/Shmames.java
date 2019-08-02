package tech.hadenw.shmamesbot;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import tech.hadenw.shmamesbot.brain.BotSetting;
import tech.hadenw.shmamesbot.brain.BotSettingName;
import tech.hadenw.shmamesbot.brain.BotSettingType;
import tech.hadenw.shmamesbot.brain.BrainController;

public final class Shmames {
	private static JDA jda;
	private static List<Poll> polls;
	private static BrainController brains;
	private static boolean isOnTimeout;
	
	public static boolean isDebug;
	public static List<BotSetting> defaults;
	
	/**
	 * The entry point for the bot.
	 * @param args Program launch arguments.
	 */
	public static void main(String[] args) {
		setDefaults();
		
		polls = new ArrayList<Poll>();
		brains = new BrainController();
		isOnTimeout = false;
		
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
			
			// Set the bot's status.
			new DailyTask();
			
			// Begin listening for events.
			jda.addEventListener(new Chat());
			jda.addEventListener(new React());
			
			// Get a truly random seed
			//Utils.updateRandomSeed();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void setDefaults() {
		// Default settings for the bot. New settings are inserted automatically to existing servers.
		// To create a new default, simply add to this list or change the existing value.
		// These can be completely removed, and existing brains should load normally.
		defaults = new ArrayList<BotSetting>();
		defaults.add(new BotSetting(BotSettingName.PIN_CHANNEL, BotSettingType.CHANNEL, "general"));
		defaults.add(new BotSetting(BotSettingName.DO_PIN, BotSettingType.BOOLEAN, "true"));
		defaults.add(new BotSetting(BotSettingName.DEV_ANNOUNCE_CHANNEL, BotSettingType.CHANNEL, "general"));
		defaults.add(new BotSetting(BotSettingName.REMOVAL_EMOTE, BotSettingType.EMOTE, "roygun"));
		defaults.add(new BotSetting(BotSettingName.APPROVAL_EMOTE, BotSettingType.EMOTE, "dedede"));
		defaults.add(new BotSetting(BotSettingName.REMOVAL_THRESHOLD, BotSettingType.NUMBER, "3"));
		defaults.add(new BotSetting(BotSettingName.APPROVAL_THRESHOLD, BotSettingType.NUMBER, "3"));
		defaults.add(new BotSetting(BotSettingName.ALLOW_MODIFY, BotSettingType.ROLE, "administrator"));
	}
	
	/**
	 * Returns whether the bot has been punished.
	 * @return True if on timeout.
	 */
	public static boolean isOnTimeout() {
		return isOnTimeout;
	}
	
	/**
	 * Sets whether the bot is being punished.
	 */
	public static void setIsOnTimeout(boolean t) {
		isOnTimeout = t;
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
	
	/**
	 * Returns a list of Poll objects currently in effect.
	 * @return A list of Polls.
	 */
	public static List<Poll> getPolls() {
		return polls;
	}
	
	/**
	 * Generates a new Poll ID.
	 * @return An ID.
	 */
	public static int getPollID() {
		int i=0;
		
		for(Poll p : polls) {
			i = p.getID() + 1;
		}
		
		return i;
	}
}
