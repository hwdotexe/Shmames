package tech.hadenw.discordbot.commands;

import java.util.regex.Pattern;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import tech.hadenw.discordbot.Errors;
import tech.hadenw.discordbot.Shmames;
import tech.hadenw.discordbot.TriggerType;
import tech.hadenw.discordbot.brain.Brain;
import tech.hadenw.discordbot.brain.Response;

public class AddResponse implements ICommand {
	@Override
	public String getDescription() {
		return "Adds a new response to the random pool.";
	}
	
	@Override
	public String getUsage() {
		return "addresponse <responseType> <responseText>";
	}

	@Override
	public String run(String args, User author, Message message) {
		if(Pattern.compile("^[a-zA-Z]{4,7} [\\w\\W]{3,}$").matcher(args).matches()) {
			Brain b = Shmames.getBrains().getBrain(message.getGuild().getId());
			String newresp = args.substring(args.indexOf(" ")).trim();
			String nrtype = args.substring(0, args.indexOf(" ")).trim();
	
			if (TriggerType.byName(nrtype) != null) {
				b.getTriggerResponses().add(new Response(TriggerType.byName(nrtype), newresp));
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
			return Errors.formatUsage(Errors.WRONG_USAGE, getUsage());
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
