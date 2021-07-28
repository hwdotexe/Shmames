package com.hadenwatne.shmames.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	private Lang lang;
	private Brain brain;
	private CommandData commandData;
	private final String[] aliases = new String[] {"addresponse", "add response"};
	private final String description = "Adds a new response to the list for the chosen response type. These are chosen at random.";

	public AddResponse() {
		this.commandData = new CommandData(getAliases()[0], getDescription());

		OptionData triggerType = new OptionData(STRING, "triggertype", "The trigger type to provoke this response.", true);

		for(TriggerType type : TriggerType.values()) {
			triggerType.addChoice(type.name(), type.name());
		}

		this.commandData.addOptions(
				triggerType,
				new OptionData(STRING, "responsetext", "The actual response text.", true)
		);
	}

	@Override
	public CommandData getCommandData() {
		return this.commandData;
	}

	@Override
	public String getDescription() {
		return this.description;
	}
	
	@Override
	public String getUsage() {
		return "addresponse <responseType> <responseText>";
	}

	@Override
	public String getExamples() {
		return "`addresponse RANDOM Your mother was a hamster!`";
	}

	@Override
	public String run (User author, MessageChannel channel) {
		Matcher m = Pattern.compile("^([a-zA-Z]{4,7}) ([\\w\\W]{3,})$").matcher(args);
		
		if(m.find()) {
			String newresp = m.group(2);
			String nrtype = m.group(1);

			// Easter egg: "ronald" -> "hate"
			nrtype = nrtype.toLowerCase().equals("ronald") ? "HATE" : nrtype;
	
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
		} else {
			return lang.wrongUsage(getUsage());
		}
	}

	@Override
	public String[] getAliases() {
		return this.aliases;
	}

	@Override
	public void setRunContext(Lang lang, @Nullable Brain brain) {
		this.lang = lang;
		this.brain = brain;
	}
	
	@Override
	public boolean requiresGuild() {
		return true;
	}
}
