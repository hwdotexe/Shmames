package tech.hadenw.shmamesbot.commands;

import java.awt.Color;
import java.util.List;
import java.util.regex.Pattern;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Message.Attachment;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import tech.hadenw.shmamesbot.Errors;
import tech.hadenw.shmamesbot.Shmames;
import tech.hadenw.shmamesbot.brain.BotSettings;
import tech.hadenw.shmamesbot.brain.Brain;

public class PinThat implements ICommand {
	@Override
	public String getDescription() {
		return "Echoes a message to another channel.";
	}
	
	@Override
	public String getUsage() {
		return "pinThat <^...>";
	}

	@Override
	public String run(String args, User author, Message message) {
		if(Pattern.compile("^[\\^]{1,10}$").matcher(args).matches()) {
			Brain b = Shmames.getBrains().getBrain(message.getGuild().getId());
			
			try {
				int messages = args.length();
				
				List<Message> msgs = message.getChannel().getHistoryBefore(message, messages).complete().getRetrievedHistory();
				Message toPin = msgs.get(msgs.size()-1);
				
				for(TextChannel ch : message.getGuild().getTextChannels()) {
					if(ch.getName().equalsIgnoreCase(b.getSettings().get(BotSettings.PIN_CHANNEL))) {
						EmbedBuilder eBuilder = new EmbedBuilder();
						
						eBuilder.setAuthor(toPin.getAuthor().getName(), null, toPin.getAuthor().getEffectiveAvatarUrl());
				        eBuilder.setColor(Color.CYAN);
				        
				        String msg = toPin.getContentRaw();
				        for(Attachment a : toPin.getAttachments()) {
				        	msg += "\n";
				        	msg += a.getUrl();
				        }
				        eBuilder.appendDescription(msg);
				        eBuilder.setFooter("#" + toPin.getChannel().getName() + " - Pinned by @"+message.getAuthor().getName(), null);				        

				        MessageEmbed embed = eBuilder.build();
				        ch.sendMessage(embed).queue();
						
						break;
					}
				}
				
				return "";
			}catch(Exception ex) {
				ex.printStackTrace();
				return Errors.NO_PERMISSION_BOT;
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
	
	@Override
	public boolean requiresGuild() {
		return true;
	}
}
