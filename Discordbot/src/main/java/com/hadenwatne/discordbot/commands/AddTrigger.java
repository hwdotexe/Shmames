package com.hadenwatne.discordbot.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hadenwatne.discordbot.storage.Locale;
import com.hadenwatne.discordbot.storage.Locales;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.discordbot.Errors;
import com.hadenwatne.discordbot.Shmames;
import com.hadenwatne.discordbot.TriggerType;
import com.hadenwatne.discordbot.storage.Brain;

import javax.annotation.Nullable;

public class AddTrigger implements ICommand {
	private Locale locale;
	private Brain brain;

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
			String newtrigger = m.group(2);
			String nttype = m.group(1);

			// Easter egg: "ronald" -> "hate"
			nttype = nttype.toLowerCase().equals("ronald") ? "HATE" : nttype;
			
			if (!brain.getTriggers().keySet().contains(newtrigger)) {
				if (TriggerType.byName(nttype) != null) {
					brain.getTriggers().put(newtrigger, TriggerType.byName(nttype));

					return locale.getMsg(Locales.ADD_TRIGGER_SUCCESS, new String[] { TriggerType.byName(nttype).toString(), newtrigger });
				} else {
					StringBuilder types = new StringBuilder();

					for (TriggerType t : TriggerType.values()) {
						if(types.length() > 0)
							types.append(", ");
						
						types.append("`").append(t.name()).append("`");
					}

					return locale.getMsg(Locales.INVALID_TRIGGER_TYPE, new String[] { types.toString() });
				}
			} else {
				return Errors.ALREADY_EXISTS;
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
	public void setRunContext(Locale locale, @Nullable Brain brain) {
		this.locale = locale;
		this.brain = brain;
	}
	
	@Override
	public boolean requiresGuild() {
		return true;
	}
}
