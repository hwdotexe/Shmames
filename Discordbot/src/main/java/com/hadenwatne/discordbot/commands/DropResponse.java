package com.hadenwatne.discordbot.commands;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.discordbot.Errors;
import com.hadenwatne.discordbot.Shmames;
import com.hadenwatne.discordbot.TriggerType;
import com.hadenwatne.discordbot.storage.Brain;
import com.hadenwatne.discordbot.storage.Response;

public class DropResponse implements ICommand {
	@Override
	public String getDescription() {
		return "Removes an existing response from the list for the specified type. Use the `listResponses` command " +
				"to view response numbers.";
	}
	
	@Override
	public String getUsage() {
		return "dropresponse <responseType> <responseNumber>";
	}

	@Override
	public String run(String args, User author, Message message) {
		Matcher m = Pattern.compile("^([a-zA-Z]{4,7}) (\\d{1,3})$").matcher(args);
		
		if(m.find()) {
			Brain b = Shmames.getBrains().getBrain(message.getGuild().getId());
			int rNum = Integer.parseInt(m.group(2));
			String nrtype = m.group(1);

			// Easter egg: "ronald" -> "hate"
			nrtype = nrtype.toLowerCase().equals("ronald") ? "HATE" : nrtype;
	
			if (TriggerType.byName(nrtype) != null) {
				List<Response> responses = Shmames.getBrains().getBrain(message.getGuild().getId()).getResponsesFor(TriggerType.byName(nrtype));
				
				if(responses.size() >= rNum) {
					Response r = responses.get(rNum-1);
					b.removeTriggerResponse(r);
					
					return "Removed \""+r.getResponse()+"\"!";
				}else {
					return "I couldn't find that response .-.";
				}
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
		return new String[] {"dropresponse", "drop response", "removeresponse", "remove response"};
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
