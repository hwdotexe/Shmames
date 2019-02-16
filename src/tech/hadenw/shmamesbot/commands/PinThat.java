package tech.hadenw.shmamesbot.commands;

import java.util.List;
import java.util.regex.Pattern;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class PinThat implements ICommand {
	@Override
	public String getDescription() {
		return "Pins a message to #the-wall, if the channel exists. `Usage: pinthat ^`\n**TIP: Try `pinthat ^^^`!**";
	}

	@Override
	public String run(String args, User author, Message message) {
		if(Pattern.compile("^[\\^]{1,10}$").matcher(args).matches()) {
			try {
				int messages = args.length();
				
				List<Message> msgs = message.getChannel().getHistoryBefore(message, messages).complete().getRetrievedHistory();
				Message toPin = msgs.get(msgs.size()-1);
				
				for(TextChannel ch : message.getGuild().getTextChannels()) {
					if(ch.getName().equalsIgnoreCase("the-wall")) {
						ch.sendMessage(toPin.getAuthor().getAsMention()+" (#"+toPin.getChannel().getName()+"): "+toPin.getContentDisplay()).queue();
						
						break;
					}
				}
				
				return "";
			}catch(Exception ex) {
				ex.printStackTrace();
				return "I wasn't able to pin that. Sorry!";
			}
		}
		
		return "What am I supposed to pin, exactly?";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"pinthat"};
	}
	
	@Override
	public String sanitize(String i) {
		return i;
	}
}
