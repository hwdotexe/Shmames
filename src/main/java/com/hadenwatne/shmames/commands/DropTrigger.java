package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.storage.Errors;
import com.hadenwatne.shmames.storage.Lang;
import com.hadenwatne.shmames.storage.Langs;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.shmames.Shmames;
import com.hadenwatne.shmames.storage.Brain;

import javax.annotation.Nullable;

public class DropTrigger implements ICommand {
	private Lang lang;
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

					return lang.getMsg(Langs.ITEM_REMOVED, new String[]{ args });
				} else
					return lang.getError(Errors.NOT_FOUND, true);
			} else {
				return lang.getError(Errors.CANNOT_DELETE, true);
			}
		} else {
			return lang.getError(Errors.INCOMPLETE, true);
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"droptrigger", "drop trigger", "remove trigger"};
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
