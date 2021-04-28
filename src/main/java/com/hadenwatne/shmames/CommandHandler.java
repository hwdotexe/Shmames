package com.hadenwatne.shmames;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hadenwatne.shmames.commands.*;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.enums.LogType;
import com.hadenwatne.shmames.models.Brain;
import com.hadenwatne.shmames.models.Lang;
import net.dv8tion.jda.api.entities.*;
import com.hadenwatne.shmames.tasks.TypingTask;

import javax.annotation.Nullable;

// After the bot is summoned, this is called to determine which command to run
public class CommandHandler {
	private static List<ICommand> commands;
	private Lang lang;

	public CommandHandler() {
		commands = new ArrayList<ICommand>();
		lang = Shmames.getDefaultLang();
		
		commands.add(new AddResponse());
		commands.add(new AddTally());
		commands.add(new AddTrigger());
		commands.add(new Blame());
		commands.add(new Choose());
		commands.add(new CringeThat());
		commands.add(new Dev());
		commands.add(new DropResponse());
		commands.add(new DropTally());
		commands.add(new DropTrigger());
		commands.add(new EightBall());
		commands.add(new Enhance());
		commands.add(new FamilyCmd());
		commands.add(new ForumWeapon());
		commands.add(new GIF());
		commands.add(new Hangman());
		commands.add(new Help());
		commands.add(new IdiotThat());
		commands.add(new Jinping());
		commands.add(new ListEmoteStats());
		commands.add(new ListResponses());
		commands.add(new ListTallies());
		commands.add(new ListTriggers());
		commands.add(new Minesweeper());
		commands.add(new Modify());
		commands.add(new Music());
		commands.add(new Nickname());
		commands.add(new PinThat());
		commands.add(new Poll());
		commands.add(new React());
		commands.add(new Report());
		commands.add(new ResetEmoteStats());
		commands.add(new Roll());
		commands.add(new SetTally());
		commands.add(new SimonSays());
		commands.add(new Storytime());
		commands.add(new Thoughts());
		commands.add(new Timer());
		commands.add(new WhatAreTheOdds());
		commands.add(new WhatShouldIDo());
		commands.add(new When());
		commands.add(new Wiki());
	}
	
	/**
	 * Parses the command provided, and performs an action based on the command determined.
	 * @param cmd The raw String calling the command.
	 * @param message The message.
	 * @param author The user who is running the command.
	 * @param server The server the command is running on.
	 */
	public void PerformCommand(String cmd, Message message, User author, @Nullable Guild server) {
		if(cmd.length() == 0) {
			sendMessageResponse(lang.getError(Errors.HEY_THERE,false, new String[] { Shmames.getBotName() }), message);
			return;
		}

		ShmamesLogger.log(LogType.COMMAND, "["+ (server != null ? server.getId() : "Private Message") + "/" + author.getName() +"] "+ cmd);

		lang = Shmames.getLangFor(server);
		Brain brain = null;

		if(message.isFromGuild()) {
			brain = Shmames.getBrains().getBrain(server.getId());
		}
		
		for(ICommand c : commands) {
			for(String alias : c.getAliases()) {
				Matcher m = Pattern.compile("^("+alias+")(.+)?$", Pattern.CASE_INSENSITIVE).matcher(cmd);

				if(m.matches()){
					logCountCommandUsage(c);

					if(server == null && c.requiresGuild()) {
						sendMessageResponse(lang.getError(Errors.GUILD_REQUIRED, true), message);

						return;
					}

					String commandArguments = m.group(2) != null ? m.group(2).trim() : "";

					c.setRunContext(lang, brain);

					// Run the command async and send a message back when it finishes.
					try {
						CompletableFuture.supplyAsync(() -> c.run(commandArguments, author, message))
								.thenAccept(r -> sendMessageResponse(r, message))
								.exceptionally(exception -> {
									sendMessageResponse(lang.getError(Errors.BOT_ERROR, true), message);
									ShmamesLogger.logException(exception);
									return null;
								});
					}catch (Exception e){
						ShmamesLogger.logException(e);
						sendMessageResponse(lang.getError(Errors.BOT_ERROR, true), message);
					}

					return;
				}
			}
		}

		sendMessageResponse(lang.getError(Errors.COMMAND_NOT_FOUND, true), message);
	}
	
	/**
	 * Gets a list of commands actively loaded.
	 * @return A list of commands.
	 */
	public static List<ICommand> getLoadedCommands(){
		return commands;
	}

	private void sendMessageResponse(String r, Message msg){
		new TypingTask(r, msg);
	}

	private void logCountCommandUsage(ICommand command) {
		String primaryCommandName = command.getAliases()[0].toLowerCase();
		HashMap<String, Integer> stats = Shmames.getBrains().getMotherBrain().getCommandStats();
		int count = stats.getOrDefault(primaryCommandName, 1) + 1;

		stats.put(primaryCommandName, count);
	}
}
