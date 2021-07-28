package com.hadenwatne.shmames;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commands.*;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.enums.LogType;
import com.hadenwatne.shmames.models.Brain;
import com.hadenwatne.shmames.models.Lang;
import com.hadenwatne.shmames.models.ParsedCommandResult;
import net.dv8tion.jda.api.entities.*;
import com.hadenwatne.shmames.tasks.TypingTask;

import javax.annotation.Nullable;

// After the bot is summoned, this is called to determine which command to run
public class CommandHandler {
	private static List<ICommand> commands;
	private final Lang defaultLang = Shmames.getDefaultLang();

	public CommandHandler() {
		commands = new ArrayList<ICommand>();

//		// TODO reset all commands
//		List<Command> cmds = Shmames.getJDA().retrieveCommands().complete();
//
//		for(Command c : cmds) {
//			Shmames.getJDA().deleteCommandById(c.getId()).queue();
//		}

//		// TODO temporary patch - Guild ID 344604589976453144
//		CommandListUpdateAction cUpdate = Shmames.getJDA().getGuildById("344604589976453144").updateCommands();
//
//		AddResponse cmd = new AddResponse();
//
//		cUpdate.addCommands(cmd.getCommandData());
//
//		commands.add(cmd);
//
//		cUpdate.queue();
//		// TODO End

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
		commands.add(new ListCmd());
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
			sendMessageResponse(defaultLang.getError(Errors.HEY_THERE,false, new String[] { Shmames.getBotName() }), message);
			return;
		}

		Brain brain = null;
		Lang lang = Shmames.getDefaultLang();

		if(server != null) {
			ShmamesLogger.log(LogType.COMMAND, "["+ server.getId() + "/" + author.getName() +"] "+ cmd);
			lang = Shmames.getLangFor(server);
			brain = Shmames.getBrains().getBrain(server.getId());
		} else {
			ShmamesLogger.log(LogType.COMMAND, "["+ "Private Message" + "/" + author.getName() +"] "+ cmd);
		}

		ParsedCommandResult parsedCommand = getMatchedCommand(cmd);

		if(parsedCommand != null) {
			ICommand c = parsedCommand.getCommand();
			String args = parsedCommand.getArguments();

			logCountCommandUsage(c);

			if(server == null && c.requiresGuild()) {
				sendMessageResponse(lang.getError(Errors.GUILD_REQUIRED, true), message);

				return;
			}

			// Validate the command usage.
			Matcher usageMatcher = c.getCommandStructure().getPattern().matcher(args);

			if(usageMatcher.matches()) {
				// Build map of arguments
				HashMap<String, String> namedArguments = new HashMap<>();

				for(CommandParameter cp : c.getCommandStructure().getParameters()) {
					String group = usageMatcher.group(cp.getName());

					if(group != null) {
						namedArguments.put(cp.getName(), group);
					}
				}

				// Execute the command
				executeCommand(lang, brain, c, namedArguments, author, message.getChannel());
			}else{
				sendMessageResponse(lang.wrongUsage(c.getUsage()), message);
			}
		} else {
			sendMessageResponse(lang.getError(Errors.COMMAND_NOT_FOUND, true), message);
		}
	}
	
	/**
	 * Gets a list of commands actively loaded.
	 * @return A list of commands.
	 */
	public static List<ICommand> getLoadedCommands(){
		return commands;
	}

	private void executeCommand(Lang lang, Brain brain, ICommand c, HashMap<String, String> arguments, User author, MessageChannel channel) {
		try {
			CompletableFuture.supplyAsync(() -> c.run(lang, brain, arguments, author, channel))
					.thenAccept(r -> sendMessageResponse(r, channel))
					.exceptionally(exception -> {
						sendMessageResponse(lang.getError(Errors.BOT_ERROR, true), channel);
						ShmamesLogger.logException(exception);
						return null;
					});
		}catch (Exception e){
			ShmamesLogger.logException(e);
			sendMessageResponse(lang.getError(Errors.BOT_ERROR, true), channel);
		}
	}

	private ParsedCommandResult getMatchedCommand(String cmd) {
		for(ICommand c : commands) {
			Matcher nameMatcher = Pattern.compile("^(" + c.getCommandStructure().getName() + ")(.+)?$", Pattern.CASE_INSENSITIVE).matcher(cmd);

			if (nameMatcher.matches()) {
				return new ParsedCommandResult(c, nameMatcher.group(2) != null ? nameMatcher.group(2).trim() : "");
			} else {
				for(String a : c.getCommandStructure().getAliases()) {
					Matcher aliasMatcher = Pattern.compile("^(" + a + ")(.+)?$", Pattern.CASE_INSENSITIVE).matcher(cmd);

					if(aliasMatcher.matches()) {
						return new ParsedCommandResult(c, nameMatcher.group(2) != null ? nameMatcher.group(2).trim() : "");
					}
				}
			}
		}

		return null;
	}

	private void sendMessageResponse(String r, Message msg){
		new TypingTask(r, msg, true);
	}

	private void sendMessageResponse(String r, MessageChannel chn){
		new TypingTask(r, chn, false);
	}

	private void logCountCommandUsage(ICommand command) {
		String primaryCommandName = command.getCommandStructure().getName().toLowerCase();
		HashMap<String, Integer> stats = Shmames.getBrains().getMotherBrain().getCommandStats();
		int count = stats.getOrDefault(primaryCommandName, 1) + 1;

		stats.put(primaryCommandName, count);
	}
}
