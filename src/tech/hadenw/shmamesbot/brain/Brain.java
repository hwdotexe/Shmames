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
	private List<Response> triggerResponses;
	private HashMap<BotSettings, String> settings;
	private List<String> feedback;
	
	public Brain(String gid) {
		guildID = gid;
		tallies = new HashMap<String, Integer>();
		triggers = new HashMap<String, TriggerType>();
		triggerResponses = new ArrayList<Response>();
		settings = new HashMap<BotSettings, String>();
		feedback = new ArrayList<String>();
		
		loadDefaults();
	}
	
	public String getGuildID() {
		return guildID;
	}
	
	public List<Response> getTriggerResponses(){
		return triggerResponses;
	}
	
	public void removeTriggerResponse(Response r) {
		triggerResponses.remove(r);
	}
	
	public HashMap<BotSettings, String> getSettings(){
		return settings;
	}
	
	public List<Response> getResponsesFor(TriggerType type){
		List<Response> t = new ArrayList<Response>();

		for(Response tr : getTriggerResponses()) {
			if(tr.getType() == type)
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
	
	public List<String> getFeedback(){
		if(feedback == null)
			feedback = new ArrayList<String>();
		
		return feedback;
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
	
	/**
	 * Loads default settings into the system.
	 */
	public void loadDefaults() {
		triggers.put(Shmames.getJDA().getSelfUser().getName().toLowerCase(), TriggerType.COMMAND);
		tallies.put("memes", 1);
		triggerResponses.add(new Response(TriggerType.RONALD, "What'd you call me?! :angry:"));
		
		settings.put(BotSettings.PIN_CHANNEL, "general");
		settings.put(BotSettings.REMOVAL_EMOTE, "royGun");
		settings.put(BotSettings.REMOVAL_THRESHOLD, "3");
		
		feedback.add("Example feedback");
	}
}