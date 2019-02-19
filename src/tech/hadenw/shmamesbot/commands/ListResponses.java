package tech.hadenw.shmamesbot.commands;

import java.util.regex.Pattern;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import tech.hadenw.shmamesbot.Errors;
import tech.hadenw.shmamesbot.Shmames;
import tech.hadenw.shmamesbot.TriggerType;

public class ListResponses implements ICommand {
	@Override
	public String getDescription() {
		return "Lists the random responses and their types.";
	}
	
	@Override
	public String getUsage() {
		return "listResponses responseType";
	}

	@Override
	public String run(String args, User author, Message message) {
		if(Pattern.compile("^[a-zA-Z]{4,7}$").matcher(args).matches()) {
			if(TriggerType.byName(args) != null) {
				String msg = "**"+args.toUpperCase()+" Responses:**";
		
				for (String s : Shmames.getBrains().getBrain(message.getGuild().getId()).getResponsesFor(TriggerType.byName(args))) {
					if(msg.length() > 0)
						msg += "\n";
					
					msg += s;
				}
				
				return msg;
			} else {
				return ":thinking: I didn't recognize that trigger type.";
			}
		} else {
			return Errors.WRONG_USAGE;
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"listresponses", "list responses"};
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
