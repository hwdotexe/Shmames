package tech.hadenw.shmamesbot.commands;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;

public class Test implements ICommand {
	@Override
	public String getDescription() {
		return "A test command that I'm allowed to break.";
	}

	@Override
	public String run(String args, User author, Guild server) {
		return "Test success...?";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"testing", "test command"};
	}
}
