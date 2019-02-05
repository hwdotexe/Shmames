package tech.hadenw.shmamesbot.commands;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;

public interface ICommand {
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
	String run(String args, User author, Guild server);
	
	/**
	 * Returns a list of command aliases for this item.
	 * @return A list of aliases.
	 */
	String[] getAliases();
}
