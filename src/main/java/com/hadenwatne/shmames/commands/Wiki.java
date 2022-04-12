package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import com.hadenwatne.shmames.services.HTTPService;

public class Wiki implements ICommand {
	private final CommandStructure commandStructure;

	public Wiki() {
		this.commandStructure = CommandBuilder.Create("wiki", "Ask the oracle your question, and I shall answer. That, or the Internet will.")
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
	public String getExamples() {
		return "`wiki distance between Earth and Jupiter`";
	}

	@Override
	public String run (Lang lang, Brain brain, ShmamesCommandData data) {
		String query = data.getArguments().getAsString("query");

		return HTTPService.GetWolfram(query);
	}
	
	@Override
	public boolean requiresGuild() {
		return false;
	}
}
