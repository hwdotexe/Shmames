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
		if (Shmames.getBrain().getTallies().containsKey(args)) {
			Shmames.getBrain().getTallies().put(args, Shmames.getBrain().getTallies().get(args) + 1);
		} else {
			Shmames.getBrain().getTallies().put(args, 1);
		}
		
		Shmames.saveBrain();

		return "Current tally for `" + args + "`: `"+ Shmames.getBrain().getTallies().get(args) + "`";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"addtally", "add a tally to"};
	}
	
	@Override
	public String sanitize(String i) {
		return i.replaceAll("\\s", "_").replaceAll("[\\W]", "").toLowerCase();
	}
}
