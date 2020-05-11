package tech.hadenw.discordbot;

public class Errors {
	public static final String NO_PERMISSION_BOT = "I ran into some trouble with the law... _[Bot: No Permission]_";
	public static final String WRONG_USAGE = "I don't think that's how you do it. _[Wrong Usage]_";
	public static final String COMMAND_NOT_FOUND = "That command hasn't been invented yet!";
	public static final String GUILD_REQUIRED = "That command must be run on a server. _[Guild Required]_";
	public static final String BOT_ERROR = "I sense a plot to destroy me. _[Bot Error]_";
	public static final String HEY_THERE = "Hey there! Try using `"+Shmames.getBotName()+" help`!";
	public static final String NO_PERMISSION_USER = "I'm afraid I can't let you do that. _[User: No Permission]_";
	public static final String NOT_FOUND = "That thing you said... I'm not sure what it is. _[Not Found]_";
	public static final String CHANNEL_NOT_FOUND = "I can't find the correct channel for that. _[Channel Not Found]_";
	public static final String INCOMPLETE = "I'm gonna need a few more details. _[Incomplete Command]_";
	public static final String SETTING_NOT_FOUND = "I couldn't find that setting. _[Not Found]_";

	public static String formatUsage(String error, String usage) {
		return error+"\nGive this a try: `"+usage+"`";
	}
}