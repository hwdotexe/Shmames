package com.hadenwatne.shmames.commands;

import com.hadenwatne.fornax.command.Command;
import com.hadenwatne.fornax.command.Execution;
import com.hadenwatne.fornax.command.builder.CommandBuilder;
import com.hadenwatne.fornax.command.builder.CommandParameter;
import com.hadenwatne.fornax.command.builder.CommandStructure;
import com.hadenwatne.fornax.command.builder.types.ParameterType;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.enums.LanguageKeys;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;

public class Blame extends Command {
	public Blame() {
		super(false);
	}

	@Override
	protected Permission[] configureRequiredBotPermissions() {
		return new Permission[]{Permission.MESSAGE_SEND, Permission.MESSAGE_SEND_IN_THREADS, Permission.MESSAGE_EMBED_LINKS};
	}

	@Override
	protected CommandStructure buildCommandStructure() {
		return CommandBuilder.Create("blame", "I'll blame stuff for you.")
				.addAlias("why")
				.addParameters(
						new CommandParameter("item", "The item to blame", ParameterType.STRING)
								.setExample("cropcircles")
				)
				.build();
	}

	@Override
	public EmbedBuilder run (Execution execution) {
		String randomAnswer = executingCommand.getLanguage().getMsg(LanguageKeys.BLAME_OPTIONS);
		String answerMessage = executingCommand.getLanguage().getMsg(LanguageKeys.BLAME, new String[]{randomAnswer});
		String question = execution.getArguments().getFirst().getAsString();

		return response(EmbedType.INFO)
				.addField(question, answerMessage, false);
	}
}
