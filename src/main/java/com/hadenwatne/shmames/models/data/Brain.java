package com.hadenwatne.shmames.models.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.hadenwatne.shmames.App;
import com.hadenwatne.shmames.enums.BotSettingName;
import com.hadenwatne.shmames.Shmames;
import com.hadenwatne.shmames.enums.TriggerType;
import com.hadenwatne.shmames.models.*;
import com.hadenwatne.shmames.tasks.JTimerTask;

public class Brain {
	private final String guildID;
	private final HashMap<String, Integer> tallies;
	private final HashMap<String, Integer> emoteStats;
	private final HashMap<String, TriggerType> triggers;
	private List<UserCustomList> userListsv2;
	private final List<Response> triggerResponses;
	private final List<BotSetting> settings;
	private final List<String> feedback;
	private final List<PollModel> activePolls;
	private final List<String> families;
	private final List<ForumWeaponObj> forumWeapons;
	private final List<JTimerTask> timers;
	private final List<Playlist> playlists;
	private boolean isReportCooldown;
	private boolean isJinping;
	private boolean sentWelcome;
	private HangmanGame hangmanGame;
	
	public Brain(String gid) {
		guildID = gid;
		tallies = new HashMap<>();
		emoteStats = new HashMap<>();
		triggers = new HashMap<>();
		userListsv2 = new ArrayList<>();
		triggerResponses = new ArrayList<>();
		settings = new ArrayList<>();
		feedback = new ArrayList<>();
		activePolls = new ArrayList<>();
		families = new ArrayList<>();
		forumWeapons = new ArrayList<>();
		timers = new ArrayList<>();
		playlists = new ArrayList<>();
		isReportCooldown = false;
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
	
	public boolean getReportCooldown() {
		return isReportCooldown;
	}
	
	public void setReportCooldown(boolean cd) {
		isReportCooldown = cd;
	}
	
	public List<PollModel> getActivePolls(){
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

	public List<UserCustomList> getUserLists() {
		if(userListsv2 == null) {
			userListsv2 = new ArrayList<>();
		}

		return userListsv2;
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
		triggers.put(App.Shmames.getBotName(), TriggerType.COMMAND);
		settings.addAll(App.Shmames.getStorageService().getDefaultSettings());
	}
}