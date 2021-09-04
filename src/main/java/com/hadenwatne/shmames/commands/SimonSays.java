package com.hadenwatne.shmames.commands;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import net.dv8tion.jda.api.entities.Emote;
import com.hadenwatne.shmames.Shmames;
import com.hadenwatne.shmames.Utils;
import net.dv8tion.jda.api.exceptions.PermissionException;

import javax.annotation.Nullable;

public class SimonSays implements ICommand {
	private final CommandStructure commandStructure;

	public SimonSays() {
		this.commandStructure = CommandBuilder.Create("simonsays", "I'll repeat after you! Send messages, links, or server emotes!")
				.addAlias("echo")
				.addAlias("repeat")
				.addParameters(
						new CommandParameter("message", "The message you want me to repeat.", ParameterType.STRING)
				)
				.build();
	}

	@Override
	public CommandStructure getCommandStructure() {
		return this.commandStructure;
	}

	@Override
	public String getExamples() {
		return "`simonsays Hey look at me, I'm a talkin' Shmames!`\n" +
				"`simonsays :emoteFromAnotherServer:`";
	}

	@Override
	public String run(Lang lang, Brain brain, ShmamesCommandData data) {
		String message = data.getArguments().getAsString("message");

		// Delete the message that ran this command, if possible.
		try {
			if(data.getMessagingChannel().hasOriginMessage()){
				data.getMessagingChannel().getOriginMessage().delete().queue();
			}
		} catch (PermissionException ignored) {}

		// If the command is being run on a server, check for emotes.
		if(brain != null && data.getServer() != null) {
			Matcher m = Pattern.compile(":([a-zA-Z0-9_]):").matcher(message);

			while (m.find()) {
				String eName = m.group(1);
				Emote emote = Utils.GetFamilyEmote(eName, brain, data.getServer());

				if(emote != null) {
					// Replace the emote name with the emote mention.
					message = message.replaceFirst(m.group(), emote.getAsMention());

					// Tally the emote
					Brain emoteBrain = Shmames.getBrains().getBrain(emote.getGuild().getId());
					String eID = Long.toString(emote.getIdLong());

					Utils.incrementEmoteTally(emoteBrain, eID);
				}
			}
		}

		return message;
	}

	@Override
	public boolean requiresGuild() {
		return false;
	}
}
