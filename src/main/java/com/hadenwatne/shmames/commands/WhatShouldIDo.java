package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.enums.Langs;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import net.dv8tion.jda.api.EmbedBuilder;

public class WhatShouldIDo extends Command {
	public WhatShouldIDo() {
		super(false);
	}

	@Override
	protected CommandStructure buildCommandStructure() {
		return CommandBuilder.Create("whatshouldido", "Get a randomized, possibly sarcastic suggestion to cure your boredom.")
				.addAlias("what should i do")
				.build();
	}

	@Override
	public EmbedBuilder run (ExecutingCommand executingCommand) {
		String randomIntro = executingCommand.getLanguage().getMsg(Langs.WHATSHOULDIDO_INTRO_OPTIONS);
		String randomAnswer = executingCommand.getLanguage().getMsg(Langs.WHATSHOULDIDO_OPTIONS);

		return response(EmbedType.INFO)
				.setDescription(randomIntro + " " + randomAnswer + "!");
	}
}
