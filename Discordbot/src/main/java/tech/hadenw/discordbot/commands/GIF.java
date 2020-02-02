package tech.hadenw.discordbot.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import tech.hadenw.discordbot.Errors;
import tech.hadenw.discordbot.Utils;

public class GIF implements ICommand {
	@Override
	public String getDescription() {
		return "Send a super :sunglasses: GIF.";
	}
	
	@Override
	public String getUsage() {
		return "gif <search>";
	}

	@Override
	public String run(String args, User author, Message message) {
		if(args.length() > 0)
			return Utils.getGIF(args);
		else {
			return Errors.formatUsage(Errors.INCOMPLETE, getUsage());
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"gif", "who is", "what is", "what are"};
	}
	
	@Override
	public String sanitize(String i) {
		return i.replaceAll("[\\W]", "").replaceAll(" ", "%20").toLowerCase();
	}
	
	@Override
	public boolean requiresGuild() {
		return false;
	}
}
