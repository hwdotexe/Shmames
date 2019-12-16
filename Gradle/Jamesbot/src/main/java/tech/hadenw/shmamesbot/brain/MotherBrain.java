package tech.hadenw.shmamesbot.brain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.dv8tion.jda.core.entities.Game.GameType;

public class MotherBrain {
	private HashMap<String, GameType> statuses;
	private HashMap<String, Integer> commandStats;
	private List<ForumWeaponObj> forumWeapons;
	
	public MotherBrain() {
		statuses = new HashMap<String, GameType>();
		commandStats = new HashMap<String, Integer>();
		forumWeapons = new ArrayList<ForumWeaponObj>();
	}
	
	public HashMap<String, GameType> getStatuses(){
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
		statuses.put("Bagpipes", GameType.DEFAULT);
		statuses.put("Netflix", GameType.WATCHING);
		statuses.put("Game Soundtracks", GameType.LISTENING);
	}
}
