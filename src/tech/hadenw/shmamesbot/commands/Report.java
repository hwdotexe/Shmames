package tech.hadenw.shmamesbot.commands;

import java.util.regex.Pattern;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import tech.hadenw.shmamesbot.Errors;
import tech.hadenw.shmamesbot.Shmames;
import tech.hadenw.shmamesbot.brain.Brain;

public class Report implements ICommand {
	@Override
	public String getDescription() {
		return "File a Secret Police report";
	}
	
	@Override
	public String getUsage() {
		return "report <bug|feature> <your message>";
	}

	@Override
	public String run(String args, User author, Message message) {
		if(Pattern.compile("^((bug)|(feature)) .{5,}$").matcher(args.toLowerCase()).matches()) {
			Brain b = Shmames.getBrains().getBrain(message.getGuild().getId());
			String type = args.substring(0, args.indexOf(" ")+1).trim();
			String msg = args.substring(args.indexOf(" ")+1).trim();
			
			b.getFeedback().add(author.getName()+" ("+message.getGuild().getName()+"): ["+type.toUpperCase()+"] "+msg);
			Shmames.getBrains().saveBrain(b);
			
			return ":notepad_spiral: Your feedback has been noted. Thanks!";
		}else {
			return Errors.WRONG_USAGE;
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"report"};
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
