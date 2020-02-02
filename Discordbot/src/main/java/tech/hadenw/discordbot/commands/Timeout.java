package tech.hadenw.discordbot.commands;

import java.util.List;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import tech.hadenw.discordbot.Shmames;
import tech.hadenw.discordbot.TriggerType;
import tech.hadenw.discordbot.Utils;
import tech.hadenw.discordbot.storage.Response;
import tech.hadenw.discordbot.tasks.TimeoutTask;

public class Timeout implements ICommand {
	@Override
	public String getDescription() {
		return "Put the bot on time-out.";
	}
	
	@Override
	public String getUsage() {
		return "timeout";
	}

	@Override
	public String run(String args, User author, Message message) {
		List<Response> r = Shmames.getBrains().getBrain(message.getGuild().getId()).getResponsesFor(TriggerType.RONALD); 
		String rFrom = r.get(Utils.getRandom(r.size())).getResponse().replaceAll("%NAME%", author.getName());
		String rTo = r.get(Utils.getRandom(r.size())).getResponse().replaceAll("%NAME%", author.getName());

		if (rFrom.startsWith("[gif]"))
			rFrom = Utils.getGIF(rFrom.split("\\[gif\\]",2)[1]);
		
		if (rTo.startsWith("[gif]"))
			rTo = Utils.getGIF(rTo.split("\\[gif\\]",2)[1]);
		
		new TimeoutTask(rTo, message.getChannel(), Shmames.getBrains().getBrain(message.getGuild().getId()));
		
		return rFrom;
	}

	@Override
	public String[] getAliases() {
		return new String[] {"timeout"};
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
