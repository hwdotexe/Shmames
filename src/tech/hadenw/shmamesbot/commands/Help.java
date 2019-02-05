package tech.hadenw.shmamesbot.commands;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;

public class Help implements ICommand {
	@Override
	public String getDescription() {
		return "Shows the help menu or additional information on a specific command.";
	}

	@Override
	public String run(String args, User author, Guild server) {
		if(args.length() > 0) {
			// Wants help on specific command
			for(ICommand c : CommandHandler.getLoadedCommands()) {
				for(String a : c.getAliases()) {
					if(a.equalsIgnoreCase(args)) {
						StringBuilder sb = new StringBuilder();
						
						sb.append("**You want _MORE_ information?**");
						sb.append("\nCommand: `"+c.getAliases()[0]+"`");
						
						// Create list of aliases
						StringBuilder alsb = new StringBuilder();
						
						for(String al : c.getAliases()) {
							if(alsb.length() > 0) {
								alsb.append(", ");
							}
							
							alsb.append("`"+al+"`");
						}
						
						sb.append("\nAliases: "+alsb.toString());
						sb.append("\nDescription: "+c.getDescription());
						
						return sb.toString();
					}
				}
			}
		}else {
			StringBuilder sb = new StringBuilder();
			
			sb.append("**How to use Shmames in 42 easy steps:**");
			
			for(ICommand c : CommandHandler.getLoadedCommands()) {
				String desc = c.getDescription().length() > 35 ? c.getDescription().substring(0, 35) + " [...]" : c.getDescription();
				sb.append("\n");
				sb.append("`"+c.getAliases()[0]+"` - "+desc);
			}
			
			author.openPrivateChannel().queue((c) -> c.sendMessage(sb.toString()).queue());
		
			return "PM'd you the deets :punch:";
		}
		
		return "I couldn't find that command :thinking:";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"help", "h"};
	}
}
