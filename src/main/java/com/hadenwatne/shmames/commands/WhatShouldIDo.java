package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.enums.Langs;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;

public class WhatShouldIDo implements ICommand {
	private final CommandStructure commandStructure;

	public WhatShouldIDo() {
		this.commandStructure = CommandBuilder.Create("whatshouldido", "Get a randomized, possibly sarcastic suggestion to cure your boredom.")
				.addAlias("what should i do")
				.build();
	}

	@Override
	public CommandStructure getCommandStructure() {
		return this.commandStructure;
	}

	@Override
	public String getExamples() {
		return "`what should i do`";
	}

	@Override
	public String run(Lang lang, Brain brain, ShmamesCommandData data) {
		String randomIntro = lang.getMsg(Langs.WHATSHOULDIDO_INTRO_OPTIONS);
		String randomAnswer = lang.getMsg(Langs.WHATSHOULDIDO_OPTIONS);

		return randomIntro + " " + randomAnswer + "!";
	}

	@Override
	public boolean requiresGuild() {
		return false;
	}
}
