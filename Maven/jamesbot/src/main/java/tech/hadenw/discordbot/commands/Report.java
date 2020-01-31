package tech.hadenw.discordbot.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import tech.hadenw.discordbot.Errors;
import tech.hadenw.discordbot.Shmames;
import tech.hadenw.discordbot.storage.Brain;
import tech.hadenw.discordbot.tasks.ReportCooldownTask;

public class Report implements ICommand {
	@Override
	public String getDescription() {
		return "File a Secret Police report";
	}
	
	@Override
	public String getUsage() {
		return "report [bug|feature] <your message>";
	}

	@Override
	public String run(String args, User author, Message message) {
		Matcher m = Pattern.compile("^((bug)|(feature) )?(.{5,})$", Pattern.CASE_INSENSITIVE).matcher(args);
		
		if(m.find()) {
			Brain b = Shmames.getBrains().getBrain(message.getGuild().getId());
			
			if(!b.getReportCooldown()) {
				String type = m.group(1); // Could be empty!
				String msg = m.group(4);
				
				if(type == null)
					type = "UNSPECIFIED";
				
				b.getFeedback().add(author.getName()+" ("+message.getGuild().getName()+"): ["+type.toUpperCase()+"] "+msg);
				Shmames.getBrains().saveBrain(b);
				
				// Start a cooldown
				new ReportCooldownTask(b);
				
				return ":notepad_spiral: Your feedback has been noted. Thanks!\nYou can report again in **2 minutes**.";
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
		return new String[] {"report", "feedback"};
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
