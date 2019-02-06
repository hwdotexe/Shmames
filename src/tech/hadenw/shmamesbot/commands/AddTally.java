package tech.hadenw.shmamesbot.commands;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import tech.hadenw.shmamesbot.Shmames;

public class AddTally implements ICommand {
	@Override
	public String getDescription() {
		return "Increments a tally on the abacus. Usage: `addtally tallyName`";
	}

	@Override
	public String run(String args, User author, Guild server) {
		String toTally = sanitize(args).trim().replaceAll(" ", "_");

		if (Shmames.getBrain().getTallies().containsKey(toTally)) {
			Shmames.getBrain().getTallies().put(toTally, Shmames.getBrain().getTallies().get(toTally) + 1);
		} else {
			Shmames.getBrain().getTallies().put(toTally, 1);
		}
		
		Shmames.saveBrain();

		return "Current tally for `" + toTally + "`: `"+ Shmames.getBrain().getTallies().get(toTally) + "`";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"addtally", "add a tally to"};
	}
	
	private String sanitize(String i) {
		return i.replaceAll("[^\\s\\w]", "").toLowerCase();
	}

}
