package tech.hadenw.shmamesbot.commands;

import java.awt.Color;
import java.util.regex.Pattern;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import tech.hadenw.shmamesbot.Shmames;
import tech.hadenw.shmamesbot.brain.BotSettings;
import tech.hadenw.shmamesbot.brain.Brain;

public class Modify implements ICommand {
	@Override
	public String getDescription() {
		return "Change some of the bot's settings.";
	}
	
	// TODO check user's role for permissions, deny if they aren't allowed to change values.
	@Override
	public String run(String args, User author, Message message) {
		Brain b = Shmames.getBrains().getBrain(message.getGuild().getId());
		
		if(Pattern.compile("^[\\w]+ [\\w\\-]+$").matcher(args).matches()) {
			BotSettings setting = BotSettings.valueOf(args.substring(0, args.indexOf(" ")).toUpperCase());
			String value = args.substring(args.indexOf(" ")+1);
			
			if(setting != null) {
				b.getSettings().put(setting, value);
				
				EmbedBuilder eBuilder = new EmbedBuilder();
				
		        eBuilder.setColor(Color.ORANGE);
		        eBuilder.appendDescription("**"+setting.toString()+"**: "+b.getSettings().get(setting)+"\n");
		        
		        message.getChannel().sendMessage(eBuilder.build()).queue();
		        
		        // Save the new settings
		        Shmames.getBrains().saveBrain(b);
				
				return "";
			}else {
				return "I couldn't find that setting.";
			}
		} else {
			EmbedBuilder eBuilder = new EmbedBuilder();
			
	        eBuilder.setColor(Color.ORANGE);
	        eBuilder.setTitle("Available settings:");
	        
	        for(BotSettings s : BotSettings.values()) {
	        	eBuilder.appendDescription("**"+s.toString()+"**: "+b.getSettings().get(s)+"\n");
	        }
	        
	        message.getChannel().sendMessage(eBuilder.build()).queue();
	        
	        return "";
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