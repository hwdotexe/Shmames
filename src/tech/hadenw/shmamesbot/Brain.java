package tech.hadenw.shmamesbot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Game.GameType;

public class Brain {
	private HashMap<String, Integer> tallies;
	private List<String> games;
	private HashMap<String, TriggerType> triggers;
	private HashMap<String, TriggerType> responses;
	
	private Shmames james;
	
	public Brain(JSONObject json, Shmames c) {
		james = c;
		tallies = new HashMap<String, Integer>();
		games = new ArrayList<String>();
		triggers = new HashMap<String, TriggerType>();
		responses = new HashMap<String, TriggerType>();
		
		if(json != null) {
			JSONObject t = json.getJSONObject("tallies");
			JSONArray g = json.getJSONArray("games");
			JSONObject tr = json.getJSONObject("triggers");
			JSONObject r = json.getJSONObject("responses");
			
			for(String k : JSONObject.getNames(t)) {
				tallies.put(k, t.getInt(k));
			}
			
			for(int i=0; i<g.length(); i++) {
				games.add(g.getString(i));
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
			// Add default values so the bot is actually usable by new users.
			triggers.put("hey james", TriggerType.COMMAND);
			games.add("with matches");
		}
		
		String game = games.get(james.getRandom().nextInt(games.size()));
		james.getJDA().getPresence().setGame(Game.of(GameType.DEFAULT, game));
	}
	
	public HashMap<String, Integer> getTallies(){
		return tallies;
	}
	
	public List<String> getGames(){
		return games;
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
		JSONArray g = new JSONArray();
		JSONObject tr = new JSONObject();
		JSONObject r = new JSONObject();
		JSONObject root = new JSONObject();
		
		for(String k : tallies.keySet()) {
			t.put(k, tallies.get(k));
		}
		
		for(String k : games) {
			g.put(k);
		}
		
		for(String k : triggers.keySet()) {
			tr.put(k, triggers.get(k).toString());
		}
		
		for(String k : responses.keySet()) {
			r.put(k, responses.get(k).toString());
		}
		
		root.put("tallies", t);
		root.put("games", g);
		root.put("triggers", tr);
		root.put("responses", r);
		
		return root;
	}
}
