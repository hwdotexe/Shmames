package com.hadenwatne.shmames.commands;

import com.hadenwatne.fornax.command.Command;
import com.hadenwatne.fornax.command.builder.CommandBuilder;
import com.hadenwatne.fornax.command.builder.CommandParameter;
import com.hadenwatne.fornax.command.builder.CommandStructure;
import com.hadenwatne.fornax.command.builder.types.ParameterType;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.language.LanguageKey;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import com.hadenwatne.shmames.services.MessageService;
import com.hadenwatne.shmames.services.TextFormatService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;

import java.util.ArrayList;
import java.util.List;

public class React extends Command {
	public React() {
		super(false);
	}

	@Override
	protected Permission[] configureRequiredBotPermissions() {
		return new Permission[]{Permission.MESSAGE_SEND, Permission.MESSAGE_SEND_IN_THREADS, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY, Permission.MESSAGE_ADD_REACTION};
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

		Message toReact = MessageService.GetMessageIndicated(executingCommand, messages);

		reactToMessageWithEmoji(toReact, word.toLowerCase());

		return response(EmbedType.SUCCESS)
				.setDescription(executingCommand.getLanguage().getMsg(LanguageKey.GENERIC_SUCCESS));
	}

	private void reactToMessageWithEmoji(Message toReact, String word) {
		List<Character> chars = new ArrayList<Character>();

		for (char letter : word.toCharArray()) {
			if (chars.contains(letter)) {
				String l = TextFormatService.DuplicateLetterToEmoji(letter);

				if (l != null)
					toReact.addReaction(Emoji.fromUnicode(l)).queue();

				continue;
			}

			toReact.addReaction(Emoji.fromUnicode(TextFormatService.LetterToEmoji(letter))).queue();
			chars.add(letter);
		}
	}
}
