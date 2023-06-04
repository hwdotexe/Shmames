package com.hadenwatne.shmames.models.data;

import com.hadenwatne.shmames.App;
import com.hadenwatne.shmames.enums.BotSettingName;
import com.hadenwatne.shmames.enums.TriggerType;
import com.hadenwatne.shmames.models.*;
import com.hadenwatne.shmames.models.game.HangmanGame;
import com.hadenwatne.shmames.tasks.AlarmTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Brain {
	private final String guildID;
	private final HashMap<String, Integer> tallies;
	private final HashMap<String, Integer> emoteStats;
	private final HashMap<String, TriggerType> triggers;
	private List<RoleMessage> roleMessages;
	private List<UserCustomList> userLists;
	private final List<Response> triggerResponses;
	private final List<BotSetting> settings;
	private final List<String> feedback;
	private final List<PollModel> activePolls;
	private final List<String> families;
	private final List<ForumWeaponObj> forumWeapons;
	private final List<AlarmTask> timers;
	private final List<Playlist> playlists;
	private List<GachaCharacter> gachaCharacters;
	private List<GachaUser> gachaUsers;
	private List<String> talliedMessages;
	private Date gachaUserCreditDate;

	@Deprecated
	private List<String> gachaBanner;

	private GachaBanner banner;
	private boolean isReportCooldown;
	private boolean sentWelcome;
	private HangmanGame hangmanGame;
	
	public Brain(String gid) {
		guildID = gid;
		tallies = new HashMap<>();
		roleMessages = new ArrayList<>();
		emoteStats = new HashMap<>();
		triggers = new HashMap<>();
		userLists = new ArrayList<>();
		triggerResponses = new ArrayList<>();
		settings = new ArrayList<>();
		feedback = new ArrayList<>();
		activePolls = new ArrayList<>();
		families = new ArrayList<>();
		forumWeapons = new ArrayList<>();
		timers = new ArrayList<>();
		playlists = new ArrayList<>();
		gachaCharacters = new ArrayList<>();
		gachaUsers = new ArrayList<>();
		talliedMessages = new ArrayList<>();
		isReportCooldown = false;
		sentWelcome = false;
		hangmanGame = null;
		gachaUserCreditDate = new Date();
		gachaBanner = new ArrayList<>();
		
		loadFirstRunDefaults();
	}
	
	/*
	 * Temporary state items
	 */
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

	public List<AlarmTask> getTimers(){
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
			if(tr.getTriggerType() == type)
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

	public List<RoleMessage> getRoleMessages() {
		if(this.roleMessages == null) {
			this.roleMessages = new ArrayList<>();
		}

		return roleMessages;
	}

	public RoleMessage getRoleMessageByID(String id) {
		for(RoleMessage roleMessage : getRoleMessages()) {
			if(roleMessage.getRoleMessageID().equalsIgnoreCase(id)) {
				return roleMessage;
			}
		}

		return null;
	}

	public List<UserCustomList> getUserLists() {
		if(userLists == null) {
			userLists = new ArrayList<>();
		}

		return userLists;
	}
	
	public List<String> getFeedback(){
		return feedback;
	}

	public List<String> getFamilies(){
		return this.families;
	}

	public List<ForumWeaponObj> getForumWeapons(){
		return forumWeapons;
	}

	public List<String> getTalliedMessages() {
		if(this.talliedMessages == null) {
			this.talliedMessages = new ArrayList<>();
		}

		return this.talliedMessages;
	}

	public List<GachaCharacter> getGachaCharacters() {
		if(this.gachaCharacters == null) {
			this.gachaCharacters = new ArrayList<>();
		}

		return gachaCharacters;
	}

	public List<GachaUser> getGachaUsers() {
		if(this.gachaUsers == null) {
			this.gachaUsers = new ArrayList<>();
		}

		return gachaUsers;
	}

	public Date getGachaUserCreditDate() {
		if(this.gachaUserCreditDate == null) {
			updateGachaUserCreditDate();
		}

		return gachaUserCreditDate;
	}

	public GachaBanner getGachaBanner() {
		if(this.banner == null) {
			this.banner = new GachaBanner();
		}

		return banner;
	}

	public void setGachaBanner(GachaBanner banner) {
		this.banner = banner;
	}

	public void updateGachaUserCreditDate() {
		this.gachaUserCreditDate = new Date();
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