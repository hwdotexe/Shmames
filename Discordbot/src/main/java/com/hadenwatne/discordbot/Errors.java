package com.hadenwatne.discordbot;

public class Errors {
	public static final String ALREADY_EXISTS = 				"I think you've already done that! _[Already Exists]_";
	public static final String BOT_ERROR = 						"I sense a plot to destroy me. _[Bot Error]_";
	public static final String CANNOT_DELETE = 					"Sorry, I can't let you delete that. It's very precious to me.  _[Cannot Delete]_";
	public static final String CHANNEL_NOT_FOUND = 				"I can't find the correct channel for that. _[Channel Not Found]_";
	public static final String COMMAND_NOT_FOUND = 				"That command hasn't been invented yet!";
	public static final String FAMILY_ALREADY_EXISTS = 			"You already own a family with that name! Please choose a different name. _[Family Already Exists]_";
	public static final String FAMILY_ALREADY_JOINED = 			"This server already belongs to that family! _[Family Already Joined]_";
	public static final String FAMILY_INVALID_DETAIL =	 		"Invalid Family name or Join Code!";
	public static final String FAMILY_NOT_JOINED = 				"That server has not joined that Family! _[Family Not Joined]_";
	public static final String FAMILY_MEMBER_MAXIMUM_REACHED = 	"That family has reached the maximum number of servers! _[Family Member Maximum Reached]_";
	public static final String FAMILY_MAXIMUM_REACHED = 		"You can only join up to 3 families! _[Family Maximum Reached]_";
	public static final String FAMILY_SERVER_LIST_EMPTY = 		"This Family does not contain any servers. _[Family Server List Empty]_";
	public static final String FORUM_WEAPON_MAXIMUM_REACHED = 	"Sorry! I can only keep up to 100 weapons. Please remove some existing weapons before creating more. _[Forum Weapon Maximum Reached]_";
	public static final String FORUM_WEAPON_OWNED_OTHER = 		"That weapon is owned by a different server! _[Forum Weapon Owned Other]_";
	public static final String GUILD_REQUIRED = 				"That command must be run on a server. _[Guild Required]_";
	public static final String HANGMAN_ALREADY_GUESSED = 		"You've already guessed that letter!";
	public static final String HANGMAN_NOT_STARTED = 			"There isn't a Hangman game running! Try starting one.";
	public static final String HEY_THERE = 						"Hey there! Try using `"+Shmames.getBotName()+" help`!";
	public static final String INCOMPLETE = 					"I'm gonna need a few more details. _[Incomplete Command]_";
	public static final String INCORRECT_ITEM_COUNT = 			"You've supplied an incorrect number of thingz! _[Incorrect Item Count]_";
	public static final String ITEMS_NOT_FOUND =	 			"There weren't any results.";
	public static final String NO_PERMISSION_BOT = 				"I ran into some trouble with the law... _[Bot: No Permission]_";
	public static final String NO_PERMISSION_USER =				"I'm afraid I can't let you do that. _[User: No Permission]_";
	public static final String NOT_FOUND = 						"That thing you said... I'm not sure what it is. _[Not Found]_";
	public static final String RESERVED_WORD = 					"Sorry, you can't use that totally awesome name! _[Reserved Word]_";
	public static final String SERVER_FAMILY_LIST_EMPTY =		"This server does not belong to a Family. _[Server Family List Empty]_";
	public static final String SETTING_NOT_FOUND =				"I couldn't find that setting. _[Not Found]_";
	public static final String TRACK_NOT_PLAYING = 				"There isn't a track playing right now. _[Not Found]_";
	public static final String WRONG_USAGE = 					"I don't think that's how you do it. _[Wrong Usage]_";

	public static String formatUsage(String error, String usage) {
		return error+"\nGive this a try: `"+usage+"`";
	}
}