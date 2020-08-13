package com.hadenwatne.discordbot.commands;

import java.util.List;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.discordbot.Shmames;
import com.hadenwatne.discordbot.TriggerType;
import com.hadenwatne.discordbot.Utils;
import com.hadenwatne.discordbot.storage.Response;
import com.hadenwatne.discordbot.tasks.TimeoutTask;

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
		List<Response> r = Shmames.getBrains().getBrain(message.getGuild().getId()).getResponsesFor(TriggerType.HATE);
		String rFrom = r.get(Utils.getRandom(r.size())).getResponse().replaceAll("%NAME%", author.getName());
		String rTo = r.get(Utils.getRandom(r.size())).getResponse().replaceAll("%NAME%", author.getName());

		if (rFrom.startsWith("[gif]"))
			rFrom = Utils.getGIF(rFrom.split("\\[gif\\]",2)[1], message.getTextChannel().isNSFW()?"low":"high");
		
		if (rTo.startsWith("[gif]"))
			rTo = Utils.getGIF(rTo.split("\\[gif\\]",2)[1], message.getTextChannel().isNSFW()?"low":"high");
		
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
