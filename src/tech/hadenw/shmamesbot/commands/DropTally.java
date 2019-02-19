package tech.hadenw.shmamesbot.commands;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import tech.hadenw.shmamesbot.Shmames;
import tech.hadenw.shmamesbot.brain.Brain;

public class DropTally implements ICommand {
	@Override
	public String getDescription() {
		return "Decrements a tally on the abacus.";
	}
	
	@Override
	public String getUsage() {
		return "droptally tallyName";
	}

	@Override
	public String run(String args, User author, Message message) {
		Brain b = Shmames.getBrains().getBrain(message.getGuild().getId());
		
		if (b.getTallies().containsKey(args)) {
			int tallies = b.getTallies().get(args);

			if (tallies - 1 < 1) {
				b.getTallies().remove(args);
				Shmames.getBrains().saveBrain(b);
				
				return "`" + args + "` hast been removed, sire";
			} else {
				b.getTallies().put(args, tallies - 1);
				Shmames.getBrains().saveBrain(b);
				
				return "Current tallies for `" + args + "`: `" + b.getTallies().get(args) + "`";
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
		return i.replaceAll("\\s", "_").replaceAll("[\\W]", "").toLowerCase();
	}
	
	@Override
	public boolean requiresGuild() {
		return true;
	}
}
