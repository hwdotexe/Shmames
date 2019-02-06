package tech.hadenw.shmamesbot.commands;

import java.util.regex.Pattern;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import tech.hadenw.shmamesbot.Shmames;
import tech.hadenw.shmamesbot.TriggerType;

public class ListResponses implements ICommand {
	@Override
	public String getDescription() {
		return "Lists the random responses and their types. Usage: `listresponses responseType`";
	}

	@Override
	public String run(String args, User author, Guild server) {
		if(Pattern.compile("^[a-zA-Z]{4,7}$").matcher(args).matches()) {
			String msg = "";
	
			for (String s : Shmames.getBrain().getAllResponsesFor(TriggerType.byName(args))) {
				if(msg.length() > 0)
					msg += "\n";
				
				msg += s;
			}
	
			return msg;
		} else {
			return ":fire: Wrong syntax! Try looking at the help menu.";
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"listresponses", "list responses"};
	}
}
