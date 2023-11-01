package com.hadenwatne.shmames.commands;

import com.hadenwatne.corvus.Corvus;
import com.hadenwatne.corvus.CorvusBuilder;
import com.hadenwatne.fornax.command.Command;
import com.hadenwatne.fornax.command.Execution;
import com.hadenwatne.fornax.command.builder.CommandBuilder;
import com.hadenwatne.fornax.command.builder.CommandParameter;
import com.hadenwatne.fornax.command.builder.CommandStructure;
import com.hadenwatne.fornax.command.builder.types.ParameterType;
import com.hadenwatne.shmames.language.LanguageKey;
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
	protected Permission[] configureRequiredUserPermissions() {
		return null;
	}

	@Override
	public void onCommandFailure(Execution execution) {

	}

	@Override
	protected CommandStructure buildCommandStructure() {
		return CommandBuilder.Create("blame", "I'll blame stuff for you.")
				.addParameters(
						new CommandParameter("item", "The item to blame", ParameterType.STRING)
								.setExample("cropcircles")
				)
				.build();
	}

	@Override
	public void run (Execution execution) {
		String randomAnswer = execution.getLanguageProvider().getMessageFromKey(execution, LanguageKey.BLAME_OPTIONS.name());
		String answerMessage = execution.getLanguageProvider().getMessageFromKey(execution, LanguageKey.BLAME.name(), randomAnswer);
		String question = execution.getArguments().get("item").getAsString();

		CorvusBuilder builder = Corvus.info(execution.getBot());

		builder.addBreadcrumbs(this.getCommandStructure().getName())
				.addField(question, answerMessage, false);

		Corvus.reply(execution, builder);
	}
}
