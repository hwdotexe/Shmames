package com.hadenwatne.shmames.commands;

import com.hadenwatne.fornax.command.Command;
import com.hadenwatne.fornax.command.builder.CommandBuilder;
import com.hadenwatne.fornax.command.builder.CommandStructure;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.enums.LanguageKeys;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;

public class WhatShouldIDo extends Command {
	public WhatShouldIDo() {
		super(false);
	}

	@Override
	protected Permission[] configureRequiredBotPermissions() {
		return new Permission[]{Permission.MESSAGE_SEND, Permission.MESSAGE_SEND_IN_THREADS, Permission.MESSAGE_EMBED_LINKS};
	}

	@Override
	protected CommandStructure buildCommandStructure() {
		return CommandBuilder.Create("whatshouldido", "Get a randomized, possibly sarcastic suggestion to cure your boredom.")
				.addAlias("what should i do")
				.build();
	}

	@Override
	public EmbedBuilder run (ExecutingCommand executingCommand) {
		String randomIntro = executingCommand.getLanguage().getMsg(LanguageKeys.WHATSHOULDIDO);
		String randomAnswer = executingCommand.getLanguage().getMsg(LanguageKeys.WHATSHOULDIDO_OPTIONS);

		return response(EmbedType.INFO)
				.setDescription(randomIntro + " " + randomAnswer + "!");
	}
}
