package com.hadenwatne.shmames.commands;

import java.util.ArrayList;
import java.util.List;

import com.hadenwatne.shmames.Utils;
import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import net.dv8tion.jda.api.entities.Message;
import com.hadenwatne.shmames.enums.Errors;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

public class React implements ICommand {
	private final CommandStructure commandStructure;

	public React() {
		this.commandStructure = CommandBuilder.Create("react", "Reacts to the specified message with emoji that spell out your word.")
				.addParameters(
						new CommandParameter("position", "A number of carats (^) pointing to the message", ParameterType.STRING)
								.setPattern("([\\^]{1,15})"),
						new CommandParameter("word", "the word to react with", ParameterType.STRING)
								.setPattern("\\w+")
				)
				.build();
	}

	@Override
	public CommandStructure getCommandStructure() {
		return this.commandStructure;
	}

	@Override
	public String getExamples() {
		return "`react dope ^^^`";
	}

	@Override
	public String run(Lang lang, Brain brain, ShmamesCommandData data) {
		int messages = data.getArguments().getAsString("position").length();
		String word = data.getArguments().getAsString("word");

		try {
			Message toReact = Utils.GetMessageIndicated(data.getMessagingChannel(), messages);

			reactToMessageWithEmoji(toReact, word);

			// Remove the querying message
			try {
				if (data.getMessagingChannel().hasOriginMessage()) {
					data.getMessagingChannel().getOriginMessage().delete().queue();
				}
			} catch (Exception ignored) {
			}

			return "";
		} catch (InsufficientPermissionException ex) {
			ex.printStackTrace();

			return lang.getError(Errors.NO_PERMISSION_BOT, true);
		}
	}

	@Override
	public boolean requiresGuild() {
		return false;
	}

	private void reactToMessageWithEmoji(Message toReact, String word) {
		List<Character> chars = new ArrayList<Character>();

		for (char letter : word.toCharArray()) {
			if (chars.contains(letter)) {
				String l = Utils.duplicateLetterToEmoji(letter);

				if (l != null)
					toReact.addReaction(l).queue();

				continue;
			}

			toReact.addReaction(Utils.letterToEmoji(letter)).queue();
			chars.add(letter);
		}
	}
}
