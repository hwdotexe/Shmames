package tech.hadenw.discordbot.commands;

import java.awt.Color;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import tech.hadenw.discordbot.CommandHandler;
import tech.hadenw.discordbot.Errors;
import tech.hadenw.discordbot.Shmames;

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
				        eBuilder.setTitle(c.getDescription());
				        eBuilder.appendDescription("**Aliases:** "+alsb.toString()+"\n");
				        eBuilder.appendDescription("**Usage:** `"+c.getUsage()+"`\n");
				        eBuilder.appendDescription("**Server-Only:** `"+(c.requiresGuild() ? "Yes" : "No")+"`\n");
						
				        message.getChannel().sendMessage(eBuilder.build()).queue();
				        
						return "";
					}
				}
			}
		}else {
			// Wants a list of all commands.
			
			StringBuilder sb = new StringBuilder();
			
			sb.append(":small_orange_diamond: **Bot Commands** :small_orange_diamond:\nUse `"+Shmames.getBotName()+" help <command>` for more info!\n> ");
			
			int row = 0;
			for(ICommand c : CommandHandler.getLoadedCommands()) {
				if(row==3) {
					sb.append("\n> ");
					row = 0;
				}
				
				if(c.getDescription().length() > 0) {
					sb.append("`"+c.getAliases()[0]+"`, ");
					
					row++;
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
		return i;
	}
	
	@Override
	public boolean requiresGuild() {
		return false;
	}
}
