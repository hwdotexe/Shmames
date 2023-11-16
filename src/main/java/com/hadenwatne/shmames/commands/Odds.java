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

public class Odds extends Command {
	public Odds() {
		super(false);
	}

	@Override
	protected Permission[] configureRequiredBotPermissions() {
		return new Permission[]{Permission.MESSAGE_SEND, Permission.MESSAGE_SEND_IN_THREADS, Permission.MESSAGE_EMBED_LINKS};
	}

	@Override
	protected Permission[] configureRequiredUserPermissions() {
		return new Permission[0];
	}

	@Override
	protected CommandStructure buildCommandStructure() {
		return CommandBuilder.Create("odds", "Get the odds out of 100 of something happening.")
				.addParameters(
						new CommandParameter("query", "The event to determine the odds of.", ParameterType.STRING)
								.setExample("I become famous")
				)
				.build();
	}

	@Override
	public void onCommandFailure(Execution execution) {

	}

	@Override
	public void run(Execution execution) {
		String query = execution.getArguments().get("query").getAsString();
		int result = RandomService.GetRandom(100) + 1;
		String reply = execution.getLanguageProvider().getMessageFromKey(execution, LanguageKey.ODDS.name(), Integer.toString(result));

		CorvusBuilder builder = Corvus.info(execution.getBot());

		builder.addBreadcrumbs(this.getCommandStructure().getName())
				.addField(query, reply, false);

		Corvus.reply(execution, builder);
	}
}
