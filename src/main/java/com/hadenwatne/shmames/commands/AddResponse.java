package com.hadenwatne.shmames.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.models.Lang;
import com.hadenwatne.shmames.enums.Langs;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.shmames.enums.TriggerType;
import com.hadenwatne.shmames.models.Brain;
import com.hadenwatne.shmames.models.Response;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import javax.annotation.Nullable;

import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

public class AddResponse implements ICommand {
	private final CommandStructure commandStructure;

	public AddResponse() {
		CommandParameter triggerType = new CommandParameter("triggerType", "The type of trigger to provoke this response", ParameterType.SELECTION);

		for(TriggerType type : TriggerType.values()) {
			triggerType.addSelectionOptions(type.name());
		}

		this.commandStructure = CommandBuilder.Create("addresponse")
				.addAlias("add response")
				.addParameters(
						triggerType,
						new CommandParameter("responseText", "The actual text of this response", ParameterType.ANY)
				)
				.build();
	}

	@Override
	public CommandStructure getCommandStructure() {
		return this.commandStructure;
	}

	@Override
	public String getDescription() {
		return "Adds a new random response for the chosen trigger type.";
	}
	
	@Override
	public String getUsage() {
		return this.commandStructure.getUsage();
	}

	@Override
	public String getExamples() {
		return "`addresponse RANDOM Your mother was a hamster!`";
	}

	@Override
	public String run (Lang lang, Brain brain, HashMap<String, String> args, User author, MessageChannel channel) {
		String nrtype = args.get("triggerType");
		String newresp = args.get("responseText");

		// Safety check
		if (TriggerType.byName(nrtype) != null) {
			brain.getTriggerResponses().add(new Response(TriggerType.byName(nrtype), newresp));

			return lang.getMsg(Langs.ITEM_ADDED);
		} else {
			StringBuilder types = new StringBuilder();

			for (TriggerType t : TriggerType.values()) {
				if(types.length() > 0)
					types.append(", ");

				types.append("`").append(t.name()).append("`");
			}

			return lang.getMsg(Langs.INVALID_TRIGGER_TYPE, new String[] { types.toString() });
		}
	}
	
	@Override
	public boolean requiresGuild() {
		return true;
	}
}
