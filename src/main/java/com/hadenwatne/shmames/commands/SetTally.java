package com.hadenwatne.shmames.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hadenwatne.shmames.models.Lang;
import com.hadenwatne.shmames.enums.Langs;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.shmames.models.Brain;

import javax.annotation.Nullable;

public class SetTally implements ICommand {
	private Lang lang;
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
		Matcher m = Pattern.compile("^([\\w\\d\\s]+)\\s(\\d{1,3})$", Pattern.CASE_INSENSITIVE).matcher(args);
		
		if(m.find()) {
			String tally = m.group(1).trim().replaceAll("\\s", "_").replaceAll("\\W", "").toLowerCase();
			int count = Integer.parseInt(m.group(2));

			if(count > 0) {
				brain.getTallies().put(tally, count);

				return lang.getMsg(Langs.TALLY_CURRENT_VALUE, new String[] { tally, Integer.toString(count) });
			} else {
				brain.getTallies().remove(tally);

				return lang.getMsg(Langs.TALLY_REMOVED, new String[] { tally });
			}
		} else {
			return lang.wrongUsage(getUsage());
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"settally", "set tally"};
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
