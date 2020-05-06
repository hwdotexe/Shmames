package tech.hadenw.discordbot.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.dv8tion.jda.api.entities.Activity.ActivityType;

public class MotherBrain {
	private HashMap<String, ActivityType> statuses;
	private HashMap<String, Integer> commandStats;
	private List<Family> serverFamilies;
	
	public MotherBrain() {
		statuses = new HashMap<String, ActivityType>();
		commandStats = new HashMap<String, Integer>();
		serverFamilies = new ArrayList<Family>();
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
	
	/**
	 * Loads default settings into the system.
	 */
	public void loadDefaults() {
		statuses.put("Bagpipes", ActivityType.DEFAULT);
		statuses.put("Netflix", ActivityType.WATCHING);
		statuses.put("Game Soundtracks", ActivityType.LISTENING);
	}
}
