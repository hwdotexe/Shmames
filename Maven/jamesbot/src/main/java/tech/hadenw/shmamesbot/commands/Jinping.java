package tech.hadenw.shmamesbot.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import tech.hadenw.shmamesbot.JinpingTask;
import tech.hadenw.shmamesbot.Shmames;
import tech.hadenw.shmamesbot.brain.Brain;

public class Jinping implements ICommand {
	@Override
	public String getDescription() {
		return ":ping_pong:";
	}
	
	@Override
	public String getUsage() {
		return "jinping";
	}

	@Override
	public String run(String args, User author, Message message) {
		Brain b = Shmames.getBrains().getBrain(message.getGuild().getId());
		new JinpingTask(b);
		return "SPAM :ping_pong: THIS :ping_pong: PONG :ping_pong: TO :ping_pong: FREE :ping_pong: HONG :ping_pong: KONG";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"jinping"};
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
