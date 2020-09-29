package com.hadenwatne.discordbot.commands;

import com.hadenwatne.discordbot.storage.Errors;
import com.hadenwatne.discordbot.storage.Lang;
import com.hadenwatne.discordbot.storage.Langs;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.discordbot.storage.Brain;

import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddTally implements ICommand {
	private Lang lang;
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
		Matcher m = Pattern.compile("^[\\w\\d\\s]+$", Pattern.CASE_INSENSITIVE).matcher(args);

		if(m.find()) {
			String tally = m.group().trim().replaceAll("\\s", "_").replaceAll("\\W", "").toLowerCase();

			if (brain.getTallies().containsKey(tally)) {
				brain.getTallies().put(tally, brain.getTallies().get(tally) + 1);
			} else {
				brain.getTallies().put(tally, 1);
			}

			return lang.getMsg(Langs.TALLY_CURRENT_VALUE, new String[]{ tally, brain.getTallies().get(tally).toString() });
		}else{
			return lang.getError(Errors.WRONG_USAGE, true);
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"addtally", "add tally", "add a tally to"};
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
