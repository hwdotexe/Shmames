package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.enums.Langs;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;

public class Blame implements ICommand {
	private final CommandStructure commandStructure;

	public Blame() {
		this.commandStructure = CommandBuilder.Create("blame", "I'll blame stuff for you.")
				.addAlias("why")
				.addParameters(
						new CommandParameter("item", "The item to blame", ParameterType.STRING)
				)
				.setExample("blame cropcircles")
				.build();
	}

	@Override
	public CommandStructure getCommandStructure() {
		return this.commandStructure;
	}

	@Override
	public String run (Lang lang, Brain brain, ShmamesCommandData data) {
		if (brain != null) {
			if (brain.getJinping()) {
				return lang.getMsg(Langs.BLAME, new String[]{"Jinping"});
			}
		}

		String randomAnswer = lang.getMsg(Langs.BLAME_OPTIONS);
		String answerMessage = lang.getMsg(Langs.BLAME, new String[]{randomAnswer});

		if (data.getMessagingChannel().hasHook()) {
			String question = data.getArguments().getAsString("item");

			return "> _Why " + question + "_\n" + answerMessage;
		}

		return answerMessage;
	}
	
	@Override
	public boolean requiresGuild() {
		return false;
	}
}
