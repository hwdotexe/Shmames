package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.models.Brain;
import com.hadenwatne.shmames.models.Lang;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import javax.annotation.Nullable;
import java.util.List;

public interface ICommand {
	/**
	 * Returns the CommandData object used in slash
	 * commands and to standardize parameters.
	 * @return CommandData
	 */
	CommandData getCommandData();

	/**
	 * Returns a description of the command.
	 * @return About information.
	 */
	String getDescription();
	
	/**
	 * Returns the basic command structure string.
	 * @return Usage information.
	 */
	String getUsage();

	/**
	 * Returns command examples.
	 * @return Command examples.
	 */
	String getExamples();
	
	/**
	 * Runs the command code.
	 * @param args Command arguments.
	 * @param author The user trying to run the command.
	 * @param message The message that triggered this command.
	 * @return A string response, if applicable.
	 */
	String run(List<OptionMapping> args, User author, MessageChannel channel);
	
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
