package com.hadenwatne.shmames.commands;

import com.hadenwatne.botcore.command.Command;
import com.hadenwatne.botcore.command.builder.CommandBuilder;
import com.hadenwatne.botcore.command.builder.CommandParameter;
import com.hadenwatne.botcore.command.builder.CommandStructure;
import com.hadenwatne.botcore.command.builder.types.ParameterType;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.enums.LanguageKeys;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;

// TODO watch this command for uses, and delete if no longer used.
public class Enhance extends Command {
	public Enhance() {
		super(false);
	}

	@Override
	protected Permission[] configureRequiredBotPermissions() {
		return new Permission[]{Permission.MESSAGE_SEND, Permission.MESSAGE_SEND_IN_THREADS, Permission.MESSAGE_EMBED_LINKS};
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
		String answer = executingCommand.getLanguage().getMsg(LanguageKeys.ENHANCE_OPTIONS, new String[]{item});

		return response(EmbedType.SUCCESS)
				.addField(item, answer, false);
	}
}