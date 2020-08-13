package com.hadenwatne.discordbot.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.discordbot.Errors;
import com.hadenwatne.discordbot.Shmames;
import com.hadenwatne.discordbot.storage.Brain;
import com.hadenwatne.discordbot.tasks.ReportCooldownTask;

public class Report implements ICommand {
	@Override
	public String getDescription() {
		return "Send feedback about "+Shmames.getBotName()+" to the developer. Your username, server's name, and message will be recorded.";
	}
	
	@Override
	public String getUsage() {
		return "report [bug|feature] <your message>";
	}

	@Override
	public String run(String args, User author, Message message) {
		Matcher m = Pattern.compile("^((bug)|(feature))?\\s?(.{10,})$", Pattern.CASE_INSENSITIVE).matcher(args);
		
		if(m.find()) {
			Brain b = Shmames.getBrains().getBrain(message.getGuild().getId());
			
			if(!b.getReportCooldown()) {
				String type = m.group(1); // Could be empty!
				String msg = m.group(4);
				
				if(type == null)
					type = "GENERIC";
				
				b.getFeedback().add(author.getName()+" ("+message.getGuild().getName()+"): ["+type.toUpperCase()+"] "+msg);
				
				// Start a cooldown
				new ReportCooldownTask(b);
				
				return ":notepad_spiral: Your feedback has been noted. Thanks!\nYou can report again in **5 minutes**.";
			}else {
				// On cooldown
				return "Please wait a bit before submitting more feedback.";
			}
		}else {
			return Errors.formatUsage(Errors.WRONG_USAGE, getUsage());
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"report", "feedback", "suggestion"};
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
