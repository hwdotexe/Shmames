package tech.hadenw.shmamesbot.commands;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import tech.hadenw.shmamesbot.Shmames;

public class Reload implements ICommand {
	@Override
	public String getDescription() {
		return "Reloads my brain from disk.";
	}

	@Override
	public String run(String args, User author, Message message) {
		Shmames.getBrains().reloadBrain(message.getGuild().getId());
		
		return "[Your File] => 10010101 => [My Brain]";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"reload"};
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
