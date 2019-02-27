package tech.hadenw.shmamesbot.commands;

import java.util.regex.Pattern;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import tech.hadenw.shmamesbot.Errors;
import tech.hadenw.shmamesbot.Shmames;
import tech.hadenw.shmamesbot.TriggerType;
import tech.hadenw.shmamesbot.brain.Brain;

public class AddResponse implements ICommand {
	@Override
	public String getDescription() {
		return "Adds a new response to the random pool.";
	}
	
	@Override
	public String getUsage() {
		return "addresponse responseType responseText";
	}

	@Override
	public String run(String args, User author, Message message) {
		if(Pattern.compile("^[a-zA-Z]{4,7} [\\w\\W]{3,}$").matcher(args).matches()) {
			Brain b = Shmames.getBrains().getBrain(message.getGuild().getId());
			String newresp = args.substring(args.indexOf(" ")).trim();
			String nrtype = args.substring(0, args.indexOf(" ")).trim();
	
			if (!b.getResponses().containsKey(newresp)) {
				if (TriggerType.byName(nrtype) != null) {
					b.getResponses().put(newresp, TriggerType.byName(nrtype));
					Shmames.getBrains().saveBrain(b);
	
					return "Added :+1:";
				} else {
					String types = "";
	
					for (TriggerType t : TriggerType.values()) {
						types += " ";
						types += "`" + t.name() + "`";
					}
	
					return ":scream: Invalid trigger type! Your options are:" + types;
				}
			} else {
				return "Good idea, but that response already exists :sob:";
			}
		} else {
			return Errors.WRONG_USAGE;
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"addresponse", "add response"};
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
