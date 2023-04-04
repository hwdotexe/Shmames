package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import com.hadenwatne.shmames.services.MessageService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import com.hadenwatne.shmames.enums.ErrorKeys;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

public class IdiotThat extends Command {
	public IdiotThat() {
		super(false);
	}

	@Override
	protected Permission[] configureRequiredBotPermissions() {
		return new Permission[]{Permission.MESSAGE_SEND, Permission.MESSAGE_SEND_IN_THREADS, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY};
	}

	@Override
	protected CommandStructure buildCommandStructure() {
		return CommandBuilder.Create("idiotthat", "Rewrite a previous message with poor grammar.")
				.addParameters(
						new CommandParameter("position", "A number of carats (^) pointing to the message", ParameterType.STRING)
								.setPattern("([\\^]{1,15})")
								.setExample("^^^")
				)
				.build();
	}

	@Override
	public EmbedBuilder run (ExecutingCommand executingCommand) {
		int messages = executingCommand.getCommandArguments().getAsString("position").length();

		try {
			Message toIdiot = MessageService.GetMessageIndicated(executingCommand, messages);
			String idiot = toIdiot.getContentDisplay();

			return response(EmbedType.INFO).setDescription(runIdiotProcess(idiot));
		} catch (InsufficientPermissionException ex) {
			ex.printStackTrace();

			return response(EmbedType.ERROR)
					.addField(ErrorKeys.NO_PERMISSION_BOT.name(), executingCommand.getLanguage().getError(ErrorKeys.NO_PERMISSION_BOT), false);
		}
	}

	private String runIdiotProcess(String idiotOrig) {
		String idiot = "";

		// PascalCase
		for (String w : idiotOrig.split(" ")) {
			idiot += w.substring(0, 1).toUpperCase() + w.substring(1).toLowerCase();

			if (w.length() > 4 && idiot.endsWith("s")) {
				idiot = idiot.substring(0, idiot.length() - 1);
				idiot += "'s";
			}

			idiot += " ";
		}

		// Exclamation Points
		idiot = idiot.replaceAll("!", "!!1");

		// Horrible Emojis
		idiotOrig = idiot;
		idiot = "";
		for (String w : idiotOrig.split(" ")) {
			idiot += w;

			if (w.equalsIgnoreCase("okay") || w.equalsIgnoreCase("ok"))
				idiot += " :ok_hand:";

			if (w.equalsIgnoreCase("love"))
				idiot += " :heart:";

			if (w.equalsIgnoreCase("lol") || w.equalsIgnoreCase("haha"))
				idiot += " :joy:";

			if (w.equalsIgnoreCase("wow"))
				idiot += " :open_mouth:";

			idiot += " ";
		}

		return idiot;
	}
}