package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.enums.Langs;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;

// TODO watch this command for uses, and delete if no longer used.
public class Enhance implements ICommand {
	private final CommandStructure commandStructure;

	public Enhance() {
		this.commandStructure = CommandBuilder.Create("enhance", "Enhance things.")
				.addParameters(
						new CommandParameter("thing", "The item you want to enhance.", ParameterType.STRING)
				)
				.setExample("enhance the national debt")
				.build();
	}

	@Override
	public CommandStructure getCommandStructure() {
		return this.commandStructure;
	}

	@Override
	public String run(Lang lang, Brain brain, ShmamesCommandData data) {
		String item = data.getArguments().getAsString("thing");
		String answer = lang.getMsg(Langs.ENHANCE_OPTIONS, new String[]{item});

		if (data.getMessagingChannel().hasHook()) {
			return "> _Enhance " + item + "_\n" + answer;
		}

		return answer;
	}

	@Override
	public boolean requiresGuild() {
		return false;
	}
}