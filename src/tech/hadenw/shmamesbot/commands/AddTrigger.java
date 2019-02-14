package tech.hadenw.shmamesbot.commands;

import java.util.regex.Pattern;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import tech.hadenw.shmamesbot.Shmames;
import tech.hadenw.shmamesbot.TriggerType;

public class AddTrigger implements ICommand {
	@Override
	public String getDescription() {
		return "Adds a message trigger that will send a random response. Usage: `addtrigger triggerType triggerWord`";
	}

	@Override
	public String run(String args, User author, Message message) {
		if(Pattern.compile("^[a-zA-Z]{3,} [a-zA-Z]{4,7}$").matcher(args).matches()) {
			String newtrigger = args.substring(args.indexOf(" ")).trim();
			String nttype = args.substring(0, args.indexOf(" ")).trim();
			
			if (!Shmames.getBrain().getAllTriggers().contains(newtrigger)) {
				if (TriggerType.byName(nttype) != null) {
					Shmames.getBrain().addTrigger(newtrigger, TriggerType.byName(nttype));
					Shmames.saveBrain();
					
					return "I will now send a `" + TriggerType.byName(nttype).toString()+ "` response when I hear `" + newtrigger + "`!";
				} else {
					String types = "";

					for (TriggerType t : TriggerType.values()) {
						if(types.length() > 0)
							types += ", ";
						
						types += "`" + t.name() + "`";
					}

					return ":scream: Invalid trigger type! Your options are: " + types;
				}
			} else {
				return "Good idea, but that trigger already exists :sob:";
			}
		} else {
			return ":fire: Wrong syntax! Try looking at the help menu.";
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"addtrigger", "add trigger"};
	}
	
	@Override
	public String sanitize(String i) {
		return i.toLowerCase();
	}
}
