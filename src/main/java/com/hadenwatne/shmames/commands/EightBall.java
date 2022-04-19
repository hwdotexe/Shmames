package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.enums.Langs;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import net.dv8tion.jda.api.EmbedBuilder;

public class EightBall extends Command {
	public EightBall() {
		super(false);
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
		String answer = executingCommand.getLanguage().getMsg(Langs.EIGHT_BALL_OPTIONS);

		return response(EmbedType.INFO)
				.addField(question, answer, false);
	}
}