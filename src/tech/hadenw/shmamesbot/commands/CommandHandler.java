package tech.hadenw.shmamesbot.commands;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

// After the bot is summoned, this is called to determine which command to run
public class CommandHandler {
	private static List<ICommand> commands;
	
	public CommandHandler() {
		commands = new ArrayList<ICommand>();
		
		commands.add(new Help());
		commands.add(new Reload());
		commands.add(new AddStatus());
		commands.add(new SetStatus());
		commands.add(new AddTally());
		commands.add(new DropTally());
		commands.add(new ShowTallies());
		commands.add(new AddTrigger());
		commands.add(new RemoveTrigger());
		commands.add(new ListTriggers());
		commands.add(new AddResponse());
		commands.add(new ListResponses());
		commands.add(new EightBall());
		commands.add(new Roll());
		commands.add(new GIF());
		commands.add(new Test());
	}
	
	/**
	 * Parses the command provided, and performs an action based on the command determined.
	 * @param cmd The raw String calling the command.
	 * @param channel The message channel.
	 * @param author The user who is running the command.
	 * @param server The server the command is running on.
	 */
	public void PerformCommand(String cmd, MessageChannel channel, User author, Guild server) {
		for(ICommand c : commands) {
			for(String a : c.getAliases()) {
				if(cmd.toLowerCase().startsWith(a.toLowerCase())) {
					
					// TODO: "Hey james helpSomeCommandHere"
					// Using the positions, we might create accidental arguments
					
					int position = cmd.toLowerCase().indexOf(a.toLowerCase()) + a.length();
					String args = cmd.substring(position).trim();
					String r = c.run(args, author, server);
					
					if(r != null && r.length() > 0) {
						channel.sendMessage(r).queue();
					}
					
					return;
				}
			}
		}
	}
	
	/**
	 * Gets a list of commands actively loaded.
	 * @return A list of commands.
	 */
	public static List<ICommand> getLoadedCommands(){
		return commands;
	}
}