package com.hadenwatne.discordbot.commands;

import com.hadenwatne.discordbot.storage.Lang;
import com.hadenwatne.discordbot.storage.Langs;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.discordbot.Errors;
import com.hadenwatne.discordbot.storage.Brain;

import javax.annotation.Nullable;

public class DropTally implements ICommand {
	private Lang lang;
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

				return lang.getMsg(Langs.TALLY_REMOVED, new String[] { args });
			} else {
				brain.getTallies().put(args, tallies - 1);

				return lang.getMsg(Langs.TALLY_CURRENT_VALUE, new String[] { args, brain.getTallies().get(args).toString() });
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
	public void setRunContext(Lang lang, @Nullable Brain brain) {
		this.lang = lang;
		this.brain = brain;
	}
	
	@Override
	public boolean requiresGuild() {
		return true;
	}
}
