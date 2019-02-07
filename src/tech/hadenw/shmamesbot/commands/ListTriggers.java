package tech.hadenw.shmamesbot.commands;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import tech.hadenw.shmamesbot.Shmames;

public class ListTriggers implements ICommand {
	@Override
	public String getDescription() {
		return "Lists all the message triggers.";
	}

	@Override
	public String run(String args, User author, Guild server) {
		String msg = "";

		for (String trig : Shmames.getBrain().getTriggers().keySet()) {
			msg += "`" + trig + "`" + " (" + Shmames.getBrain().getTriggers().get(trig) + ")\n";
		}

		return msg;
	}

	@Override
	public String[] getAliases() {
		return new String[] {"listtriggers", "list triggers"};
	}
	
	@Override
	public String sanitize(String i) {
		return i;
	}
}
