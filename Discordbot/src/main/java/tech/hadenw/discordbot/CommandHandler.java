package tech.hadenw.discordbot;

import java.nio.channels.Channel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dv8tion.jda.api.entities.*;
import tech.hadenw.discordbot.commands.*;
import tech.hadenw.discordbot.tasks.TypingTask;

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
		commands.add(new ForumWeaponList());
		commands.add(new ForumWeapon());
		commands.add(new GIF());
		commands.add(new Help());
		commands.add(new IdiotThat());
		commands.add(new Jinping());
		commands.add(new ListEmoteStats());
		commands.add(new ListResponses());
		commands.add(new ListTriggers());
		commands.add(new Minesweeper());
		commands.add(new Modify());
		commands.add(new Music());
		commands.add(new NewSeed());
		commands.add(new PinThat());
		commands.add(new React());
		commands.add(new Report());
		commands.add(new ResetEmoteStats());
		commands.add(new Roll());
		commands.add(new SetTally());
		commands.add(new ShowTallies());
		commands.add(new SimonSays());
		commands.add(new Source());
		commands.add(new Startpoll());
		commands.add(new Thoughts());
		commands.add(new Timeout());
		commands.add(new Timer());
		commands.add(new WhatShouldIDo());
		commands.add(new Wiki());
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
					// Record command use statistic.
					String alias = c.getAliases()[0].toLowerCase();
					HashMap<String, Integer> stats = Shmames.getBrains().getMotherBrain().getCommandStats();

					if(stats.containsKey(alias)) {
						int s = stats.get(alias);
						stats.put(alias, s+1);
					}else {
						stats.put(alias, 1);
					}

					// Execute command.
					if(!(server==null && c.requiresGuild())) {
						Matcher m = Pattern.compile("^("+a+")(.+)?$", Pattern.CASE_INSENSITIVE).matcher(cmd);

						if(m.matches()){
							String args = c.sanitize(m.group(2) != null ? m.group(2).trim() : "");

							// Run the command async and send a message back when it finishes.
							CompletableFuture.supplyAsync(() -> c.run(args, author, message))
									.thenAccept(r -> sendMessageToChannel(r, message.getChannel()));
						}
					}else {
						sendMessageToChannel(Errors.GUILD_REQUIRED, message.getChannel());
					}

					return;
				}
			}
		}

		sendMessageToChannel(Errors.COMMAND_NOT_FOUND, message.getChannel());
	}
	
	/**
	 * Gets a list of commands actively loaded.
	 * @return A list of commands.
	 */
	public static List<ICommand> getLoadedCommands(){
		return commands;
	}

	private void sendMessageToChannel(String r, MessageChannel channel){
		if(r != null) {
			if(r.length() > 0) {
				for(String m : splitStringEvery(r, 2000)){
					new TypingTask(channel, m);
				}
			}
		} else {
			new TypingTask(channel, Errors.COMMAND_NOT_FOUND);
		}
	}

	// https://stackoverflow.com/questions/12295711/split-a-string-at-every-nth-position
	private String[] splitStringEvery(String s, int interval) {
		int arrayLength = (int) Math.ceil(((s.length() / (double)interval)));
		String[] result = new String[arrayLength];

		int j = 0;
		int lastIndex = result.length - 1;

		for (int i = 0; i < lastIndex; i++) {
			result[i] = s.substring(j, j + interval);
			j += interval;
		}

		result[lastIndex] = s.substring(j);

		return result;
	}
}