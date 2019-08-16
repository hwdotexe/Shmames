package tech.hadenw.shmamesbot.commands;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import tech.hadenw.shmamesbot.Errors;

public class IdiotThat implements ICommand {
	@Override
	public String getDescription() {
		return "Make them sound like an idiot.";
	}
	
	@Override
	public String getUsage() {
		return "idiotthat <^...>";
	}

	@Override
	public String run(String args, User author, Message message) {
		Matcher m = Pattern.compile("^([\\^]{1,15})?$").matcher(args);
		
		if(m.find()) {
			int messages = m.group(1).length();
			List<Message> msgs = message.getChannel().getHistoryBefore(message, messages).complete().getRetrievedHistory();
			Message toPin = msgs.get(msgs.size()-1);
			String idiotOrig = toPin.getContentDisplay();
			String idiot = "";
			
			// PascalCase
			for(String w : idiotOrig.split(" ")) {
				idiot += w.substring(0,1).toUpperCase()+w.substring(1).toLowerCase();
				
				if(w.length()>4 && idiot.endsWith("s")) {
					idiot = idiot.substring(0, idiot.length()-1);
					idiot += "'s";
				}
				
				idiot += " ";
			}
			
			// Exclamation Points
			idiot = idiot.replaceAll("!", "!!1");
			
			// Horrible Emojis
			idiotOrig = idiot;
			idiot = "";
			for(String w : idiotOrig.split(" ")) {
				idiot += w;
				
				if(w.equalsIgnoreCase("okay") || w.equalsIgnoreCase("ok"))
					idiot += " :ok_hand:";
				
				if(w.equalsIgnoreCase("love"))
					idiot += " :heart:";
				
				if(w.equalsIgnoreCase("lol") || w.equalsIgnoreCase("haha"))
					idiot += " :joy:";
				
				if(w.equalsIgnoreCase("wow"))
					idiot += " :open_mouth:";
				
				idiot += " ";
			}
			
			return idiot;
		}else {
			return Errors.formatUsage(Errors.INCOMPLETE, getUsage());
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"idiotthat"};
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
