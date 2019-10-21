package tech.hadenw.shmamesbot.brain;

import java.util.HashMap;

import net.dv8tion.jda.core.entities.Game.GameType;

public class MotherBrain {
	private HashMap<String, GameType> statuses;
	private HashMap<String, Integer> commandStats;
	
	public MotherBrain() {
		statuses = new HashMap<String, GameType>();
		commandStats = new  HashMap<String, Integer>();
	}
	
	public HashMap<String, GameType> getStatuses(){
		return statuses;
	}
	
	public  HashMap<String, Integer> getCommandStats(){
		if(commandStats == null)
			 commandStats = new HashMap<String, Integer>();
		
		return commandStats;
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
