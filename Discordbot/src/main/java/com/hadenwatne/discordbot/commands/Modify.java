package com.hadenwatne.discordbot.commands;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import com.hadenwatne.discordbot.Errors;
import com.hadenwatne.discordbot.Shmames;
import com.hadenwatne.discordbot.Utils;
import com.hadenwatne.discordbot.storage.BotSetting;
import com.hadenwatne.discordbot.storage.BotSettingName;
import com.hadenwatne.discordbot.storage.Brain;

public class Modify implements ICommand {
	@Override
	public String getDescription() {
		return "The Administrator's command to customize bot settings and behavior.";
	}
	
	@Override
	public String getUsage() {
		return "modify [<setting> [new value]]";
	}
	
	@Override
	public String run(String args, User author, Message message) {
		Brain b = Shmames.getBrains().getBrain(message.getGuild().getId());
		BotSetting canModify = b.getSettingFor(BotSettingName.ALLOW_MODIFY);

		if(Utils.CheckUserPermission(canModify, message.getGuild().getMember(author))) {
			Matcher m = Pattern.compile("^([\\w]+)( [\\w\\-]+)?$").matcher(args);
			
			if(m.find()) {
				if(BotSettingName.contains(m.group(1))) {
					BotSettingName settingName = BotSettingName.valueOf(m.group(1).toUpperCase());
					BotSetting setting = b.getSettingFor(settingName);

					if(m.group(2) != null) {
						// Ensure that this setting is only changed by an Administrator.
						if (settingName == BotSettingName.ALLOW_MODIFY) {
							if (!message.getGuild().getMember(author).hasPermission(Permission.ADMINISTRATOR) && !Shmames.isDebug) {
								return Errors.NO_PERMISSION_USER;
							}
						}

						String value = m.group(2).trim();

						if (setting.setValue(value, b)) {
							EmbedBuilder eBuilder = new EmbedBuilder();

							eBuilder.setColor(Color.ORANGE);
							flexValueType(eBuilder, setting, message.getGuild());
							eBuilder.addField("Status", "Setting updated successfully!", false);
							message.getChannel().sendMessage(eBuilder.build()).queue();

							return "";
						} else {
							// Not successful
							return Errors.formatUsage(Errors.WRONG_USAGE, "`modify " + setting.getName().toString() + " <" + setting.getType().toString() + ">`");
						}
					}else{
						//TODO
						EmbedBuilder eBuilder = new EmbedBuilder();

						eBuilder.setColor(Color.ORANGE);
						flexValueType(eBuilder, setting, message.getGuild());
						eBuilder.addField("Description", settingName.getDescription(), false);
						eBuilder.addField("Type", setting.getType().name(), false);
						message.getChannel().sendMessage(eBuilder.build()).queue();

						return "";
					}
				}else {
					return Errors.formatUsage(Errors.SETTING_NOT_FOUND, "`modify`");
				}
			} else {
				EmbedBuilder eBuilder = new EmbedBuilder();
				
		        eBuilder.setColor(Color.ORANGE);
		        eBuilder.setTitle("Available settings:");
		        eBuilder.setFooter("Do not include \"#\" or \":\" symbols. // Use \""+Shmames.getBotName()+" modify <setting>\" for info.");
		        
		        for(BotSetting v : b.getSettings()) {
		        	flexValueType(eBuilder, v, message.getGuild());
		        }
		        
		        message.getChannel().sendMessage(eBuilder.build()).queue();
		        
		        return "";
			}
		}else {
			return Errors.NO_PERMISSION_USER;
		}
	}

	private void flexValueType(EmbedBuilder eBuilder, BotSetting v, Guild g){
		switch(v.getType()){
			case CHANNEL:
				try {
					TextChannel mc = g.getTextChannelById(v.getValue());
					eBuilder.addField("**"+v.getName().toString()+"**" + "» :tv:", mc.getAsMention(), true);
				}catch (Exception e){
					eBuilder.addField("**"+v.getName().toString()+"**" + "» :tv:", ":warning: INVALID", true);
				}

				break;
			case EMOTE:
				try {
					Emote em = g.getEmoteById(v.getValue());
					eBuilder.addField("**"+v.getName().toString()+"**" + "» :muscle:", em.getAsMention(), true);
				}catch (Exception e){
					eBuilder.addField("**"+v.getName().toString()+"**" + "» :muscle:", ":warning: INVALID", true);
				}

				break;
			case NUMBER:
				eBuilder.addField("**"+v.getName().toString()+"**" + "» :hash:", v.getValue(), true);

				break;
			case BOOLEAN:
				eBuilder.addField("**"+v.getName().toString()+"**" + "» :level_slider:", v.getValue(), true);

				break;
			case ROLE:
				eBuilder.addField("**"+v.getName().toString()+"**" + "» :tools:", v.getValue(), true);

				break;
			default:
				eBuilder.addField("**"+v.getName().toString()+"**" + "» :gear:", v.getValue(), true);
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"modify"};
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
