package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.enums.Langs;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import net.dv8tion.jda.api.EmbedBuilder;

// TODO watch this command for uses, and delete if no longer used.
public class Enhance extends Command {
	public Enhance() {
		super(false);
	}

	@Override
	protected CommandStructure buildCommandStructure() {
		return CommandBuilder.Create("enhance", "Enhance things.")
				.addParameters(
						new CommandParameter("thing", "The item you want to enhance.", ParameterType.STRING)
								.setExample("the national debt")
				)
				.build();
	}

	@Override
	public EmbedBuilder run (ExecutingCommand executingCommand) {
		String item = executingCommand.getCommandArguments().getAsString("thing");
		String answer = executingCommand.getLanguage().getMsg(Langs.ENHANCE_OPTIONS, new String[]{item});

		return response(EmbedType.SUCCESS)
				.addField(item, answer, false);
	}
}