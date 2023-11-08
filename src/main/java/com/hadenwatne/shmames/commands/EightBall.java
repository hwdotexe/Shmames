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

public class EightBall extends Command {
	public EightBall() {
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
		return CommandBuilder.Create("8ball", "Shake a Magic 8 Ball and let me see your future.")
				.addParameters(
						new CommandParameter("question", "The question to ask the magic 8 ball.", ParameterType.STRING)
								.setExample("Will I ever find true love?")
				)
				.build();
	}

	@Override
	public void onCommandFailure(Execution execution) {

	}

	@Override
	public void run(Execution execution) {
		String question = execution.getArguments().get("question").getAsString();
		String answer = execution.getLanguageProvider().getMessageFromKey(execution, LanguageKey.EIGHT_BALL_OPTIONS.name());
		CorvusBuilder builder = Corvus.info(execution.getBot());

		builder.addBreadcrumbs(this.getCommandStructure().getName())
				.addField(question, answer, false);

		Corvus.reply(execution, builder);
	}
}