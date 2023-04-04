package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.enums.LanguageKeys;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import com.hadenwatne.shmames.services.RandomService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;

public class WhatAreTheOdds extends Command {
	public WhatAreTheOdds() {
		super(false);
	}

	@Override
	protected Permission[] configureRequiredBotPermissions() {
		return new Permission[]{Permission.MESSAGE_SEND, Permission.MESSAGE_SEND_IN_THREADS, Permission.MESSAGE_EMBED_LINKS};
	}

	@Override
	protected CommandStructure buildCommandStructure() {
		return CommandBuilder.Create("whataretheodds", "Get the odds out of 100 of something happening.")
				.addAlias("what are the odds")
				.addParameters(
						new CommandParameter("query", "The event to determine the odds of.", ParameterType.STRING)
								.setExample("I become famous")
				)
				.build();
	}

	@Override
	public EmbedBuilder run (ExecutingCommand executingCommand) {
		String query = executingCommand.getCommandArguments().getAsString("query");
		int result = RandomService.GetRandom(100) + 1;

		return response(EmbedType.INFO)
				.addField(query, executingCommand.getLanguage().getMsg(LanguageKeys.WHAT_ARE_THE_ODDS, new String[]{Integer.toString(result)+"%"}), false);
	}
}
