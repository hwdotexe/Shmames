package tech.hadenw.discordbot.commands;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import tech.hadenw.discordbot.Errors;
import tech.hadenw.discordbot.Shmames;
import tech.hadenw.discordbot.TriggerType;
import tech.hadenw.discordbot.storage.Response;

public class ListResponses implements ICommand {
	@Override
	public String getDescription() {
		return "Lists the random responses and their types.";
	}
	
	@Override
	public String getUsage() {
		return "listResponses <responseType>";
	}

	@Override
	public String run(String args, User author, Message message) {
		Matcher m = Pattern.compile("^([a-zA-Z]{4,7})$").matcher(args);
		
		if(m.find()) {
			if(TriggerType.byName(m.group(1)) != null) {
				String msg = "**"+args.toUpperCase()+" Responses:**";
		
				List<Response> rs = Shmames.getBrains().getBrain(message.getGuild().getId()).getResponsesFor(TriggerType.byName(args));
				for (Response r : rs) {
					if(msg.length() > 0)
						msg += "\n";
					
					msg += (rs.indexOf(r)+1) + ": ";
					msg += r.getResponse();
				}
				
				return msg;
			} else {
				String types = "";

				for (TriggerType t : TriggerType.values()) {
					if(types.length() > 0)
						types += ", ";
					
					types += "`" + t.name() + "`";
				}

				return ":scream: Invalid trigger type! Your options are:" + types;
			}
		} else {
			return Errors.formatUsage(Errors.WRONG_USAGE, getUsage());
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
