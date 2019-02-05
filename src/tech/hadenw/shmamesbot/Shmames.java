package tech.hadenw.shmamesbot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.json.JSONArray;
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
		brainFile = new File("brain.json");
		r = new Random();
		
		try {
			// Real James
			//jda = new JDABuilder(AccountType.BOT).setToken("Mzc3NjM5MDQ4NTczMDkxODYw.DOP4Yw.0fcDxWpRiy1G-BhzVLL-Idd2854").build();
			
			// TestBot
			jda = new JDABuilder(AccountType.BOT).setToken("NTI4MDc4MjI5MTYxMTE1Njcx.DztlbA.eIbCOJcRZX1ZpJ5aQ7ot8nYGmzI").build();
			
			// Load from file.
			loadBrain();
			
			// Set the bot's status.
			String action = Shmames.randomItem(brain.getStatuses().keySet());
			GameType t = brain.getStatuses().get(action);
			Shmames.getJDA().getPresence().setGame(Game.of(t, action));
			
			// Begin listening for chats.
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
	 * A public-facing method to reload the brain from file.
	 */
	public static void reloadBrain() {
		loadBrain();
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
	 * Gets a random item from a Set.
	 * @param set The Set to loop through.
	 * @return An item from the Set.
	 */
	public static <T> T randomItem(Set<T> set) {
	    int num = getRandom(set.size());
	    for(T t: set)
	    	if (--num < 0)
	    		return t;
	    throw new AssertionError();
	}
	
	/**
	 * Sends an HTTP GET request to the specified URL.
	 * @param u The URL to GET.
	 * @return A String representing the response.
	 */
	public static String sendGET(String u) {
		try {
			URL url = new URL(u);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			
		    conn.setRequestMethod("GET");
		    
		    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		    String line;
		    String result = "";
		    
		    while ((line = rd.readLine()) != null) {
		       result += line;
		    }
		    
		    rd.close();
		    
		    return result;
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}

	}
	
	/**
	 * Searches a GIF API and sends back a random result.
	 * @param search The search criteria.
	 * @return A String GIF URL.
	 */
	public static String getGIF(String search) {
		search = search.replaceAll(" ", "%20");
		String result = sendGET("https://api.tenor.com/v1/search?q="+search+"&key=1CI2O5Y3VUY1&safesearch=moderate&limit=20");
		
		JSONObject json = new JSONObject(result);
	    JSONArray jsonArray = json.getJSONArray("results");
	    List<String> gifURLs = new ArrayList<String>();
	    
	    for(int i=0; i<jsonArray.length(); i++) {
	    	gifURLs.add(jsonArray.getJSONObject(i).getString("url"));
	    }
	    
	    String gifurl = gifURLs.get(r.nextInt(gifURLs.size()));
	    
	    return gifurl;
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
