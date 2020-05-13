package tech.hadenw.discordbot.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import tech.hadenw.discordbot.Shmames;
import tech.hadenw.discordbot.storage.Brain;

public class ListTriggers implements ICommand {
	@Override
	public String getDescription() {
		return "Displays all the current message trigger words or phrases, along with " +
				"their types.";
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
