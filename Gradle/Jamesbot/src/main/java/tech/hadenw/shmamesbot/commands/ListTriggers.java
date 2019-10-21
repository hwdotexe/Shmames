package tech.hadenw.shmamesbot.commands;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import tech.hadenw.shmamesbot.Shmames;
import tech.hadenw.shmamesbot.brain.Brain;

public class ListTriggers implements ICommand {
	@Override
	public String getDescription() {
		return "Lists all the message triggers.";
	}
	
	@Override
	public String getUsage() {
		return "listTriggers";
	}

	@Override
	public String run(String args, User author, Message message) {
		String msg = ":small_red_triangle: **Triggers** :small_red_triangle:";
		Brain b = Shmames.getBrains().getBrain(message.getGuild().getId());

		for (String trig : b.getTriggers().keySet()) {
			msg += "\n";
			msg += "`" + trig + "`" + " (" + b.getTriggers().get(trig) + ")";
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
	
	@Override
	public boolean requiresGuild() {
		return true;
	}
}
