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
import com.hadenwatne.shmames.services.RandomService;
import net.dv8tion.jda.api.Permission;

public class When extends Command {
	public When() {
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
	protected CommandStructure buildCommandStructure() {
		return CommandBuilder.Create("when", "I'll tell you when something will happen.")
				.addParameters(
						new CommandParameter("event", "The event that will happen later.", ParameterType.STRING)
								.setExample("will I get rich")
				)
				.build();
	}

	@Override
	public void onCommandFailure(Execution execution) {

	}

	@Override
	public void run(Execution execution) {
		String msg = execution.getLanguageProvider().getMessageFromKey(execution, LanguageKey.WHEN_OPTIONS.name(), Integer.toString(RandomService.GetRandom(150) + 1));
		String question = execution.getArguments().get("event").getAsString();

		CorvusBuilder builder = Corvus.info(execution.getBot());

		builder.addBreadcrumbs(this.getCommandStructure().getName())
						.addField(question, msg, false);

		Corvus.reply(execution, builder);
	}
}
