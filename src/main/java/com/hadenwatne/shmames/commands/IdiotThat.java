package com.hadenwatne.shmames.commands;

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

public class IdiotThat implements ICommand {
	private final CommandStructure commandStructure;

	public IdiotThat() {
		this.commandStructure = CommandBuilder.Create("idiotthat", "Rewrite a previous message with poor grammar.")
				.addParameters(
						new CommandParameter("position", "A number of carats (^) pointing to the message", ParameterType.STRING)
								.setPattern("([\\^]{1,15})")
				)
				.build();
	}

	@Override
	public CommandStructure getCommandStructure() {
		return this.commandStructure;
	}

	@Override
	public String getExamples() {
		return "`idiotthat ^^`";
	}

	@Override
	public String run(Lang lang, Brain brain, ShmamesCommandData data) {
		int messages = data.getArguments().getAsString("position").length();

		try {
			Message toIdiot = Utils.GetMessageIndicated(data.getMessagingChannel(), messages);
			String idiot = toIdiot.getContentDisplay();

			return runIdiotProcess(idiot);
		} catch (InsufficientPermissionException ex) {
			ex.printStackTrace();

			return lang.getError(Errors.NO_PERMISSION_BOT, true);
		}
	}

	@Override
	public boolean requiresGuild() {
		return false;
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