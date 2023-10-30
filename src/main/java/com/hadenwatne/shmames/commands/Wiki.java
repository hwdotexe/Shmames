package com.hadenwatne.shmames.commands;

import com.hadenwatne.fornax.command.Command;
import com.hadenwatne.fornax.command.builder.CommandBuilder;
import com.hadenwatne.fornax.command.builder.CommandParameter;
import com.hadenwatne.fornax.command.builder.CommandStructure;
import com.hadenwatne.fornax.command.builder.types.ParameterType;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import com.hadenwatne.shmames.services.HTTPService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;

public class Wiki extends Command {
	public Wiki() {
		super(false);
	}

	@Override
	protected Permission[] configureRequiredBotPermissions() {
		return new Permission[]{Permission.MESSAGE_SEND, Permission.MESSAGE_SEND_IN_THREADS, Permission.MESSAGE_EMBED_LINKS};
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
