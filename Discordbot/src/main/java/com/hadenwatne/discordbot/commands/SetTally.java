package com.hadenwatne.discordbot.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hadenwatne.discordbot.storage.Locale;
import com.hadenwatne.discordbot.storage.Locales;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.discordbot.Errors;
import com.hadenwatne.discordbot.Shmames;
import com.hadenwatne.discordbot.storage.Brain;

import javax.annotation.Nullable;

public class SetTally implements ICommand {
	private Locale locale;
	private Brain brain;

	@Override
	public String getDescription() {
		return "Overrides a tally with a new value, creating it if it didn't already exist.";
	}
	
	@Override
	public String getUsage() {
		return "settally <tallyname> <count>";
	}

	@Override
	public String run(String args, User author, Message message) {
		Matcher m = Pattern.compile("^(.+) (\\d{1,3})$", Pattern.CASE_INSENSITIVE).matcher(args);
		
		if(m.find()) {
			String tally = m.group(1).replaceAll("\\s", "_").replaceAll("[\\W]", "").toLowerCase();
			int count = Integer.parseInt(m.group(2));
			
			if (brain.getTallies().containsKey(tally)) {
				if(count == 0) {
					brain.getTallies().remove(tally);
					
					return "`" + tally + "` hast been removed, sire";
				}
			}

			brain.getTallies().put(tally, count);

			return locale.getMsg(Locales.TALLY_CURRENT_VALUE, new String[] { tally, Integer.toString(count) });
		} else {
			return Errors.formatUsage(Errors.WRONG_USAGE, getUsage());
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"settally", "set tally"};
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
