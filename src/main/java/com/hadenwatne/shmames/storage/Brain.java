package com.hadenwatne.shmames.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.hadenwatne.shmames.Poll;
import com.hadenwatne.shmames.Shmames;
import com.hadenwatne.shmames.TriggerType;
import com.hadenwatne.shmames.tasks.JTimerTask;

public class Brain {
	private String guildID;
	private HashMap<String, Integer> tallies;
	private HashMap<String, Integer> emoteStats;
	private HashMap<String, TriggerType> triggers;
	private List<Response> triggerResponses;
	private List<BotSetting> settings;
	private List<String> feedback;
	private List<Poll> activePolls;
	private List<String> families;
	private List<ForumWeaponObj> forumWeapons;
	private List<JTimerTask> timers;
	private List<Playlist> playlists;
	private boolean isReportCooldown;
	private boolean isTimeout;
	private boolean isJinping;
	private boolean sentWelcome;
	private HangmanGame hangmanGame;
	
	public Brain(String gid) {
		guildID = gid;
		tallies = new HashMap<String, Integer>();
		emoteStats = new HashMap<String, Integer>();
		triggers = new HashMap<String, TriggerType>();
		triggerResponses = new ArrayList<Response>();
		settings = new ArrayList<BotSetting>();
		feedback = new ArrayList<String>();
		activePolls = new ArrayList<Poll>();
		families = new ArrayList<String>();
		forumWeapons = new ArrayList<ForumWeaponObj>();
		timers = new ArrayList<JTimerTask>();
		playlists = new ArrayList<Playlist>();
		isReportCooldown = false;
		isTimeout = false;
		isJinping = false;
		sentWelcome = false;
		hangmanGame = null;
		
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
		return activePolls;
	}

	public HangmanGame getHangmanGame(){
		return hangmanGame;
	}

	public void setHangmanGame(HangmanGame g){
		this.hangmanGame = g;
	}

	public List<JTimerTask> getTimers(){
		return timers;
	}
	
	/*
	 * Permanent / Semi-permanent items
	 */

	public List<Playlist> getPlaylists() {
		if(playlists == null)
			playlists = new ArrayList<Playlist>();

		return playlists;
	}

	public boolean didSendWelcome(){
		return sentWelcome;
	}

	public void setSentWelcome(){
		sentWelcome = true;
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
		return emoteStats;
	}
	
	public HashMap<String, TriggerType> getTriggers(){
		return triggers;
	}
	
	public List<String> getFeedback(){
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

	public List<String> getFamilies(){
		return this.families;
	}

	public List<ForumWeaponObj> getForumWeapons(){
		return forumWeapons;
	}
	
	/**
	 * Loads default setting values into the object. This method will only
	 * run when this object is first created.
	 */
	public void loadFirstRunDefaults() {
		triggers.put(Shmames.getBotName(), TriggerType.COMMAND);
		settings.addAll(Shmames.defaults);
	}
}