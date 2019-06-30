package tech.hadenw.shmamesbot.commands;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import tech.hadenw.shmamesbot.Errors;
import tech.hadenw.shmamesbot.Shmames;
import tech.hadenw.shmamesbot.brain.BotSettingName;
import tech.hadenw.shmamesbot.brain.BotSetting;
import tech.hadenw.shmamesbot.brain.Brain;

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
		if(message.getGuild().getMember(author).hasPermission(Permission.ADMINISTRATOR) || Shmames.isDebug) {
			Brain b = Shmames.getBrains().getBrain(message.getGuild().getId());
			Matcher m = Pattern.compile("^([\\w]+) ([\\w\\-]+)$").matcher(args);
			
			if(m.find()) {
				BotSettingName setting = BotSettingName.valueOf(m.group(1).toUpperCase());
				String value = m.group(2);
				
				if(setting != null) {
					//b.getSettings().put(setting, value.toLowerCase());
					BotSetting val = b.getSettingFor(setting);
					
					if(val.setValue(value, b)) {
						EmbedBuilder eBuilder = new EmbedBuilder();
						
				        eBuilder.setColor(Color.ORANGE);
				        eBuilder.appendDescription("**"+val.getName()+"** = "+val.getValue()+"\n");
				        
				        message.getChannel().sendMessage(eBuilder.build()).queue();
				        
				        // Save the new settings
				        Shmames.getBrains().saveBrain(b);
						return "";
					}else {
						// Not successful
						return Errors.WRONG_USAGE;
					}
				}else {
					return "I couldn't find that setting.";
				}
			} else {
				EmbedBuilder eBuilder = new EmbedBuilder();
				
		        eBuilder.setColor(Color.ORANGE);
		        eBuilder.setTitle("Available settings:");
		        
		        for(BotSetting v : b.getSettings()) {
		        	eBuilder.appendDescription("**"+v.getName().toString()+"**:"+v.getType().toString()+" = "+v.getValue()+"\n");
		        }
		        
		        message.getChannel().sendMessage(eBuilder.build()).queue();
		        
		        return "";
			}
		}else {
			return Errors.NO_PERMISSION_USER;
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
