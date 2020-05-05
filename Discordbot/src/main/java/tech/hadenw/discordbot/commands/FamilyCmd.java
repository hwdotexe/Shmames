package tech.hadenw.discordbot.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import tech.hadenw.discordbot.Errors;
import tech.hadenw.discordbot.Shmames;
import tech.hadenw.discordbot.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FamilyCmd implements ICommand {
	@Override
	public String getDescription() {
		return "Manage your "+ Shmames.getBotName() + " Family";
	}
	
	@Override
	public String getUsage() {
		return "family <create|add|view|remove> [name|code]";
	}

	@Override
	public String run(String args, User author, Message message) {
		Matcher m = Pattern.compile("^((create)|(add)|(view)|(remove))( [a-z0-9\\-]+)?$", Pattern.CASE_INSENSITIVE).matcher(args);

		//g1 - command
		//g6 - args

		// The server will record its own families, the same object stored in Motherbrain.
		// This should speed up searching for cross-family items.
		// TODO ensure that we update the object reference so that both stay in sync; remove from both lists, etc.
		// TODO problem: GSON is likely to create 2 objects at reload.
		// How about the Family object has IDs, and the Brain has a list of Family IDs it belongs to. That'll teach 'em.

		if(m.find()){
			switch(m.group(1).toLowerCase()){
				case "create":
					// Allow if this person doesn't already have a family with that name.
					// Can create multiple families with the same server.
					// Add this server to the family if created.
					break;
				case "add":
					// If no args, we want to create a new code (if possible) for other servers.
					// If args, try it as a code. Invalidate even if user has no perm to add the server.
					break;
				case "view":
					// View info on the family (Administrator)
					break;
				case "remove":
					// If no args, assume this server. User must be an Admin.
					// If an arg, assume another server. User must be the family owner.
					break;
				default:
					return Errors.formatUsage(Errors.WRONG_USAGE, getUsage());
			}
		}

		return Errors.formatUsage(Errors.WRONG_USAGE, getUsage());
	}

	@Override
	public String[] getAliases() {
		return new String[] {"family"};
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
