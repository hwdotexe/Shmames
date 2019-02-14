package tech.hadenw.shmamesbot.commands;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import tech.hadenw.shmamesbot.Poll;
import tech.hadenw.shmamesbot.Shmames;

public class Startpoll implements ICommand {
	@Override
	public String getDescription() {
		return "Starts a new poll. Usage: `startpoll question? optionA, optionB...`";
	}

	@Override
	public String run(String args, User author, Message message) {
		try {
			String q = args.substring(0, args.indexOf("?")+1);
			String o = args.substring(args.indexOf("?")+1).trim();
			
			List<String> options = new ArrayList<String>();
			
			for(String s : o.split(", ")) {
				options.add(s);
			}
			
			if(options.size() > 1 && options.size() <= 9) {
				message.getChannel().deleteMessageById(message.getIdLong()).queue();
				Shmames.getPolls().add(new Poll(message.getChannel(), q, options));
			}
			else
				return "You must provide at least 2 different options, and fewer than 9!";
		}catch(Exception e) {
			e.printStackTrace();
			return ":thinking: I don't think that's how you do it.";
		}
		
		return "";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"startpoll"};
	}
	
	@Override
	public String sanitize(String i) {
		return i;
	}
}
