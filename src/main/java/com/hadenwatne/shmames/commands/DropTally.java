package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.models.Lang;
import com.hadenwatne.shmames.enums.Langs;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.models.Brain;

import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	public String getExamples() {
		return "`droptally professor_trips_on_hdmi_cord`";
	}

	@Override
	public String run(String args, User author, Message message) {
		Matcher m = Pattern.compile("^[\\w\\d\\s]+$", Pattern.CASE_INSENSITIVE).matcher(args);

		if(m.find()) {
			String tally = m.group().trim().replaceAll("\\s", "_").replaceAll("\\W", "").toLowerCase();

			if (brain.getTallies().containsKey(tally)) {
				int tallies = brain.getTallies().get(tally);

				if (tallies - 1 < 1) {
					brain.getTallies().remove(tally);

					return lang.getMsg(Langs.TALLY_REMOVED, new String[] { tally });
				} else {
					brain.getTallies().put(tally, tallies - 1);

					return lang.getMsg(Langs.TALLY_CURRENT_VALUE, new String[] { tally, brain.getTallies().get(tally).toString() });
				}
			} else {
				return lang.getError(Errors.NOT_FOUND, true);
			}
		}else{
			return lang.getError(Errors.WRONG_USAGE, true);
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
