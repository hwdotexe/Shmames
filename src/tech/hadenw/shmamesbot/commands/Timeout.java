package tech.hadenw.shmamesbot.commands;

import java.util.List;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import tech.hadenw.shmamesbot.Shmames;
import tech.hadenw.shmamesbot.TimeoutTask;
import tech.hadenw.shmamesbot.TriggerType;
import tech.hadenw.shmamesbot.Utils;
import tech.hadenw.shmamesbot.brain.Response;

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
