package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.models.Brain;
import com.hadenwatne.shmames.models.Lang;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;

public interface ICommand {
	/**
	 * Returns structure data for how to validate
	 * and use this command.
	 * @return A CommandStructure object.
	 */
	CommandStructure getCommandStructure();

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
	 * @param lang The language file to use.
	 * @param brain The brain of the server this command is calling from, if available.
	 * @param args Command arguments map in the format <parameterName, parameterValue>
	 * @param author The user trying to run the command.
	 * @param channel The channel this command is being run in.
	 * @return A string response, if applicable.
	 */
	String run(Lang lang, @Nullable Brain brain, HashMap<String, String> args, User author, MessageChannel channel);

	/**
	 * Sets whether this command must be run in a Guild.
	 * @return Whether this must be a Guild.
	 */
	boolean requiresGuild();
}
