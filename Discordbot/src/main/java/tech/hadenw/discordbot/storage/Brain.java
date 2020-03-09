package tech.hadenw.discordbot.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tech.hadenw.discordbot.Poll;
import tech.hadenw.discordbot.Shmames;
import tech.hadenw.discordbot.TriggerType;

public class Brain {
	private String guildID;
	private HashMap<String, Integer> tallies;
	private HashMap<String, Integer> emoteStats;
	private HashMap<String, TriggerType> triggers;
	private List<Response> triggerResponses;
	private List<BotSetting> settings;
	private List<String> feedback;
	private List<Poll> activePolls;
	private boolean isReportCooldown;
	private boolean isTimeout;
	private boolean isJinping;
	
	public Brain(String gid) {
		guildID = gid;
		tallies = new HashMap<String, Integer>();
		emoteStats = new HashMap<String, Integer>();
		triggers = new HashMap<String, TriggerType>();
		triggerResponses = new ArrayList<Response>();
		settings = new ArrayList<BotSetting>();
		feedback = new ArrayList<String>();
		activePolls = new ArrayList<Poll>();
		isReportCooldown = false;
		isTimeout = false;
		isJinping = false;
		
		loadFirstRunDefaults();
	}
	
	/*
	 * Temporary state items
	 */
	
	public boolean getJinping() {
		return isJinping;
	}
	
	public void setJinping(boolean j) {
		isJinping = j;
	}
	
	public boolean getTimeout() {
		return isTimeout;
	}
	
	public void setTimeout(boolean t) {
		isTimeout=t;
	}
	
	public boolean getReportCooldown() {
		return isReportCooldown;
	}
	
	public void setReportCooldown(boolean cd) {
		isReportCooldown = cd;
	}
	
	public List<Poll> getActivePolls(){
		if(activePolls == null)
			activePolls = new ArrayList<Poll>();
		
		return activePolls;
	}
	
	/*
	 * Permanent / Semi-permanent items
	 */
	
	public String getGuildID() {
		return guildID;
	}
	
	public List<Response> getTriggerResponses(){
		return triggerResponses;
	}
	
	public void removeTriggerResponse(Response r) {
		triggerResponses.remove(r);
	}
	
	public List<BotSetting> getSettings(){
		return settings;
	}
	
	public BotSetting getSettingFor(BotSettingName n) {
		for(BotSetting s : settings) {
			if(s.getName() == n) {
				return s;
			}
		}
		
		return null;
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
	
	public HashMap<String, Integer> getEmoteStats(){
		if(emoteStats == null)
			emoteStats = new HashMap<String, Integer>();
		
		return emoteStats;
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
	 * Loads default setting values into the object. This method will only
	 * run when this object is first created.
	 */
	public void loadFirstRunDefaults() {
		triggers.put(Shmames.getJDA().getSelfUser().getName().toLowerCase(), TriggerType.COMMAND);
		settings.addAll(Shmames.defaults);
		feedback.add("Example feedback");
	}
}