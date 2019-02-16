package tech.hadenw.shmamesbot.brain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tech.hadenw.shmamesbot.Shmames;
import tech.hadenw.shmamesbot.TriggerType;

public class Brain {
	private String guildID;
	private HashMap<String, Integer> tallies;
	private HashMap<String, TriggerType> triggers;
	private HashMap<String, TriggerType> responses;
	private HashMap<BotSettings, String> settings;
	
	public Brain(String gid) {
		guildID = gid;
		tallies = new HashMap<String, Integer>();
		triggers = new HashMap<String, TriggerType>();
		responses = new HashMap<String, TriggerType>();
		settings = new HashMap<BotSettings, String>();
		
		loadDefaults();
	}
	
	public String getGuildID() {
		return guildID;
	}
	
	public HashMap<String, TriggerType> getResponses(){
		return responses;
	}
	
	public HashMap<BotSettings, String> getSettings(){
		return settings;
	}
	
	public List<String> getResponsesFor(TriggerType type){
		List<String> t = new ArrayList<String>();
		
		for(String tr : responses.keySet()) {
			if(responses.get(tr)==type)
				t.add(tr);
		}
		
		return t;
	}
	
	public HashMap<String, Integer> getTallies(){
		return tallies;
	}
	
	public HashMap<String, TriggerType> getTriggers(){
		return triggers;
	}
	
	public List<String> getTriggers(TriggerType type){
		List<String> tt = new ArrayList<String>();
		
		for(String k : triggers.keySet()) {
			if(triggers.get(k) == type) {
				tt.add(k);
			}
		}
		
		return tt;
	}
	
	public String getRemovalEmoji() {
		return settings.get(BotSettings.REMOVALEMOTE);
	}
	
	/**
	 * Loads default settings into the system.
	 */
	public void loadDefaults() {
		triggers.put(Shmames.getJDA().getSelfUser().getName().toLowerCase(), TriggerType.COMMAND);
		tallies.put("memes", 1);
		responses.put("What'd you call me?! :angry:", TriggerType.RONALD);
		
		settings.put(BotSettings.PINCHANNEL, "general");
		settings.put(BotSettings.REMOVALEMOTE, "x");
	}
}