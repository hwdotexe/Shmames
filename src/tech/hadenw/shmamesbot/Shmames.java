package tech.hadenw.shmamesbot;

import java.io.File;
import java.io.FileInputStream;
import java.util.Random;
import java.util.Set;

import org.json.JSONObject;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Game.GameType;

public final class Shmames {
	private static JDA jda;
	private static File brainFile;
	private static Random r;
	private static Brain brain;
	
	/**
	 * The entry point for the bot.
	 * @param args Program launch arguments.
	 */
	public static void main(String[] args) {
		Init();
	}
	
	/**
	 * Constructs the bot instance
	 */
	private static void Init() {
		brainFile = new File("brain.json");
		r = new Random();
		
		try {
			// Real James
			//jda = new JDABuilder(AccountType.BOT).setToken("Mzc3NjM5MDQ4NTczMDkxODYw.DOP4Yw.0fcDxWpRiy1G-BhzVLL-Idd2854").build();
			
			// TestBot
			jda = new JDABuilder(AccountType.BOT).setToken("NTI4MDc4MjI5MTYxMTE1Njcx.DztlbA.eIbCOJcRZX1ZpJ5aQ7ot8nYGmzI").build();
			
			loadBrain();
			
			// Set the bot's status.
			String action = Shmames.randomItem(brain.getStatuses().keySet());
			GameType t = brain.getStatuses().get(action);
			Shmames.getJDA().getPresence().setGame(Game.of(t, action));
			
			jda.addEventListener(new Chat());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns the current JDA working object.
	 * @return A JDA object.
	 */
	public static JDA getJDA() {
		return jda;
	}
	
	/**
	 * Returns the current Brain object.
	 * @return A Brain object.
	 */
	public static Brain getBrain() {
		return brain;
	}
	
	/**
	 * Returns a random number between 0 and the upper bound, exclusive.
	 * @param bound The ceiling for the random number.
	 * @return A random number.
	 */
	public static int getRandom(int bound) {
		return r.nextInt(bound);
	}
	
	/**
	 * Gets a random item from a Set. Taken from StackOverflow.
	 * @param coll The Set to loop through.
	 * @return An item from the Set.
	 */
	public static <T> T randomItem(Set<T> coll) {
	    int num = getRandom(coll.size());
	    for(T t: coll)
	    	if (--num < 0)
	    		return t;
	    throw new AssertionError();
	}
	
	/**
	 * Loads the data from file into memory.
	 */
	private static void loadBrain() {
		if(brainFile.exists()) {
			try {
				int data;
				FileInputStream is = new FileInputStream("brain.json");
				String prefs = "";
				
				while((data = is.read()) != -1) {
					prefs += (char)data;
				}
				
				is.close();
				
				brain = new Brain(new JSONObject(prefs));
			}catch(Exception e) {
				System.out.println("NOT_ENOUGH_MATCHES: Failed to load my brain from disk.");
				e.printStackTrace();
			}
		} else {
			brain = new Brain(null);
		}
	}
}
