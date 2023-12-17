package com.hadenwatne.shmames.commands;

import com.hadenwatne.corvus.Corvus;
import com.hadenwatne.corvus.CorvusBuilder;
import com.hadenwatne.fornax.command.Command;
import com.hadenwatne.fornax.command.Execution;
import com.hadenwatne.fornax.command.builder.CommandBuilder;
import com.hadenwatne.fornax.command.builder.CommandStructure;
import com.hadenwatne.shmames.language.LanguageKey;
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
				.build();
	}

	@Override
	protected Permission[] configureRequiredUserPermissions() {
		return null;
	}

	@Override
	public void onCommandFailure(Execution execution) {

	}

	@Override
	public void run(Execution execution) {
		String randomIntro = execution.getLanguageProvider().getMessageFromKey(execution, LanguageKey.WHATSHOULDIDO.name());
		String randomAnswer = execution.getLanguageProvider().getMessageFromKey(execution, LanguageKey.WHATSHOULDIDO_OPTIONS.name());

		CorvusBuilder builder = Corvus.info(execution.getBot());

		builder.addBreadcrumbs(this.getCommandStructure().getName())
						.setDescription(randomIntro + " " + randomAnswer + "!");

		Corvus.reply(execution, builder);
	}
}
