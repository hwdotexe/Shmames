package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.enums.Langs;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import net.dv8tion.jda.api.EmbedBuilder;

public class Blame extends Command {
	public Blame() {
		super(false);
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
	public EmbedBuilder run (ExecutingCommand executingCommand) {
		String randomAnswer = executingCommand.getLanguage().getMsg(Langs.BLAME_OPTIONS);
		String answerMessage = executingCommand.getLanguage().getMsg(Langs.BLAME, new String[]{randomAnswer});
		String question = executingCommand.getCommandArguments().getAsString("item");

		return response(EmbedType.INFO)
				.addField(question, answerMessage, false);
	}
}
