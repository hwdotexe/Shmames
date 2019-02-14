package tech.hadenw.shmamesbot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.json.JSONObject;

import net.dv8tion.jda.core.entities.Game.GameType;

public class Brain {
	private HashMap<String, Integer> tallies;
	private HashMap<String, GameType> statuses;
	private HashMap<String, TriggerType> triggers;
	private HashMap<String, TriggerType> responses;
	
	public Brain(JSONObject json) {
		tallies = new HashMap<String, Integer>();
		statuses = new HashMap<String, GameType>();
		triggers = new HashMap<String, TriggerType>();
		responses = new HashMap<String, TriggerType>();
		
		if(json != null) {
			JSONObject t = json.getJSONObject("tallies");
			JSONObject s = json.getJSONObject("statuses");
			JSONObject tr = json.getJSONObject("triggers");
			JSONObject r = json.getJSONObject("responses");
			
			for(String k : JSONObject.getNames(t)) {
				tallies.put(k, t.getInt(k));
			}
			
			for(String k : JSONObject.getNames(s)) {
				GameType type = GameType.valueOf(s.getString(k));
				
				if(type != null) {
					statuses.put(k, type);
				}
			}
			
			for(String k : JSONObject.getNames(tr)) {
				TriggerType type = TriggerType.byName(tr.getString(k));
				
				if(type != null) {
					triggers.put(k, type);
				}
			}
			
			for(String k : JSONObject.getNames(r)) {
				TriggerType type = TriggerType.byName(r.getString(k));
				
				if(type != null) {
					responses.put(k, type);
				}
			}
		}else {
			// Add default values.
			triggers.put("hey james", TriggerType.COMMAND);
			statuses.put("with matches", GameType.DEFAULT);
			tallies.put("memes", 1);
			responses.put("What'd you call me?! :angry:", TriggerType.RONALD);
		}
	}
	
	public HashMap<String, Integer> getTallies(){
		return tallies;
	}
	
	public HashMap<String, GameType> getStatuses(){
		return statuses;
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
	
	public Set<String> getAllTriggers(){
		return triggers.keySet();
	}
	
	public void addTrigger(String trig, TriggerType type) {
		triggers.put(trig, type);
	}
	
	public HashMap<String, TriggerType> getResponses(){
		return responses;
	}
	
	public List<String> getAllResponsesFor(TriggerType type){
		List<String> t = new ArrayList<String>();
		
		for(String tr : responses.keySet()) {
			if(responses.get(tr)==type)
				t.add(tr);
		}
		
		return t;
	}
	
	public void addResponse(String resp, TriggerType type) {
		responses.put(resp, type);
	}
	
	public JSONObject getValuesAsJSON() {
		JSONObject t = new JSONObject();
		JSONObject g = new JSONObject();
		JSONObject tr = new JSONObject();
		JSONObject r = new JSONObject();
		JSONObject root = new JSONObject();
		
		for(String k : tallies.keySet()) {
			t.put(k, tallies.get(k));
		}
		
		for(String k : statuses.keySet()) {
			g.put(k, statuses.get(k).toString());
		}
		
		for(String k : triggers.keySet()) {
			tr.put(k, triggers.get(k).toString());
		}
		
		for(String k : responses.keySet()) {
			r.put(k, responses.get(k).toString());
		}
		
		root.put("tallies", t);
		root.put("statuses", g);
		root.put("triggers", tr);
		root.put("responses", r);
		
		return root;
	}
}
