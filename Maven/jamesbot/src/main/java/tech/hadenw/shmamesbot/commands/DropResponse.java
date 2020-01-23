package tech.hadenw.shmamesbot.commands;

import java.util.List;
import java.util.regex.Pattern;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import tech.hadenw.shmamesbot.Errors;
import tech.hadenw.shmamesbot.Shmames;
import tech.hadenw.shmamesbot.TriggerType;
import tech.hadenw.shmamesbot.brain.Brain;
import tech.hadenw.shmamesbot.brain.Response;

public class DropResponse implements ICommand {
	@Override
	public String getDescription() {
		return "Removes a response from the pool.";
	}
	
	@Override
	public String getUsage() {
		return "dropresponse <responseType> <responseNumber>";
	}

	@Override
	public String run(String args, User author, Message message) {
		if(Pattern.compile("^[a-zA-Z]{4,7} \\d{1,3}$").matcher(args).matches()) {
			Brain b = Shmames.getBrains().getBrain(message.getGuild().getId());
			int rNum = Integer.parseInt(args.substring(args.indexOf(" ")).trim());
			String nrtype = args.substring(0, args.indexOf(" ")).trim();
	
			if (TriggerType.byName(nrtype) != null) {
				List<Response> responses = Shmames.getBrains().getBrain(message.getGuild().getId()).getResponsesFor(TriggerType.byName(nrtype));
				
				if(responses.size() >= rNum) {
					Response r = responses.get(rNum-1);
					b.removeTriggerResponse(r);
					Shmames.getBrains().saveBrain(b);
					
					return "Removed \""+r.getResponse()+"\"!";
				}else {
					return "I couldn't find that response .-.";
				}
			} else {
				String types = "";

				for (TriggerType t : TriggerType.values()) {
					types += " ";
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
		return new String[] {"dropresponse", "drop response", "removeresponse", "remove response"};
	}
	
	@Override
	public String sanitize(String i) {
		return i.replace("@", "");
	}
	
	@Override
	public boolean requiresGuild() {
		return true;
	}
}
