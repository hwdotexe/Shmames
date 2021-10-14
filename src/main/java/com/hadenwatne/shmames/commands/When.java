package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.Utils;
import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.enums.Langs;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;

import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class When implements ICommand {
	private final CommandStructure commandStructure;

	public When() {
		this.commandStructure = CommandBuilder.Create("when", "I'll tell you when something will happen.")
				.addParameters(
						new CommandParameter("event", "The event that will happen later.", ParameterType.STRING)
				)
				.build();
	}

	@Override
	public CommandStructure getCommandStructure() {
		return this.commandStructure;
	}

	@Override
	public String getExamples() {
		return "`when will I get fired?`";
	}

	@Override
	public String run(Lang lang, Brain brain, ShmamesCommandData data) {
		String msg = lang.getMsg(Langs.WHEN_OPTIONS);
		Matcher m = Pattern.compile(lang.wildcard).matcher(msg);

		while (m.find()) {
			msg = msg.replaceFirst(m.group(), Integer.toString(Utils.getRandom(150) + 1));
		}

		if (data.getMessagingChannel().hasHook()) {
			String question = data.getArguments().getAsString("event");

			return "> _When " + question + "_\n" + msg;
		}

		return msg;
	}

	@Override
	public boolean requiresGuild() {
		return false;
	}
}
