package tech.hadenw.shmamesbot.commands;

public interface ICommand {
	/**
	 * Returns some help text for how to use this command.
	 * @return Usage information.
	 */
	String getUsage();
	
	/**
	 * Runs the command code.
	 * @param args Command arguments.
	 * @return A string response.
	 */
	String run(String args);
	
	/**
	 * Returns a list of command aliases for this item.
	 * @return A list of aliases.
	 */
	String[] getAliases();
}
