package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.enums.ErrorKeys;
import com.hadenwatne.shmames.enums.LanguageKeys;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import com.hadenwatne.shmames.services.MessageService;
import com.hadenwatne.shmames.services.TextFormatService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

import java.util.ArrayList;
import java.util.List;

public class React extends Command {
	public React() {
		super(false);
	}

	@Override
	protected CommandStructure buildCommandStructure() {
		return CommandBuilder.Create("react", "Reacts to the specified message with emoji that spell out your word.")
				.addParameters(
						new CommandParameter("position", "A number of carats (^) pointing to the message", ParameterType.STRING)
								.setPattern("([\\^]{1,15})")
								.setExample("^^^"),
						new CommandParameter("word", "the word to react with", ParameterType.STRING)
								.setPattern("\\w+")
								.setExample("dope")
				)
				.build();
	}

	@Override
	public EmbedBuilder run (ExecutingCommand executingCommand) {
		int messages = executingCommand.getCommandArguments().getAsString("position").length();
		String word = executingCommand.getCommandArguments().getAsString("word");

		try {
			Message toReact = MessageService.GetMessageIndicated(executingCommand, messages);

			reactToMessageWithEmoji(toReact, word.toLowerCase());

			return response(EmbedType.SUCCESS)
					.setDescription(executingCommand.getLanguage().getMsg(LanguageKeys.GENERIC_SUCCESS));
		} catch (InsufficientPermissionException e) {
			return response(EmbedType.ERROR, ErrorKeys.NO_PERMISSION_BOT.name())
					.setDescription(executingCommand.getLanguage().getError(ErrorKeys.NO_PERMISSION_BOT));
		}
	}

	private void reactToMessageWithEmoji(Message toReact, String word) {
		List<Character> chars = new ArrayList<Character>();

		for (char letter : word.toCharArray()) {
			if (chars.contains(letter)) {
				String l = TextFormatService.DuplicateLetterToEmoji(letter);

				if (l != null)
					toReact.addReaction(l).queue();

				continue;
			}

			toReact.addReaction(TextFormatService.LetterToEmoji(letter)).queue();
			chars.add(letter);
		}
	}
}
