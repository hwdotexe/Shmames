package tech.hadenw.shmamesbot;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import tech.hadenw.shmamesbot.brain.BrainController;

public final class Shmames {
	private static JDA jda;
	private static List<Poll> polls;
	private static BrainController brains;
	private static boolean isOnTimeout;
	
	/**
	 * The entry point for the bot.
	 * @param args Program launch arguments.
	 */
	public static void main(String[] args) {
		polls = new ArrayList<Poll>();
		brains = new BrainController();
		isOnTimeout = false;
		
		Utils.Init();
		
		try {
			// Use the TestBot if the debug argument is supplied at launch.
			if(args.length > 0) {
				String a0 = args[0].toLowerCase();
				
				if(a0.equals("debug")) {
					// TestBot
					jda = new JDABuilder(AccountType.BOT).setToken("NTI4MDc4MjI5MTYxMTE1Njcx.DztlbA.eIbCOJcRZX1ZpJ5aQ7ot8nYGmzI").build();
				}
			}else {
				// Real Bot
				jda = new JDABuilder(AccountType.BOT).setToken("Mzc3NjM5MDQ4NTczMDkxODYw.DOP4Yw.0fcDxWpRiy1G-BhzVLL-Idd2854").build();
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
