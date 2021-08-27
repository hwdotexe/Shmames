package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import com.hadenwatne.shmames.Utils;

public class Wiki implements ICommand {
	private final CommandStructure commandStructure;

	public Wiki() {
		this.commandStructure = CommandBuilder.Create("wiki")
				.addParameters(
						new CommandParameter("query", "A short search query", ParameterType.STRING)
						.setPattern(".{3,150}")
				)
				.build();
	}

	@Override
	public CommandStructure getCommandStructure() {
		return this.commandStructure;
	}

	@Override
	public String getDescription() {
		return "Ask the oracle your question, and I shall answer. That, or the Internet will.";
	}
	
	@Override
	public String getUsage() {
		return this.commandStructure.getUsage();
	}

	@Override
	public String getExamples() {
		return "`wiki distance between Earth and Jupiter`";
	}

	@Override
	public String run (Lang lang, Brain brain, ShmamesCommandData data) {
		String query = data.getArguments().getAsString("query");

		return Utils.getWolfram(query);
	}
	
	@Override
	public boolean requiresGuild() {
		return false;
	}
}
