package com.hadenwatne.discordbot.commands;

import java.awt.Color;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hadenwatne.discordbot.storage.Lang;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.discordbot.Errors;
import com.hadenwatne.discordbot.storage.BotSettingName;
import com.hadenwatne.discordbot.storage.Brain;

import javax.annotation.Nullable;

public class PinThat implements ICommand {
	private Brain brain;

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
			try {
				int messages = args.length();
				
				List<Message> msgs = message.getChannel().getHistoryBefore(message, messages).complete().getRetrievedHistory();
				Message toPin = msgs.get(msgs.size()-1);
				
				boolean channelFound = false;
				for(TextChannel ch : message.getGuild().getTextChannels()) {
					if(ch.getId().equalsIgnoreCase(brain.getSettingFor(BotSettingName.PIN_CHANNEL).getValue())) {
						channelFound = true;
						
						EmbedBuilder eBuilder = new EmbedBuilder();
						
						eBuilder.setAuthor(toPin.getAuthor().getName(), null, toPin.getAuthor().getEffectiveAvatarUrl());
				        eBuilder.setColor(Color.CYAN);
				        
				        StringBuilder msg = new StringBuilder(toPin.getContentRaw());
				        for(Attachment a : toPin.getAttachments()) {
				        	msg.append("\n");
				        	msg.append(a.getUrl());
				        }
				        eBuilder.appendDescription(msg.toString());
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
	public void setRunContext(Lang lang, @Nullable Brain brain) {
		this.brain = brain;
	}
	
	@Override
	public boolean requiresGuild() {
		return true;
	}
}
