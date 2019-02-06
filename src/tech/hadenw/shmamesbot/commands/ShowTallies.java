package tech.hadenw.shmamesbot.commands;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import tech.hadenw.shmamesbot.Shmames;

public class ShowTallies implements ICommand {
	@Override
	public String getDescription() {
		return "Displays all the current tallie on the abacus.";
	}

	@Override
	public String run(String args, User author, Guild server) {
		String tallies = "The abacus hast recorded thusly:\n";

		for (String key : Shmames.getBrain().getTallies().keySet()) {
			tallies += "`" + key + "`: `" + Shmames.getBrain().getTallies().get(key) + "`\n";
		}

		return tallies;
	}

	@Override
	public String[] getAliases() {
		return new String[] {"showtallies", "show all the tallies", "show all tallies", "show all the tallier"};
	}
}
