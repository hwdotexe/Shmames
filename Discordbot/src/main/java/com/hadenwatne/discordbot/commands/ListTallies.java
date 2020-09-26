package com.hadenwatne.discordbot.commands;

import java.util.LinkedHashMap;

import com.hadenwatne.discordbot.storage.Locale;
import com.hadenwatne.discordbot.storage.Locales;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.discordbot.Shmames;
import com.hadenwatne.discordbot.Utils;
import com.hadenwatne.discordbot.storage.Brain;

import javax.annotation.Nullable;

public class ListTallies implements ICommand {
	private Locale locale;
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
	public String run(String args, User author, Message message) {
		LinkedHashMap<String, Integer> tSorted = Utils.sortHashMap(brain.getTallies());
		
		String tallies = Utils.GenerateList(tSorted, -1);

		return locale.getMsg(Locales.TALLY_LIST)+"\n"+tallies;
	}

	@Override
	public String[] getAliases() {
		return new String[] {"listtallies", "list tallies", "showtallies", "show tallies"};
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
