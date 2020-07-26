package tech.hadenw.discordbot.commands;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import tech.hadenw.discordbot.Errors;
import tech.hadenw.discordbot.Shmames;
import tech.hadenw.discordbot.Utils;
import tech.hadenw.discordbot.storage.Brain;
import tech.hadenw.discordbot.storage.Family;
import tech.hadenw.discordbot.storage.ForumWeaponObj;

public class ForumWeaponList implements ICommand {
	@Override
	public String getDescription() {
		return "List all the Forum Weapons available to this server.";
	}
	
	@Override
	public String getUsage() {
		return "fwlist [all|search <query>]";
	}

	@Override
	public String run(String args, User author, Message message) {
		return "This command has moved! \nTry: `fw list [all]` or `fw search <query>`!";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"fwlist", "fwarsenal"};
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
