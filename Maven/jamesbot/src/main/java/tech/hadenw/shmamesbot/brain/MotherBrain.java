package tech.hadenw.shmamesbot.brain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.dv8tion.jda.api.entities.Activity.ActivityType;

public class MotherBrain {
	private HashMap<String, ActivityType> statuses;
	private HashMap<String, Integer> commandStats;
	private List<ForumWeaponObj> forumWeapons;
	
	public MotherBrain() {
		statuses = new HashMap<String, ActivityType>();
		commandStats = new HashMap<String, Integer>();
		forumWeapons = new ArrayList<ForumWeaponObj>();
	}
	
	public HashMap<String, ActivityType> getStatuses(){
		return statuses;
	}
	
	public  HashMap<String, Integer> getCommandStats(){
		if(commandStats == null)
			 commandStats = new HashMap<String, Integer>();
		
		return commandStats;
	}
	
	public List<ForumWeaponObj> getForumWeapons(){
		if(forumWeapons == null)
			forumWeapons = new ArrayList<ForumWeaponObj>();
		
		return forumWeapons;
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
