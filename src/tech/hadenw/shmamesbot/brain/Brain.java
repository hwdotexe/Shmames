package tech.hadenw.shmamesbot.brain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tech.hadenw.shmamesbot.TriggerType;

public class Brain {
	private String guildID;
	private HashMap<String, Integer> tallies;
	private HashMap<String, TriggerType> triggers;
	private HashMap<String, TriggerType> responses;
	
	private String removeEmoji;
	
	public Brain(String gid) {
		guildID = gid;
		tallies = new HashMap<String, Integer>();
		triggers = new HashMap<String, TriggerType>();
		responses = new HashMap<String, TriggerType>();
		removeEmoji = "x";
	}
	
	public String getGuildID() {
		return guildID;
	}
	
	public HashMap<String, TriggerType> getResponses(){
		return responses;
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
		return removeEmoji;
	}
	
	/**
	 * Loads default settings into the system.
	 */
	public void loadDefaults() {
		triggers.put("hey bot", TriggerType.COMMAND);
		tallies.put("memes", 1);
		responses.put("What'd you call me?! :angry:", TriggerType.RONALD);
	}
}
