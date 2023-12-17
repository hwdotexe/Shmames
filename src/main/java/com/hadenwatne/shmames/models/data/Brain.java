package com.hadenwatne.shmames.models.data;

import com.hadenwatne.shmames.models.*;
import com.hadenwatne.shmames.models.game.HangmanGame;
import com.hadenwatne.shmames.services.settings.BotSetting;
import com.hadenwatne.shmames.services.settings.types.BotSettingName;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

// TODO clean this up
public class Brain {
	public String guildID;
	public HashMap<String, Integer> tallies;
	public HashMap<String, Integer> emoteStats;
	public List<RoleMessage> roleMessages;
	public List<UserCustomList> userLists;
	public List<BotSetting> settings;
	public List<String> feedback;
	public List<String> families;
	public List<ForumWeaponObj> forumWeapons;
	public List<Playlist> playlists;
	public List<GachaCharacter> gachaCharacters;
	public List<GachaUser> gachaUsers;
	public List<String> talliedMessages;
	public List<PollModel> activePolls;
	public Date gachaUserCreditDate;
	public GachaBanner banner;
	public boolean isReportCooldown;
	public boolean sentWelcome;
	public StorytimeStories stories;
	public HangmanDictionaries dictionaries;
	public HangmanGame hangmanGame;

	public Brain() {};

	public Brain(String gid) {
		guildID = gid;
		tallies = new HashMap<>();
		roleMessages = new ArrayList<>();
		emoteStats = new HashMap<>();
		userLists = new ArrayList<>();
		settings = new ArrayList<>();
		feedback = new ArrayList<>();
		families = new ArrayList<>();
		forumWeapons = new ArrayList<>();
		playlists = new ArrayList<>();
		gachaCharacters = new ArrayList<>();
		gachaUsers = new ArrayList<>();
		talliedMessages = new ArrayList<>();
		activePolls = new ArrayList<>();
		isReportCooldown = false;
		sentWelcome = false;
		hangmanGame = null;
		gachaUserCreditDate = new Date();
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

	public HangmanGame getHangmanGame(){
		return hangmanGame;
	}

	public void setHangmanGame(HangmanGame g){
		this.hangmanGame = g;
	}

	public List<PollModel> getActivePolls() {
		return activePolls;
	}

	/*
	 * Permanent / Semi-permanent items
	 */

	public StorytimeStories getStories() {
		return stories;
	}

	public HangmanDictionaries getHangmanDictionaries() {
		return dictionaries;
	}

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
	
	public HashMap<String, Integer> getTallies(){
		return tallies;
	}
	
	public HashMap<String, Integer> getEmoteStats(){
		return emoteStats;
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
}