package tech.hadenw.shmamesbot.commands;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

public interface ICommand {
	/**
	 * Returns a description of the command.
	 * @return About information.
	 */
	String getDescription();
	
	/**
	 * Returns some help text for how to use this command.
	 * @return Usage information.
	 */
	String getUsage();
	
	/**
	 * Runs the command code.
	 * @param args Command arguments.
	 * @param author The user trying to run the command.
	 * @param server The server this command is running on.
	 * @return A string response, if applicable.
	 */
	String run(String args, User author, Message message);
	
	/**
	 * Returns a list of command aliases for this item.
	 * @return A list of aliases.
	 */
	String[] getAliases();
	
	/**
	 * Sanitizes (or doesn't) the argument string.
	 * @return A sanitized version of the argument string.
	 */
	String sanitize(String args);
	
	/**
	 * Sets whether this command must be run in a Guild.
	 * @return Whether this must be a Guild.
	 */
	boolean requiresGuild();
}
