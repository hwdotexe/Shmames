package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.App;
import com.hadenwatne.shmames.Shmames;
import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.models.command.ShmamesCommandArguments;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;

import java.util.LinkedHashMap;

public class WhatAreTheOdds implements ICommand {
	private final CommandStructure commandStructure;

	public WhatAreTheOdds() {
		this.commandStructure = CommandBuilder.Create("whataretheodds", "Get the odds out of 100 of something happening.")
				.addAlias("what are the odds")
				.addParameters(
						new CommandParameter("query", "The event to determine the odds of.", ParameterType.STRING)
								.setExample("I become famous")
				)
				.build();
	}

	@Override
	public CommandStructure getCommandStructure() {
		return this.commandStructure;
	}

	@Override
	public String run(Lang lang, Brain brain, ShmamesCommandData data) {
		String query = data.getArguments().getAsString("query");

		for (ICommand c : App.Shmames.getCommandHandler().getLoadedCommands()) {
			if (c.getCommandStructure().getName().equalsIgnoreCase("roll")) {
				String prefix = "\"What are the odds " + query + "\"\n";
				LinkedHashMap<String, Object> rollArgs = new LinkedHashMap<>();

				rollArgs.put("dice", "1d100");

				ShmamesCommandData rollData = new ShmamesCommandData(
						c,
						new ShmamesCommandArguments(rollArgs),
						data.getMessagingChannel(),
						App.Shmames.getJDA().getSelfUser(),
						data.getServer()
				);

				return prefix + c.run(lang, brain, rollData);
			}
		}

		return null;
	}
	
	@Override
	public boolean requiresGuild() {
		return false;
	}
}
