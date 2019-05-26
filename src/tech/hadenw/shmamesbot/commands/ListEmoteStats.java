package tech.hadenw.shmamesbot.commands;

import java.util.List;

import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import tech.hadenw.shmamesbot.Shmames;
import tech.hadenw.shmamesbot.brain.Brain;

public class ListEmoteStats implements ICommand {
	@Override
	public String getDescription() {
		return "View emote usage statistics.";
	}
	
	@Override
	public String getUsage() {
		return "listEmoteStats";
	}

	@Override
	public String run(String args, User author, Message message) {
		Brain b = Shmames.getBrains().getBrain(message.getGuild().getId());
		
		String stats = "**Thus sayeth the Shmames:**";
		
		// TODO does not sort
		if(b.getEmoteStats().keySet().size() > 0) {
			for(String em : b.getEmoteStats().keySet()) {
				stats += "\n";
				
				List<Emote> ems = message.getGuild().getEmotesByName(em, false);
				
				if(!ems.isEmpty()) {
					stats += ems.get(0).getAsMention() + ": " + b.getEmoteStats().get(em);
				}
			}
		}else {
			stats += "\nThere's nothing here!";
		}

		return stats;
	}

	@Override
	public String[] getAliases() {
		return new String[] {"listemotestats", "list emote stats"};
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
