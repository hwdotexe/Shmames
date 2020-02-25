package tech.hadenw.discordbot.commands;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import tech.hadenw.discordbot.Errors;
import tech.hadenw.discordbot.Shmames;
import tech.hadenw.discordbot.storage.BotSetting;
import tech.hadenw.discordbot.storage.BotSettingName;
import tech.hadenw.discordbot.storage.Brain;

public class Modify implements ICommand {
	@Override
	public String getDescription() {
		return "Change some of the bot's settings.";
	}
	
	@Override
	public String getUsage() {
		return "modify [<setting> <value>]";
	}
	
	@Override
	public String run(String args, User author, Message message) {
		Brain b = Shmames.getBrains().getBrain(message.getGuild().getId());
		String rs = b.getSettingFor(BotSettingName.ALLOW_MODIFY).getValue();
		
		Role r = !rs.equals("administrator") && !rs.equals("everyone") ? Shmames.getJDA().getGuildById(b.getGuildID()).getRolesByName(rs, true).get(0) : null;
		
		// Allow modification by administrators, users with roles, or if running in debug mode.
		if(message.getGuild().getMember(author).hasPermission(Permission.ADMINISTRATOR) || rs.equals("everyone") || message.getGuild().getMember(author).getRoles().contains(r) || Shmames.isDebug) {
			Matcher m = Pattern.compile("^([\\w]+) ([\\w\\-]+)$").matcher(args);
			
			if(m.find()) {
				if(BotSettingName.contains(m.group(1))) {
					BotSettingName setting = BotSettingName.valueOf(m.group(1).toUpperCase());
					String value = m.group(2);

					// Ensure that this setting is only changed by an Administrator.
					if(setting == BotSettingName.ALLOW_MODIFY) {
						if(!message.getGuild().getMember(author).hasPermission(Permission.ADMINISTRATOR) && !Shmames.isDebug) {
							return Errors.NO_PERMISSION_USER;
						}
					}
					
					BotSetting val = b.getSettingFor(setting);
					
					if(val.setValue(value, b)) {
						EmbedBuilder eBuilder = new EmbedBuilder();
						
				        eBuilder.setColor(Color.ORANGE);
						flexValueType(eBuilder, val, message.getGuild());
				        message.getChannel().sendMessage(eBuilder.build()).queue();
				        
				        // Save the new settings
				        Shmames.getBrains().saveBrain(b);
				        
						return "";
					}else {
						// Not successful
						return Errors.formatUsage(Errors.WRONG_USAGE, "`modify "+val.getName().toString()+" <"+val.getType().toString()+">`");
					}
				}else {
					return Errors.formatUsage(Errors.SETTING_NOT_FOUND, "`modify`");
				}
			} else {
				EmbedBuilder eBuilder = new EmbedBuilder();
				
		        eBuilder.setColor(Color.ORANGE);
		        eBuilder.setTitle("Available settings:");
		        eBuilder.setFooter("Do not include \"#\" or \":\" symbols.");
		        
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
