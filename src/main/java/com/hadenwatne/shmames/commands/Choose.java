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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Choose extends Command {
	public Choose() {
		super(false);
	}

	@Override
	protected CommandStructure buildCommandStructure() {
		return CommandBuilder.Create("choose", "Let me make a decision for you.")
				.addParameters(
						new CommandParameter("thisorthat", "Two options, separated by 'or'.", ParameterType.STRING)
								.setPattern("(.{1,}) or (.{1,})")
								.setExample("Go outside or One more level")
				)
				.build();
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
		CorvusBuilder builder = Corvus.error(execution.getBot());

		builder.addBreadcrumbs(this.getCommandStructure().getName())
				.setDescription("That was wrong!") // TODO use a proper error message
				.setEphemeral();

		Corvus.reply(execution, builder);
	}

	@Override
	public void run(Execution execution) {
		Pattern p = getCommandStructure().getParameters().get(0).getPattern();
		String thisOrThat = execution.getArguments().get("thisorthat").getAsString();
		Matcher m = p.matcher(thisOrThat);

		// The command is already validated. Call m.find() to prepare a new matcher and separate out the arguments.
		m.find();

		int mutator = RandomService.GetRandom(75);
		String response;

		if (mutator < 5) {
			response = "Neither";
		} else if (mutator < 10) {
			response = "Both";
		} else {
			// Starting at group 2 base because the pattern is wrapped inside a named group.
			response = m.group(2 + RandomService.GetRandom(2));
		}

		String choice = execution.getLanguageProvider().getMessageFromKey(LanguageKey.CHOOSE.name(), response);
		CorvusBuilder builder = Corvus.info(execution.getBot());

		builder.addBreadcrumbs(this.getCommandStructure().getName())
						.addField(thisOrThat, choice, false);

		Corvus.reply(execution, builder);
	}
}
