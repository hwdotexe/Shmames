package com.hadenwatne.discordbot.commands;

import com.hadenwatne.discordbot.Errors;
import com.hadenwatne.discordbot.Utils;
import com.hadenwatne.discordbot.storage.Locale;
import com.hadenwatne.discordbot.storage.Locales;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.discordbot.Shmames;
import com.hadenwatne.discordbot.storage.Brain;

import javax.annotation.Nullable;

public class DropTrigger implements ICommand {
	private Locale locale;
	private Brain brain;

	@Override
	public String getDescription() {
		return "Removes an existing trigger word or phrase.";
	}
	
	@Override
	public String getUsage() {
		return "droptrigger <triggerWord>";
	}

	@Override
	public String run(String args, User author, Message message) {
		if(args.length() > 0) {
			if (!args.equalsIgnoreCase(Shmames.getBotName())) {
				if (brain.getTriggers().containsKey(args)) {
					brain.getTriggers().remove(args);

					return locale.getMsg(Locales.ITEM_REMOVED, new String[]{ args });
				} else
					return Errors.NOT_FOUND;
			} else {
				return Errors.CANNOT_DELETE;
			}
		} else {
			return Errors.INCOMPLETE;
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"droptrigger", "drop trigger", "remove trigger"};
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
