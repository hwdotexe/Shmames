package tech.hadenw.shmamesbot.commands;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

// After the bot is summoned, this is called to determine which command to run
public class CommandHandler {
	private List<ICommand> commands;
	
	public CommandHandler() {
		commands = new ArrayList<ICommand>();
		
		commands.add(new Reload());
	}
	
	/**
	 * Parses the command provided, and performs an action based on the command determined.
	 * @param cmd The raw String calling the command.
	 * @param channel The message channel.
	 * @param author The user who is running the command.
	 * @param server The server the command is running on.
	 */
	public void PerformCommand(String cmd, MessageChannel channel, User author, Guild server) {
		// Loop through commands and find alias. If it matches, call that command's run() function.
		String base = cmd.contains(" ") ? cmd.substring(0, cmd.indexOf(" ")) : cmd;
		String args = cmd.length() > base.length() ? cmd.split(base, 2)[1].trim() : "";
		
		System.out.println("Base: "+base);
		System.out.println("Args: "+args);
		
		for(ICommand c : commands) {
			for(String a : c.getAliases()) {
				if(a.equalsIgnoreCase(base)) {
					channel.sendMessage(c.run(args)).queue();
					
					return;
				}
			}
		}
	}
}