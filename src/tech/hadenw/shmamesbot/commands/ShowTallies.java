package tech.hadenw.shmamesbot.commands;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import tech.hadenw.shmamesbot.Shmames;
import tech.hadenw.shmamesbot.brain.Brain;

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
		String tallies = "The abacus hast recorded thusly:\n";
		Brain b = Shmames.getBrains().getBrain(message.getGuild().getId());

		for (String key : b.getTallies().keySet()) {
			tallies += "`" + key + "`: `" + b.getTallies().get(key) + "`\n";
		}

		return tallies;
	}

	@Override
	public String[] getAliases() {
		return new String[] {"showtallies", "show all the tallies", "show all tallies", "show all the tallier"};
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
