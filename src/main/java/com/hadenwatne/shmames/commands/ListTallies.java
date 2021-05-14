package com.hadenwatne.shmames.commands;

import java.util.LinkedHashMap;

import com.hadenwatne.shmames.models.Lang;
import com.hadenwatne.shmames.enums.Langs;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.shmames.Utils;
import com.hadenwatne.shmames.models.Brain;

import javax.annotation.Nullable;

public class ListTallies implements ICommand {
	private Lang lang;
	private Brain brain;

	@Override
	public String getDescription() {
		return "Displays all the current tallies.";
	}
	
	@Override
	public String getUsage() {
		return "listTallies";
	}

	@Override
	public String getExamples() {
		return "`listtallies`";
	}

	@Override
	public String run(String args, User author, Message message) {
		LinkedHashMap<String, Integer> tSorted = Utils.sortHashMap(brain.getTallies());
		
		String tallies = Utils.generateList(tSorted, -1);

		return lang.getMsg(Langs.TALLY_LIST)+"\n"+tallies;
	}

	@Override
	public String[] getAliases() {
		return new String[] {"listtallies", "list tallies", "showtallies", "show tallies"};
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
