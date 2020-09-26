package com.hadenwatne.discordbot.commands;

import com.hadenwatne.discordbot.storage.Locale;
import com.hadenwatne.discordbot.storage.Locales;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.discordbot.Shmames;
import com.hadenwatne.discordbot.storage.Brain;

import javax.annotation.Nullable;

public class AddTally implements ICommand {
	private Locale locale;
	private Brain brain;

	@Override
	public String getDescription() {
		return "Increments a tally, or creates one if it doesn't exist.";
	}
	
	@Override
	public String getUsage() {
		return "addtally <tallyname>";
	}

	@Override
	public String run(String args, User author, Message message) {
		if (brain.getTallies().containsKey(args)) {
			brain.getTallies().put(args, brain.getTallies().get(args) + 1);
		} else {
			brain.getTallies().put(args, 1);
		}

		return locale.getMsg(Locales.TALLY_CURRENT_VALUE, new String[] { args, brain.getTallies().get(args).toString() });
	}

	@Override
	public String[] getAliases() {
		return new String[] {"addtally", "add tally", "add a tally to"};
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
