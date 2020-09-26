package com.hadenwatne.discordbot.commands;

import com.hadenwatne.discordbot.storage.Brain;
import com.hadenwatne.discordbot.storage.Lang;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import javax.annotation.Nullable;

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
	 * @param message The message that triggered this command.
	 * @return A string response, if applicable.
	 */
	String run(String args, User author, Message message);
	
	/**
	 * Returns a list of command aliases for this item.
	 * @return A list of aliases.
	 */
	String[] getAliases();
	
	/**
	 * Used to provide context data to the command.
	 */
	void setRunContext(Lang lang, @Nullable Brain brain);
	
	/**
	 * Sets whether this command must be run in a Guild.
	 * @return Whether this must be a Guild.
	 */
	boolean requiresGuild();
}
