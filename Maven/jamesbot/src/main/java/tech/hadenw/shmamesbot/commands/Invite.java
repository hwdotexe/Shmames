package tech.hadenw.shmamesbot.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

public class Invite implements ICommand {
	@Override
	public String getDescription() {
		return "Share this bot with others!";
	}
	
	@Override
	public String getUsage() {
		return "invite";
	}

	@Override
	public String run(String args, User author, Message message) {
		author.openPrivateChannel().queue((c) -> c.sendMessage("Use this link to invite me to another server! \nhttps://discordapp.com/api/oauth2/authorize?client_id=377639048573091860&permissions=335637584&redirect_uri=https%3A%2F%2Fdiscordapp.com%2Fapi%2Foauth2%2Fauthorize&scope=bot").queue());
		
		return "Sent it over in a PM!";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"invite"};
	}
	
	@Override
	public String sanitize(String i) {
		return i;
	}
	
	@Override
	public boolean requiresGuild() {
		return false;
	}
}
