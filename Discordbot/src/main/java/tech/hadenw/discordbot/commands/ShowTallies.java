package tech.hadenw.discordbot.commands;

import java.util.LinkedHashMap;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import tech.hadenw.discordbot.Shmames;
import tech.hadenw.discordbot.Utils;
import tech.hadenw.discordbot.storage.Brain;

public class ShowTallies implements ICommand {
	@Override
	public String getDescription() {
		return "Displays all the current tallie on the abacus.";
	}
	
	@Override
	public String getUsage() {
		return "showTallies";
	}

	@Override
	public String run(String args, User author, Message message) {
		String tallies = "";
		Brain b = Shmames.getBrains().getBrain(message.getGuild().getId());
		LinkedHashMap<String, Integer> tSorted = Utils.sortHashMap(b.getTallies());
		
		for(String c : tSorted.keySet()) {
			if(tallies.length() > 0)
				tallies += "\n";
			tallies += "`"+c+"`: "+tSorted.get(c);
		}

		return "**The abacus hast recorded thusly:**\n"+tallies;
	}

	@Override
	public String[] getAliases() {
		return new String[] {"showtallies", "show tallies", "show all the tallies", "show all tallies", "listtallies", "list tallies"};
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
