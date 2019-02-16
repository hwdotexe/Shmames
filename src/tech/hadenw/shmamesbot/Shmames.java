package tech.hadenw.shmamesbot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Game.GameType;

public final class Shmames {
	private static JDA jda;
	private static File brainFile;
	private static Brain brain;
	private static List<Poll> polls;
	
	/**
	 * The entry point for the bot.
	 * @param args Program launch arguments.
	 */
	public static void main(String[] args) {
		brainFile = new File("brain.json");
		polls = new ArrayList<Poll>();
		
		Utils.Init();
		
		try {
			// Real James
			jda = new JDABuilder(AccountType.BOT).setToken("Mzc3NjM5MDQ4NTczMDkxODYw.DOP4Yw.0fcDxWpRiy1G-BhzVLL-Idd2854").build();
			
			// TestBot
			//jda = new JDABuilder(AccountType.BOT).setToken("NTI4MDc4MjI5MTYxMTE1Njcx.DztlbA.eIbCOJcRZX1ZpJ5aQ7ot8nYGmzI").build();
			
			// Load from file.
			loadBrain();
			
			// Set the bot's status.
			String action = Utils.randomItem(brain.getStatuses().keySet());
			GameType t = brain.getStatuses().get(action);
			Shmames.getJDA().getPresence().setGame(Game.of(t, action));
			
			// Begin listening for events.
			jda.addEventListener(new Chat());
			jda.addEventListener(new React());
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
	 * Returns a list of Poll objects currently in effect.
	 * @return A list of Polls.
	 */
	public static List<Poll> getPolls() {
		return polls;
	}
	
	/**
	 * Returns the current Brain object.
	 * @return A Brain object.
	 */
	public static Brain getBrain() {
		return brain;
	}
	
	/**
	 * A public-facing method to reload the brain from file.
	 */
	public static void reloadBrain() {
		loadBrain();
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
	
	/**
	 * Saves the bot's data to disk.
	 */
	public static void saveBrain() {
		byte[] bytes = brain.getValuesAsJSON().toString().getBytes();
		
		try {
			FileOutputStream os = new FileOutputStream(brainFile);
			
			if(!brainFile.exists()) 
				brainFile.createNewFile();
			
			os.write(bytes);
			os.flush();
			os.close();
		}catch(Exception e) {
			System.out.println("TOO_MANY_MATCHES: Failed to save my brain to disk.");
			e.printStackTrace();
		}
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
