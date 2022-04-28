package com.hadenwatne.shmames.enums;

public enum BotSettingName {
	ALLOW_MODIFY("Sets the role (other than Administrator) allowed to use the Modify command."),
	ALLOW_POLLS("Sets the role (other than Administrator) allowed to start and end Polls."),
	APPROVAL_EMOTE("The Emote to use as a \"Like\" button."),
	APPROVAL_THRESHOLD("The number of Likes a post must get to increase the user's good tally."),
	MANAGE_MUSIC("Sets the role (other than Administrator) allowed to play and manage music."),
	PIN_CHANNEL("The channel to send custom pins to."),
	PIN_POLLS("Whether to pin Polls to the current channel."),
	PRUNE_FW("Sets the role (other than Administrator) allowed to prune Forum Weapons."),
	REMOVAL_EMOTE("The Emote to use as a \"Dislike\" button."),
	REMOVAL_THRESHOLD("The number of Dislikes a post must get to be removed and increase the user's bad tally."),
	RESET_EMOTE_STATS("Sets the role (other than Administrator) allowed to reset the emoji counts."),
	SERVER_LANG("Sets which Lang preset to use on this server."),
	TALLY_REACTIONS("Whether reaction tallies are enabled.");

	private final String description;

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
