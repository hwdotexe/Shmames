package com.hadenwatne.shmames.commands;

import com.hadenwatne.fornax.command.Command;
import com.hadenwatne.fornax.command.builder.CommandBuilder;
import com.hadenwatne.fornax.command.builder.CommandParameter;
import com.hadenwatne.fornax.command.builder.CommandStructure;
import com.hadenwatne.fornax.command.builder.types.ParameterType;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.language.LanguageKey;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;

public class Thoughts extends Command {
	public Thoughts() {
		super(false);
	}

	@Override
	protected Permission[] configureRequiredBotPermissions() {
		return new Permission[]{Permission.MESSAGE_SEND, Permission.MESSAGE_SEND_IN_THREADS, Permission.MESSAGE_EMBED_LINKS};
	}

	@Override
	protected CommandStructure buildCommandStructure() {
		return CommandBuilder.Create("thoughts", "Get my randomized opinion on something.")
				.addAlias("what do you think about")
				.addAlias("what do you think of")
				.addParameters(
						new CommandParameter("item", "The item to get my thoughts about", ParameterType.STRING)
								.setExample("my style")
				)
				.build();
	}

	@Override
	public EmbedBuilder run (ExecutingCommand executingCommand) {
		String item = executingCommand.getCommandArguments().getAsString("item");

		return response(EmbedType.INFO)
				.addField(item, executingCommand.getLanguage().getMsg(LanguageKey.THOUGHTS_OPTIONS), false);
	}
}
