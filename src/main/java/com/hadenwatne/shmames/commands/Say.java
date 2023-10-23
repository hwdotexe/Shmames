package com.hadenwatne.shmames.commands;

import com.hadenwatne.botcore.command.Command;
import com.hadenwatne.shmames.App;
import com.hadenwatne.botcore.command.builder.CommandBuilder;
import com.hadenwatne.botcore.command.builder.CommandParameter;
import com.hadenwatne.botcore.command.builder.CommandStructure;
import com.hadenwatne.botcore.command.builder.ParameterType;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.services.MessageService;
import com.hadenwatne.shmames.services.ShmamesService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.exceptions.PermissionException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Say extends Command {
	public Say() {
		super(false);
	}

	@Override
	protected Permission[] configureRequiredBotPermissions() {
		return new Permission[]{Permission.MESSAGE_SEND, Permission.MESSAGE_SEND_IN_THREADS, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_EXT_EMOJI};
	}

	@Override
	protected CommandStructure buildCommandStructure() {
		return CommandBuilder.Create("say", "I'll repeat after you! Send messages, links, or server emotes!")
				.addAlias("echo")
				.addAlias("repeat")
				.addAlias("simonsays")
				.addParameters(
						new CommandParameter("message", "The message you want me to repeat.", ParameterType.STRING)
								.setExample("Am I kawaii??")
				)
				.build();
	}

	@Override
	public EmbedBuilder run (ExecutingCommand executingCommand) {
		String message = executingCommand.getCommandArguments().getAsString("message");
		boolean sendSimple = false;

		// if is message, delete message and send simple message back.
		// if is hook, build an embed.

		if(executingCommand.hasMessage()) {
			// Delete the message that ran this command, if possible.
			try {
				executingCommand.getMessage().delete().queue();
			} catch (PermissionException ignored) {}

			sendSimple = true;
		}

		if(executingCommand.getServer() != null) {
			Matcher m = Pattern.compile("(?!<):([\\w\\d_]+):(?!\\d+>)", Pattern.CASE_INSENSITIVE).matcher(message);
			Brain brain = executingCommand.getBrain();

			while (m.find()) {
				String eName = m.group(1);
				RichCustomEmoji emote = ShmamesService.GetFamilyEmote(eName, brain, executingCommand.getServer());

				if(emote != null) {
					// Replace the emote name with the emote mention.
					message = message.replaceFirst(m.group(), emote.getAsMention());

					// Tally the emote
					Brain emoteBrain = App.Shmames.getStorageService().getBrain(emote.getGuild().getId());
					String eID = Long.toString(emote.getIdLong());

					ShmamesService.IncrementEmoteTally(emoteBrain, eID);
				}
			}
		}

		if(sendSimple) {
			MessageService.SendSimpleMessage(executingCommand.getChannel(), message);

			return null;
		} else {
			return response(EmbedType.INFO)
					.setDescription(message);
		}
	}
}
