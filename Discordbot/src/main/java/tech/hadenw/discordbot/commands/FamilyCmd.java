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

		if(m.find()){
			switch(m.group(1).toLowerCase()){
				case "create":
					break;
				case "add":
					break;
				case "view":
					break;
				case "remove":
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
