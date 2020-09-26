package com.hadenwatne.discordbot.storage;

public enum BotSettingName {
	PIN_CHANNEL("The channel to send custom pins to."),
	DEV_ANNOUNCE_CHANNEL("The channel to receive important developer announcements."),
	MUTE_DEV_ANNOUNCES("Whether to mute developer announcements."),
	PIN_POLLS("Whether to pin Polls to the current channel."),
	REMOVAL_EMOTE("The Emote to use as a \"Dislike\" button."),
	REMOVAL_THRESHOLD("The number of Dislikes a post must get to be removed and increase the user's bad tally."),
	APPROVAL_THRESHOLD("The number of Likes a post must get to increase the user's good tally."),
	APPROVAL_EMOTE("The Emote to use as a \"Like\" button."),
	ALLOW_MODIFY("Sets the role (other than Administrator) allowed to use the Modify command."),
	ALLOW_NICKNAME("Sets the role (other than Administrator) allowed to change the bot's nickname."),
	ALLOW_POLLS("Sets the role (other than Administrator) allowed to start and end Polls."),
	RESET_EMOTE_STATS("Sets the role (other than Administrator) allowed to reset the emoji counts."),
	MANAGE_MUSIC("Sets the role (other than Administrator) allowed to play and manage music."),
	SERVER_LANG("Sets which language Locale preset to use on this server.");

	private String description;

	BotSettingName(String desc){
		this.description = desc;
	}

	public String getDescription(){
		return description;
	}

	public static boolean contains(String opt){
		for(BotSettingName v : BotSettingName.values()){
			if(v.toString().equalsIgnoreCase(opt)){
				return true;
			}
		}

		return false;
	}
}
