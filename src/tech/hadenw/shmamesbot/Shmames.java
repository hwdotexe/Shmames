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
import java.util.Timer;

import org.json.JSONArray;
import org.json.JSONObject;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.TextChannel;

public class Shmames{
	private JDA jda;
	private File brainFile;
	private Random r;
	private Brain brain;
	
	public void onEnable() {
		brainFile = new File("brain.json");
		r = new Random();
		
		try {
			jda = new JDABuilder(AccountType.BOT).setToken("Mzc3NjM5MDQ4NTczMDkxODYw.DOP4Yw.0fcDxWpRiy1G-BhzVLL-Idd2854").buildBlocking();
			
			this.loadBrain();
			this.openBirthdayThreads();
			
			jda.addEventListener(new Chat(this));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void onDisable() {
		this.saveBrain();
	}
	
	public Brain getBrain() {
		return brain;
	}
	
	public JDA getJDA() {
		return jda;
	}
	
	public Random getRandom() {
		return r;
	}
	
	private void openBirthdayThreads() {
		for(TextChannel c : jda.getTextChannels()) {
			if(c.getName().equalsIgnoreCase("shmamesbotstuff")) {
				Timer test = new Timer("Chad");
				test.scheduleAtFixedRate(new BirthdaySpam(22, 3, "Happy Birthday, you old fart!", "ChadderBox", c, jda), 3600000, 3600000);
			}
		}
	}
	
	public void saveBrain() {
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
		}
	}
	
	public String getGifURL(String search) {
		search = search.replaceAll(" ", "%20");
		try {
			URL url = new URL("https://api.tenor.com/v1/search?q="+search+"&key=1CI2O5Y3VUY1&safesearch=moderate&limit=20");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			
		    conn.setRequestMethod("GET");
		    
		    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		    String line;
		    String result = "";
		    
		    while ((line = rd.readLine()) != null) {
		       result += line;
		    }
		    
		    rd.close();
		    
		    JSONObject results = new JSONObject(result);
		    JSONArray arr = results.getJSONArray("results");
		    List<String> gurls = new ArrayList<String>();
		    
		    for(int i=0; i<arr.length(); i++) {
		    	gurls.add(arr.getJSONObject(i).getString("url"));
		    }
		    
		    String gifurl = gurls.get(r.nextInt(gurls.size()));
		    
		    return gifurl;
		}catch(Exception e) {
			return "I think they cancelled the Internet... https://tenor.com/vcct.gif";
		}
	}
	
	public void loadBrain() {
		if(brainFile.exists()) {
			try {
				int data;
				FileInputStream is = new FileInputStream("brain.json");
				String prefs = "";
				
				while((data = is.read()) != -1) {
					prefs += (char)data;
				}
				
				is.close();
				
				brain = new Brain(new JSONObject(prefs), this);
			}catch(Exception e) {
				System.out.println("NOT_ENOUGH_MATCHES: Failed to load my brain from disk.");
				e.printStackTrace();
			}
		} else {
			brain = new Brain(null, this);
		}
	}
}
