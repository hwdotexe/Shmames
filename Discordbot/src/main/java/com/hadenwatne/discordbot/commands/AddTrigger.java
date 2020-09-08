package com.hadenwatne.discordbot.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.discordbot.Errors;
import com.hadenwatne.discordbot.Shmames;
import com.hadenwatne.discordbot.TriggerType;
import com.hadenwatne.discordbot.storage.Brain;

public class AddTrigger implements ICommand {
	@Override
	public String getDescription() {
		return "Creates a new trigger word or phrase, which then sends a response for the " +
				"given type.";
	}
	
	@Override
	public String getUsage() {
		return "addtrigger <triggerType> <triggerWord>";
	}

	@Override
	public String run(String args, User author, Message message) {
		Matcher m = Pattern.compile("^([a-zA-Z]{3,7}) ([\\w \\-]{3,})$").matcher(args);
		
		if(m.find()) {
			Brain b = Shmames.getBrains().getBrain(message.getGuild().getId());
			String newtrigger = m.group(2);
			String nttype = m.group(1);

			// Easter egg: "ronald" -> "hate"
			nttype = nttype.toLowerCase().equals("ronald") ? "HATE" : nttype;
			
			if (!b.getTriggers().keySet().contains(newtrigger)) {
				if (TriggerType.byName(nttype) != null) {
					b.getTriggers().put(newtrigger, TriggerType.byName(nttype));
					
					return "I will now send a `" + TriggerType.byName(nttype).toString()+ "` response when I hear `" + newtrigger + "`!";
				} else {
					String types = "";

					for (TriggerType t : TriggerType.values()) {
						if(types.length() > 0)
							types += ", ";
						
						types += "`" + t.name() + "`";
					}

					return ":scream: Invalid trigger type! Your options are: " + types;
				}
			} else {
				return "Good idea, but that trigger already exists :sob:";
			}
		} else {
			return Errors.formatUsage(Errors.WRONG_USAGE, getUsage());
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"addtrigger", "add trigger"};
	}
	
	@Override
	public String sanitize(String i) {
		return i.toLowerCase();
	}
	
	@Override
	public boolean requiresGuild() {
		return true;
	}
}
