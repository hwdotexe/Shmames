package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import com.hadenwatne.shmames.services.HTTPService;
import net.dv8tion.jda.api.EmbedBuilder;

public class Wiki extends Command {
	public Wiki() {
		super(false);
	}

	@Override
	protected CommandStructure buildCommandStructure() {
		return CommandBuilder.Create("wiki", "Ask the oracle your question, and I shall answer. That, or the Internet will.")
				.addParameters(
						new CommandParameter("query", "A short search query", ParameterType.STRING)
								.setPattern(".{3,150}")
								.setExample("mass of Uranus")
				)
				.build();
	}

	@Override
	public EmbedBuilder run (ExecutingCommand executingCommand) {
		String query = executingCommand.getCommandArguments().getAsString("query");

		return response(EmbedType.INFO).setDescription(HTTPService.GetWolfram(query));
	}
}
