package tech.hadenw.discordbot.commands;

import java.awt.Color;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import tech.hadenw.discordbot.Errors;
import tech.hadenw.discordbot.Shmames;
import tech.hadenw.discordbot.storage.BotSettingName;
import tech.hadenw.discordbot.storage.Brain;

public class PinThat implements ICommand {
	@Override
	public String getDescription() {
		return "Sends a copy of the specified message over to the Pin Channel, if configured.";
	}
	
	@Override
	public String getUsage() {
		return "pinThat <^...>";
	}

	@Override
	public String run(String args, User author, Message message) {
		Matcher m = Pattern.compile("^([\\^]{1,15})$").matcher(args);
		
		if(m.find()) {
			Brain b = Shmames.getBrains().getBrain(message.getGuild().getId());
			
			try {
				int messages = args.length();
				
				List<Message> msgs = message.getChannel().getHistoryBefore(message, messages).complete().getRetrievedHistory();
				Message toPin = msgs.get(msgs.size()-1);
				
				boolean channelFound = false;
				for(TextChannel ch : message.getGuild().getTextChannels()) {
					if(ch.getId().equalsIgnoreCase(b.getSettingFor(BotSettingName.PIN_CHANNEL).getValue())) {
						channelFound = true;
						
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
				
				if(!channelFound)
					return Errors.CHANNEL_NOT_FOUND;
				
				return "";
			}catch(Exception ex) {
				ex.printStackTrace();
				return Errors.NO_PERMISSION_BOT;
			}
		}
		
		return Errors.formatUsage(Errors.INCOMPLETE, getUsage());
	}

	@Override
	public String[] getAliases() {
		return new String[] {"pinthat", "pin that"};
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
