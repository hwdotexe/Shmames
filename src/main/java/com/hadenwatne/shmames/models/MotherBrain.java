package com.hadenwatne.shmames.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.hadenwatne.shmames.models.Family;
import net.dv8tion.jda.api.entities.Activity.ActivityType;

public class MotherBrain {
	private HashMap<String, ActivityType> statuses;
	private HashMap<String, Integer> commandStats;
	private List<Family> serverFamilies;
	private String botAPIKey;
	private String botAPIKeySecondary;
	private String tenorAPIKey;
	private String wolframAPIKey;
	
	public MotherBrain() {
		statuses = new HashMap<String, ActivityType>();
		commandStats = new HashMap<String, Integer>();
		serverFamilies = new ArrayList<Family>();
		botAPIKey = "API_KEY_HERE";
		botAPIKeySecondary = "API_KEY_HERE";
		tenorAPIKey = "API_KEY_HERE";
		wolframAPIKey = "API_KEY_HERE";
	}
	
	public HashMap<String, ActivityType> getStatuses(){
		return statuses;
	}
	
	public  HashMap<String, Integer> getCommandStats(){
		if(commandStats == null)
			 commandStats = new HashMap<String, Integer>();
		
		return commandStats;
	}

	public List<Family> getServerFamilies(){
		if(this.serverFamilies == null)
			this.serverFamilies = new ArrayList<Family>();

		return this.serverFamilies;
	}

	public Family getFamilyByID(String famID){
		for(Family f : getServerFamilies()){
			if(f.getFamID().equals(famID)){
				return f;
			}
		}

		return null;
	}

	public String getBotAPIKey(){
		if(botAPIKey == null)
			botAPIKey = "API_KEY_HERE";

		return botAPIKey;
	}

	public String getBotAPIKeySecondary() {
		if(botAPIKeySecondary == null)
			botAPIKeySecondary = "API_KEY_HERE";

		return botAPIKeySecondary;
	}

	public String getTenorAPIKey() {
		if(tenorAPIKey == null)
			tenorAPIKey = "API_KEY_HERE";

		return tenorAPIKey;
	}

	public String getWolframAPIKey() {
		if(wolframAPIKey == null)
			wolframAPIKey = "API_KEY_HERE";

		return wolframAPIKey;
	}
	
	/**
	 * Loads default settings into the system.
	 */
	public void loadDefaults() {
		statuses.put("Bagpipes", ActivityType.DEFAULT);
		statuses.put("Netflix", ActivityType.WATCHING);
		statuses.put("Game Soundtracks", ActivityType.LISTENING);
	}
}
