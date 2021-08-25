package com.hadenwatne.shmames.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.enums.TriggerType;
import com.hadenwatne.shmames.enums.Langs;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.Utils;

import javax.annotation.Nullable;

public class Choose implements ICommand {
	private final CommandStructure commandStructure;

	public Choose() {
		this.commandStructure = CommandBuilder.Create("choose")
				.addParameters(
						new CommandParameter("thisOrThat", "Two options, separated by 'or'.", ParameterType.STRING)
						.setPattern("(.{1,}) or (.{1,})")
				)
				.build();
	}

	@Override
	public CommandStructure getCommandStructure() {
		return this.commandStructure;
	}

	@Override
	public String getDescription() {
		return "Let me make a decision for you.";
	}
	
	@Override
	public String getUsage() {
		return "choose <item> or <item>";
	}

	@Override
	public String getExamples() {
		return "`choose Go outside or One more level`";
	}

	@Override
	public String run (Lang lang, Brain brain, ShmamesCommandData data) {
		Pattern p = data.getCommand().getCommandStructure().getParameters().get(0).getPattern();
		Matcher m = p.matcher(data.getArguments().getAsString());

		// Using another Matcher to separate out the 2 options inside the command arguments.
		// At this point during runtime, the command has already been validated.
		if (m.find()) {
			int mutator = Utils.getRandom(50);

			if (mutator < 5) { // 10%
				return lang.getMsg(Langs.CHOOSE, new String[]{"Neither"});
			} else if (mutator < 10) { // 20%
				return lang.getMsg(Langs.CHOOSE, new String[]{"Both"});
			} else {
				String c = m.group(1 + Utils.getRandom(2));

				return lang.getMsg(Langs.CHOOSE, new String[]{c});
			}
		} else {
			return lang.getError(Errors.INCOMPLETE, true);
		}
	}
	
	@Override
	public boolean requiresGuild() {
		return false;
	}
}
