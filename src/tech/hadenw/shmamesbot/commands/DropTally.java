package tech.hadenw.shmamesbot.commands;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import tech.hadenw.shmamesbot.Shmames;

public class DropTally implements ICommand {
	@Override
	public String getDescription() {
		return "Decrements a tally.";
	}

	@Override
	public String run(String args, User author, Guild server) {
		String toTally = sanitize(args).trim().replaceAll(" ", "_");

		if (Shmames.getBrain().getTallies().containsKey(toTally)) {
			int tallies = Shmames.getBrain().getTallies().get(toTally);

			if (tallies - 1 < 1) {
				Shmames.getBrain().getTallies().remove(toTally);
				Shmames.saveBrain();
				
				return "`" + toTally + "` hast been removed, sire";
			} else {
				Shmames.getBrain().getTallies().put(toTally, tallies - 1);
				Shmames.saveBrain();
				
				return "Current tallies for `" + toTally + "`: `" + Shmames.getBrain().getTallies().get(toTally) + "`";
			}
		} else {
			return "**I'm sorry " + author.getAsMention() + ", I'm afraid I can't do that.**";
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"droptally", "remove a tally from"};
	}
	
	private String sanitize(String i) {
		return i.replaceAll("[^\\s\\w]", "").toLowerCase();
	}

}
