package tech.hadenw.discordbot.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import tech.hadenw.discordbot.Shmames;
import tech.hadenw.discordbot.brain.Brain;

public class ResetEmoteStats implements ICommand {
	@Override
	public String getDescription() {
		return "Reset emote usage statistics.";
	}
	
	@Override
	public String getUsage() {
		return "resetEmoteStats";
	}

	@Override
	public String run(String args, User author, Message message) {
		Brain b = Shmames.getBrains().getBrain(message.getGuild().getId());
		
		b.getEmoteStats().clear();
		Shmames.getBrains().saveBrain(b);

		return "We didn't need those anyway ;} #StatsCleared!";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"resetemotestats", "reset emote stats"};
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
