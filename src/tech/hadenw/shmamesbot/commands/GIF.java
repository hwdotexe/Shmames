package tech.hadenw.shmamesbot.commands;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import tech.hadenw.shmamesbot.Shmames;

public class GIF implements ICommand {
	@Override
	public String getDescription() {
		return "Send a super :sunglasses: GIF.";
	}

	@Override
	public String run(String args, User author, Guild server) {
		if(args.length() > 0)
			return Shmames.getGIF(args);
		else {
			return "You actually need to specify what you want me to search...";
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"gif", "what is a", "what are", "who is", "who are"};
	}
	
	@Override
	public String sanitize(String i) {
		return i.replaceAll("[\\W]", "").replaceAll(" ", "_").toLowerCase();
	}
}
