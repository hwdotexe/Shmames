package tech.hadenw.shmamesbot.commands;

import java.util.regex.Pattern;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import tech.hadenw.shmamesbot.Shmames;
import tech.hadenw.shmamesbot.TriggerType;

public class AddResponse implements ICommand {
	@Override
	public String getDescription() {
		return "Adds a new response to the random pool. Usage: `addresponse responseType responseText`";
	}

	@Override
	public String run(String args, User author, Guild server) {
		if(Pattern.compile("^[a-zA-Z]{4,7} [\\w\\W]{3,}$").matcher(args).matches()) {
			String newresp = args.substring(args.indexOf(" ")).trim();
			String nrtype = args.substring(0, args.indexOf(" ")).trim();
	
			if (!Shmames.getBrain().getResponses().containsKey(newresp)) {
				if (TriggerType.byName(nrtype) != null) {
					Shmames.getBrain().addResponse(newresp, TriggerType.byName(nrtype));
					Shmames.saveBrain();
	
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
			return ":fire: Wrong syntax! Try looking at the help menu.";
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"addresponse", "add response"};
	}
}
