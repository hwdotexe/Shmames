package tech.hadenw.shmamesbot.commands;

import java.awt.Color;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import tech.hadenw.shmamesbot.CommandHandler;
import tech.hadenw.shmamesbot.Errors;

public class Help implements ICommand {
	@Override
	public String getDescription() {
		return "Shows help & additional information";
	}
	
	@Override
	public String getUsage() {
		return "help [command]";
	}

	@Override
	public String run(String args, User author, Message message) {
		if(args.length() > 0) {
			// Wants help on specific command.
			
			for(ICommand c : CommandHandler.getLoadedCommands()) {
				for(String a : c.getAliases()) {
					if(a.equalsIgnoreCase(args)) {
						// Create list of aliases
						StringBuilder alsb = new StringBuilder();
						
						for(String al : c.getAliases()) {
							if(alsb.length() > 0) {
								alsb.append(", ");
							}
							
							alsb.append("`"+al+"`");
						}
						 
						EmbedBuilder eBuilder = new EmbedBuilder();
						
						eBuilder.setAuthor("== Command Help ==");
				        eBuilder.setColor(Color.MAGENTA);
				        eBuilder.setTitle(c.getAliases()[0]);
				        eBuilder.appendDescription("**Aliases:** "+alsb.toString()+"\n");
				        eBuilder.appendDescription("**Usage:** `"+c.getUsage()+"`\n");
				        eBuilder.appendDescription("**Description:** "+c.getDescription());
						
				        message.getChannel().sendMessage(eBuilder.build()).queue();
				        
						return "";
					}
				}
			}
		}else {
			// Wants a list of all commands and brief help.
			
			StringBuilder sb = new StringBuilder();
			
			sb.append("**How to use Shmames in 42 easy steps:**");
			
			for(ICommand c : CommandHandler.getLoadedCommands()) {
				if(c.getDescription().length() > 0) {
					String desc = c.getDescription().length() > 50 ? c.getDescription().substring(0, 50) + " [...]" : c.getDescription();
					sb.append("\n");
					sb.append("`"+c.getAliases()[0]+"` - "+desc);
				}
			}
			
			author.openPrivateChannel().queue((c) -> c.sendMessage(sb.toString()).queue());
		
			return "PM'd you the deets :punch:";
		}
		
		return Errors.COMMAND_NOT_FOUND;
	}

	@Override
	public String[] getAliases() {
		return new String[] {"help", "how do you use", "how do I use"};
	}
	
	@Override
	public String sanitize(String i) {
		return i.replaceAll("[\\W]", "").toLowerCase();
	}
	
	@Override
	public boolean requiresGuild() {
		return false;
	}
}
