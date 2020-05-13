package tech.hadenw.discordbot.commands;

import java.util.LinkedHashMap;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import tech.hadenw.discordbot.Shmames;
import tech.hadenw.discordbot.Utils;
import tech.hadenw.discordbot.storage.Brain;

public class ListTallies implements ICommand {
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
		Brain b = Shmames.getBrains().getBrain(message.getGuild().getId());
		LinkedHashMap<String, Integer> tSorted = Utils.sortHashMap(b.getTallies());
		
		String tallies = Utils.GenerateList(tSorted, -1);

		return "**Here's what I have written down:**\n"+tallies;
	}

	@Override
	public String[] getAliases() {
		return new String[] {"listtallies", "list tallies", "showtallies", "show tallies"};
	}
	
	@Override
	public String sanitize(String i) {
		return i;
	}
	
	@Override
	public boolean requiresGuild() {
		return true;
	}
}
