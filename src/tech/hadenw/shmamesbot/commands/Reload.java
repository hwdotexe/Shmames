package tech.hadenw.shmamesbot.commands;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;

public class Reload implements ICommand {
	@Override
	public String getUsage() {
		return "reload";
	}

	@Override
	public String run(String args, User author, Guild server) {
		return "This command is incomplete.";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"reload"};
	}
}
