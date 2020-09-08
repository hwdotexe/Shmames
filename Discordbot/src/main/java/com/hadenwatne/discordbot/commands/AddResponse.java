package com.hadenwatne.discordbot.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.discordbot.Errors;
import com.hadenwatne.discordbot.Shmames;
import com.hadenwatne.discordbot.TriggerType;
import com.hadenwatne.discordbot.storage.Brain;
import com.hadenwatne.discordbot.storage.Response;

public class AddResponse implements ICommand {
	@Override
	public String getDescription() {
		return "Adds a new response to the list for the chosen response type. These are " +
				"chosen at random.";
	}
	
	@Override
	public String getUsage() {
		return "addresponse <responseType> <responseText>";
	}

	@Override
	public String run(String args, User author, Message message) {
		Matcher m = Pattern.compile("^([a-zA-Z]{4,7}) ([\\w\\W]{3,})$").matcher(args);
		
		if(m.find()) {
			Brain b = Shmames.getBrains().getBrain(message.getGuild().getId());
			String newresp = m.group(2);
			String nrtype = m.group(1);

			// Easter egg: "ronald" -> "hate"
			nrtype = nrtype.toLowerCase().equals("ronald") ? "HATE" : nrtype;
	
			if (TriggerType.byName(nrtype) != null) {
				b.getTriggerResponses().add(new Response(TriggerType.byName(nrtype), newresp));

				return "Added :+1:";
			} else {
				String types = "";

				for (TriggerType t : TriggerType.values()) {
					if(types.length() > 0)
						types += ", ";
					
					types += "`" + t.name() + "`";
				}

				return ":scream: Invalid trigger type! Your options are:" + types;
			}
		} else {
			return Errors.formatUsage(Errors.WRONG_USAGE, getUsage());
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"addresponse", "add response"};
	}
	
	@Override
	public String sanitize(String i) {
		return i.replace("@", "");
	}
	
	@Override
	public boolean requiresGuild() {
		return true;
	}
}
