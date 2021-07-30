package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;

import javax.annotation.Nullable;

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
	 * @param data Data about the command call and how to return the result.
	 * @return A string response, if applicable.
	 */
	String run(Lang lang, @Nullable Brain brain, ShmamesCommandData data);

	/**
	 * Sets whether this command must be run in a Guild.
	 * @return Whether this must be a Guild.
	 */
	boolean requiresGuild();
}
