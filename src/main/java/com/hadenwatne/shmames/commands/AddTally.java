package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.models.Lang;
import com.hadenwatne.shmames.enums.Langs;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.shmames.models.Brain;

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
	public String getExamples() {
		return "`addtally professor_trips_on_hdmi_cord`";
	}

	@Override
	public String run(String args, User author, Message message) {
		Matcher m = Pattern.compile("^[\\w\\d\\s]+$", Pattern.CASE_INSENSITIVE).matcher(args);

		if(m.find()) {
			String tally = m.group().trim().replaceAll("\\s", "_").replaceAll("\\W", "").toLowerCase();
			int tallyNum = 0;

			if (brain.getTallies().containsKey(tally)) {
				tallyNum = brain.getTallies().get(tally) + 1;
			} else {
				tallyNum = 1;
			}

			brain.getTallies().put(tally, tallyNum);

			return lang.getMsg(Langs.TALLY_CURRENT_VALUE, new String[]{ tally, Integer.toString(tallyNum) });
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
