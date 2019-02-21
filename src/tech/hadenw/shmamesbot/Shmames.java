package tech.hadenw.shmamesbot;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Game.GameType;
import tech.hadenw.shmamesbot.brain.BrainController;

public final class Shmames {
	private static JDA jda;
	private static List<Poll> polls;
	private static BrainController brains;
	
	/**
	 * The entry point for the bot.
	 * @param args Program launch arguments.
	 */
	public static void main(String[] args) {
		polls = new ArrayList<Poll>();
		brains = new BrainController();
		
		Utils.Init();
		
		try {
			// Real James
			jda = new JDABuilder(AccountType.BOT).setToken("Mzc3NjM5MDQ4NTczMDkxODYw.DOP4Yw.0fcDxWpRiy1G-BhzVLL-Idd2854").build();
			
			// TestBot
			//jda = new JDABuilder(AccountType.BOT).setToken("NTI4MDc4MjI5MTYxMTE1Njcx.DztlbA.eIbCOJcRZX1ZpJ5aQ7ot8nYGmzI").build();
			
			// Set the bot's status.
			String action = Utils.randomItem(brains.getMotherBrain().getStatuses().keySet());
			GameType t = brains.getMotherBrain().getStatuses().get(action);
			Shmames.getJDA().getPresence().setGame(Game.of(t, action));
			
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
