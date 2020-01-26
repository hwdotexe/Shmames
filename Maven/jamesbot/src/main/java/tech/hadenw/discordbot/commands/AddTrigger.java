package tech.hadenw.discordbot.commands;

import java.util.regex.Pattern;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import tech.hadenw.discordbot.Errors;
import tech.hadenw.discordbot.Shmames;
import tech.hadenw.discordbot.TriggerType;
import tech.hadenw.discordbot.brain.Brain;

public class AddTrigger implements ICommand {
	@Override
	public String getDescription() {
		return "Adds a message trigger to send a random response.";
	}
	
	@Override
	public String getUsage() {
		return "addtrigger <triggerType> <triggerWord>";
	}

	@Override
	public String run(String args, User author, Message message) {
		if(Pattern.compile("^[a-zA-Z]{3,7} [\\w \\-]{3,}$").matcher(args).matches()) {
			Brain b = Shmames.getBrains().getBrain(message.getGuild().getId());
			String newtrigger = args.substring(args.indexOf(" ")).trim();
			String nttype = args.substring(0, args.indexOf(" ")).trim();
			
			if (!b.getTriggers().keySet().contains(newtrigger)) {
				if (TriggerType.byName(nttype) != null) {
					b.getTriggers().put(newtrigger, TriggerType.byName(nttype));
					Shmames.getBrains().saveBrain(b);
					
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
			return Errors.formatUsage(Errors.WRONG_USAGE, getUsage());
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
	
	@Override
	public boolean requiresGuild() {
		return true;
	}
}
