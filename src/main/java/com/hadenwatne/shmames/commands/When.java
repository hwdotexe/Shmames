package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.Utils;
import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;

import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class When implements ICommand {
	private final CommandStructure commandStructure;
	private final String[] answers = new String[]{"In %T years", "In %T minutes", "%T days ago", "When pigs fly", "Absolutely never", "Right now, but in a parallel universe",
			"Not sure, ask your mom", "%T years ago", "Once you stop procrastinating", "Once I get elected Chancellor", "After the heat death of the universe",
			"In precisely %T", "On the next full moon", "When the sand in me hourglass be empty", "Time is subjective", "Time is a tool you can put on the wall",
			"Probably within %T days", "I'd say in %T months", "In %T? %T? Maybe %T?", "Between %T and %T centuries", "Sooner shall %T days pass", "%T seconds", "%T hours, %T minutes, and %T seconds",
			"Eventually", "Not in your lifetime, kiddo", "In your dreams", "Right now"};

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
		String msg = answers[Utils.getRandom(answers.length)];
		Matcher m = Pattern.compile("%T").matcher(msg);

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
