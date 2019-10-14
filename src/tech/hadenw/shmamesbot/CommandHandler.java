package tech.hadenw.shmamesbot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import tech.hadenw.shmamesbot.commands.AddResponse;
import tech.hadenw.shmamesbot.commands.AddTally;
import tech.hadenw.shmamesbot.commands.AddTrigger;
import tech.hadenw.shmamesbot.commands.Blame;
import tech.hadenw.shmamesbot.commands.Choose;
import tech.hadenw.shmamesbot.commands.Closepoll;
import tech.hadenw.shmamesbot.commands.CringeThat;
import tech.hadenw.shmamesbot.commands.Dev;
import tech.hadenw.shmamesbot.commands.DropResponse;
import tech.hadenw.shmamesbot.commands.DropTally;
import tech.hadenw.shmamesbot.commands.DropTrigger;
import tech.hadenw.shmamesbot.commands.EightBall;
import tech.hadenw.shmamesbot.commands.Enhance;
import tech.hadenw.shmamesbot.commands.GIF;
import tech.hadenw.shmamesbot.commands.Help;
import tech.hadenw.shmamesbot.commands.ICommand;
import tech.hadenw.shmamesbot.commands.IdiotThat;
import tech.hadenw.shmamesbot.commands.Invite;
import tech.hadenw.shmamesbot.commands.Jinping;
import tech.hadenw.shmamesbot.commands.ListEmoteStats;
import tech.hadenw.shmamesbot.commands.ListResponses;
import tech.hadenw.shmamesbot.commands.ListTriggers;
import tech.hadenw.shmamesbot.commands.Minesweeper;
import tech.hadenw.shmamesbot.commands.Modify;
import tech.hadenw.shmamesbot.commands.NewSeed;
import tech.hadenw.shmamesbot.commands.PinThat;
import tech.hadenw.shmamesbot.commands.React;
import tech.hadenw.shmamesbot.commands.Report;
import tech.hadenw.shmamesbot.commands.ResetEmoteStats;
import tech.hadenw.shmamesbot.commands.Roll;
import tech.hadenw.shmamesbot.commands.ShowTallies;
import tech.hadenw.shmamesbot.commands.SimonSays;
import tech.hadenw.shmamesbot.commands.Startpoll;
import tech.hadenw.shmamesbot.commands.Thoughts;
import tech.hadenw.shmamesbot.commands.Timeout;
import tech.hadenw.shmamesbot.commands.Timer;
import tech.hadenw.shmamesbot.commands.WhatShouldIDo;

// After the bot is summoned, this is called to determine which command to run
public class CommandHandler {
	private static List<ICommand> commands;
	
	public CommandHandler() {
		commands = new ArrayList<ICommand>();
		
		commands.add(new AddResponse());
		commands.add(new AddTally());
		commands.add(new AddTrigger());
		commands.add(new Blame());
		commands.add(new Choose());
		commands.add(new Closepoll());
		commands.add(new CringeThat());
		commands.add(new Dev());
		commands.add(new DropResponse());
		commands.add(new DropTally());
		commands.add(new DropTrigger());
		commands.add(new EightBall());
		commands.add(new Enhance());
		commands.add(new GIF());
		commands.add(new Help());
		commands.add(new IdiotThat());
		commands.add(new Invite());
		commands.add(new Jinping());
		//commands.add(new IsDevAlive());
		commands.add(new ListEmoteStats());
		commands.add(new ListResponses());
		commands.add(new ListTriggers());
		commands.add(new Minesweeper());
		commands.add(new Modify());
		commands.add(new NewSeed());
		commands.add(new PinThat());
		commands.add(new React());
		commands.add(new Report());
		commands.add(new ResetEmoteStats());
		commands.add(new Roll());
		commands.add(new SimonSays());
		commands.add(new ShowTallies());
		commands.add(new Startpoll());
		commands.add(new Thoughts());
		commands.add(new Timeout());
		commands.add(new Timer());
		commands.add(new WhatShouldIDo());
	}
	
	/**
	 * Parses the command provided, and performs an action based on the command determined.
	 * @param cmd The raw String calling the command.
	 * @param message The message.
	 * @param author The user who is running the command.
	 * @param server The server the command is running on.
	 */
	public void PerformCommand(String cmd, Message message, User author, Guild server) {
		if(cmd.length() == 0) {
			message.getChannel().sendMessage(Errors.HEY_THERE).queue();
			return;
		}
		
		for(ICommand c : commands) {
			for(String a : c.getAliases()) {
				if(cmd.toLowerCase().startsWith(a.toLowerCase())) {
					
					// Log command usage
					String alias = c.getAliases()[0].toLowerCase();
					HashMap<String, Integer> stats = Shmames.getBrains().getMotherBrain().getCommandStats();
					
					if(stats.containsKey(alias)) {
						int s = stats.get(alias);
						stats.put(alias, s+1);
					}else {
						stats.put(alias, 1);
					}
					
					if(!(server==null && c.requiresGuild())) {
						int position = cmd.toLowerCase().indexOf(a.toLowerCase()) + a.length();
						String args = c.sanitize(cmd.substring(position).trim());
						String r = c.run(args, author, message);
						
						if(r != null) {
							if(r.length() > 0) {
								
								if(r.length() > 2000) {
									String h1 = r.substring(0, 2000);
									h1 = h1.substring(0, h1.lastIndexOf(" "));
									
									String h2 = r.substring(h1.length()-1);
									
									message.getChannel().sendMessage(h1).queue();
									message.getChannel().sendMessage(h2).queue();
									return;
								}
								
								//message.getChannel().sendMessage(r).queue();
								new Typing(message.getChannel(), r);
							}
						}else {
							// If a command returns null, send the 404 message.
							message.getChannel().sendMessage(Errors.COMMAND_NOT_FOUND).queue();
						}
					}else {
						message.getChannel().sendMessage(Errors.GUILD_REQUIRED).queue();
					}
					
					return;
				}
			}
		}
		
		message.getChannel().sendMessage(Errors.COMMAND_NOT_FOUND).queue();
	}
	
	/**
	 * Gets a list of commands actively loaded.
	 * @return A list of commands.
	 */
	public static List<ICommand> getLoadedCommands(){
		return commands;
	}
}