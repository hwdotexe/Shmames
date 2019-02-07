package tech.hadenw.shmamesbot.commands;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import tech.hadenw.shmamesbot.Shmames;

public class DropTally implements ICommand {
	@Override
	public String getDescription() {
		return "Decrements a tally on the abacus. Usage: `droptally tallyName`";
	}

	@Override
	public String run(String args, User author, Guild server) {
		if (Shmames.getBrain().getTallies().containsKey(args)) {
			int tallies = Shmames.getBrain().getTallies().get(args);

			if (tallies - 1 < 1) {
				Shmames.getBrain().getTallies().remove(args);
				Shmames.saveBrain();
				
				return "`" + args + "` hast been removed, sire";
			} else {
				Shmames.getBrain().getTallies().put(args, tallies - 1);
				Shmames.saveBrain();
				
				return "Current tallies for `" + args + "`: `" + Shmames.getBrain().getTallies().get(args) + "`";
			}
		} else {
			return "**I'm sorry " + author.getAsMention() + ", I'm afraid I can't do that.**";
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"droptally", "remove a tally from"};
	}
	
	@Override
	public String sanitize(String i) {
		return i.replaceAll("[\\W]", "").replaceAll(" ", "_").toLowerCase();
	}
}
