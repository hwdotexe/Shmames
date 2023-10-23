package com.hadenwatne.shmames.commands;

import com.hadenwatne.botcore.command.Command;
import com.hadenwatne.botcore.command.builder.CommandBuilder;
import com.hadenwatne.botcore.command.builder.CommandParameter;
import com.hadenwatne.botcore.command.builder.CommandStructure;
import com.hadenwatne.botcore.command.builder.ParameterType;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.enums.LanguageKeys;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import net.dv8tion.jda.api.EmbedBuilder;
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
	protected CommandStructure buildCommandStructure() {
		return CommandBuilder.Create("8ball", "Shake a Magic 8 Ball and let me see your future.")
				.addParameters(
						new CommandParameter("question", "The question to ask the magic 8 ball.", ParameterType.STRING)
								.setExample("Will I ever find true love?")
				)
				.build();
	}

	@Override
	public EmbedBuilder run (ExecutingCommand executingCommand) {
		String question = executingCommand.getCommandArguments().getAsString("question");
		String answer = executingCommand.getLanguage().getMsg(LanguageKeys.EIGHT_BALL_OPTIONS);

		return response(EmbedType.INFO)
				.addField(question, answer, false);
	}
}