package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.enums.Langs;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import com.hadenwatne.shmames.Utils;

public class EightBall implements ICommand {
	private final CommandStructure commandStructure;

	public EightBall() {
		this.commandStructure = CommandBuilder.Create("8ball", "Shake a Magic 8 Ball and let me see your future.")
				.addParameters(
						new CommandParameter("question", "The question to ask the magic 8 ball.", ParameterType.STRING)
				)
				.build();
	}

	@Override
	public CommandStructure getCommandStructure() {
		return this.commandStructure;
	}

	@Override
	public String getExamples() {
		return "`8ball Am I a pretty girl?`";
	}

	@Override
	public String run(Lang lang, Brain brain, ShmamesCommandData data) {
		String answer = lang.getMsg(Langs.EIGHT_BALL_OPTIONS);

		if (data.getMessagingChannel().hasHook()) {
			String question = data.getArguments().getAsString("question");

			return "> _" + question + "_\n" + answer;
		}

		return answer;
	}

	@Override
	public boolean requiresGuild() {
		return false;
	}
}