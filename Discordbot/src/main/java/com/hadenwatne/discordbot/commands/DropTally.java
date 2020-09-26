package com.hadenwatne.discordbot.commands;

import com.hadenwatne.discordbot.storage.Locale;
import com.hadenwatne.discordbot.storage.Locales;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.discordbot.Errors;
import com.hadenwatne.discordbot.Shmames;
import com.hadenwatne.discordbot.storage.Brain;

import javax.annotation.Nullable;

public class DropTally implements ICommand {
	private Locale locale;
	private Brain brain;

	@Override
	public String getDescription() {
		return "Decrements a tally, or removes it if the tally reaches 0.";
	}
	
	@Override
	public String getUsage() {
		return "droptally <tallyName>";
	}

	@Override
	public String run(String args, User author, Message message) {
		if (brain.getTallies().containsKey(args)) {
			int tallies = brain.getTallies().get(args);

			if (tallies - 1 < 1) {
				brain.getTallies().remove(args);

				return locale.getMsg(Locales.TALLY_REMOVED, new String[] { args });
			} else {
				brain.getTallies().put(args, tallies - 1);

				return locale.getMsg(Locales.TALLY_CURRENT_VALUE, new String[] { args, brain.getTallies().get(args).toString() });
			}
		} else {
			return Errors.NOT_FOUND;
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"droptally", "drop tally", "removetally", "remove tally"};
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
