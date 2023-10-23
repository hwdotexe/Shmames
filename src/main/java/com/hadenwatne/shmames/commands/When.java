package com.hadenwatne.shmames.commands;

import com.hadenwatne.botcore.command.Command;
import com.hadenwatne.botcore.command.builder.CommandBuilder;
import com.hadenwatne.botcore.command.builder.CommandParameter;
import com.hadenwatne.botcore.command.builder.CommandStructure;
import com.hadenwatne.botcore.command.builder.ParameterType;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.enums.LanguageKeys;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import com.hadenwatne.shmames.services.RandomService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class When extends Command {
	public When() {
		super(false);
	}

	@Override
	protected Permission[] configureRequiredBotPermissions() {
		return new Permission[]{Permission.MESSAGE_SEND, Permission.MESSAGE_SEND_IN_THREADS, Permission.MESSAGE_EMBED_LINKS};
	}

	@Override
	protected CommandStructure buildCommandStructure() {
		return CommandBuilder.Create("when", "I'll tell you when something will happen.")
				.addParameters(
						new CommandParameter("event", "The event that will happen later.", ParameterType.STRING)
								.setExample("will I get rich")
				)
				.build();
	}

	@Override
	public EmbedBuilder run (ExecutingCommand executingCommand) {
		String msg = executingCommand.getLanguage().getMsg(LanguageKeys.WHEN_OPTIONS);
		Matcher m = Pattern.compile(executingCommand.getLanguage().wildcard).matcher(msg);
		String question = executingCommand.getCommandArguments().getAsString("event");

		while (m.find()) {
			msg = msg.replaceFirst(m.group(), Integer.toString(RandomService.GetRandom(150) + 1));
		}

		return response(EmbedType.INFO)
				.addField("When " + question, msg, false);
	}
}
