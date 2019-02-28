package tech.hadenw.shmamesbot;

public class Errors {
	public static final String NO_PERMISSION_BOT = "I don't think I'm allowed to do that. _[Bot: No Permission]_";
	public static final String WRONG_USAGE = "I don't think that's how you do it. _[Wrong Usage]_";
	public static final String COMMAND_NOT_FOUND = "I couldn't find that command.";
	public static final String GUILD_REQUIRED = "That command must be run on a server. _[Guild Required]_";
	public static final String BOT_ERROR = "I sense a plot to destroy me. _[Bot Error]_";
	public static final String HEY_THERE = "Hey there! Try using `"+Shmames.getJDA().getSelfUser().getName().toLowerCase()+" help`!";
	public static final String NO_PERMISSION_USER = "I'm afraid I can't let you do that. _[User: No Permission]_";
}