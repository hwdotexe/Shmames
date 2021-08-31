package com.hadenwatne.shmames;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commands.*;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.enums.Langs;
import com.hadenwatne.shmames.enums.LogType;
import com.hadenwatne.shmames.models.command.*;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import net.dv8tion.jda.api.entities.*;
import com.hadenwatne.shmames.tasks.TypingTask;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import javax.annotation.Nullable;

// After the bot is summoned, this is called to determine which command to run
public class CommandHandler {
	private static List<ICommand> commands;
	private final Lang defaultLang = Shmames.getDefaultLang();

	public CommandHandler() {
		commands = new ArrayList<ICommand>();

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
		/*
		commands.add(new FamilyCmd());
		commands.add(new ForumWeapon());
		*/
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
		/*
		commands.add(new Music());
		*/
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

		// Send Discord the syntax we plan to use for slash commands.
		updateSlashCommands(commands);
	}

	/**
	 * Gets a list of commands actively loaded.
	 * @return A list of commands.
	 */
	public List<ICommand> getLoadedCommands(){
		return commands;
	}
	
	/**
	 * Parses the command provided, and performs an action based on the command determined.
	 * @param commandText The raw String calling the command.
	 * @param message The message.
	 * @param server The server the command is running on, if applicable.
	 * @param brain The brain of the server, if applicable.
	 */
	public void PerformCommand(String commandText, Message message, @Nullable Guild server, @Nullable Brain brain) {
		Lang lang = Shmames.getDefaultLang();
		User author = message.getAuthor();
		MessageChannel channel = message.getChannel();

		// Log the command.
		if(server != null) {
			ShmamesLogger.log(LogType.COMMAND, "["+ server.getId() + "/" + author.getName() +"] "+ commandText);
			lang = Shmames.getLangFor(server);
		} else {
			ShmamesLogger.log(LogType.COMMAND, "["+ "Private Message" + "/" + author.getName() +"] "+ commandText);
		}

		// Parse the command.
		ParsedCommandResult parsedCommand = parseCommandString(commandText);

		if(parsedCommand != null) {
			ICommand c = parsedCommand.getCommand();
			String args = parsedCommand.getArguments();

			logCountCommandUsage(c);

			if(server == null && c.requiresGuild()) {
				sendMessageResponse(lang.getError(Errors.GUILD_REQUIRED, true), new ShmamesCommandMessagingChannel(message, channel), lang);

				return;
			}

			// Validate the command usage.
			Matcher usageMatcher = c.getCommandStructure().getPattern().matcher(args);

			if(usageMatcher.matches()) {
				// Build map of arguments
				ShmamesSubCommandData subCommandData = null;
				LinkedHashMap<String, Object> commandArgs = buildArgumentsMap(c.getCommandStructure(), usageMatcher, server);

				// Build data for any subcommands this might have.
				for(CommandStructure subCommand : c.getCommandStructure().getSubcommands()) {
					if(args.toLowerCase().startsWith(subCommand.getName())) {
						LinkedHashMap<String, Object> subCommandArgs = buildArgumentsMap(subCommand, usageMatcher, server);

						subCommandData = new ShmamesSubCommandData(subCommand.getName(), new ShmamesCommandArguments(subCommandArgs));

						break;
					}
				}

				// Build command data.
				ShmamesCommandData data = new ShmamesCommandData(
						c,
						subCommandData,
						new ShmamesCommandArguments(commandArgs),
						new ShmamesCommandMessagingChannel(message, channel),
						author,
						server
				);

				// Execute the command
				executeCommand(lang, brain, data);
			}else{
				sendMessageResponse(lang.wrongUsage(c.getUsage()), new ShmamesCommandMessagingChannel(message, channel), lang);
			}
		} else {
			sendMessageResponse(lang.getError(Errors.COMMAND_NOT_FOUND, true), new ShmamesCommandMessagingChannel(message, channel), lang);
		}
	}

	/**
	 * Performs additional validation on the command provided, and runs it if valid.
	 * @param command The command to run.
	 * @param arguments Arguments to pass into the command.
	 * @param event The event that summoned this method.
	 * @param server The server this command is running on, if applicable.
	 */
	public void PerformCommand(ICommand command, ShmamesSubCommandData subCommand, ShmamesCommandArguments arguments, SlashCommandEvent event, @Nullable Guild server) {
		event.deferReply().queue();

		Brain brain = null;
		Lang lang = Shmames.getDefaultLang();
		User author = event.getUser();
		InteractionHook hook = event.getHook();

		String cmdString = (subCommand == null ? arguments.getAsString() : subCommand.getAsString()).trim();

		if (server != null) {
			ShmamesLogger.log(LogType.COMMAND, "[" + server.getId() + "/" + author.getName() + "] " + event.getName() + " " + cmdString);
			lang = Shmames.getLangFor(server);
			brain = Shmames.getBrains().getBrain(server.getId());
		} else {
			ShmamesLogger.log(LogType.COMMAND, "[" + "Private Message" + "/" + author.getName() + "] " + event.getName() + " " + cmdString);

			if (command.requiresGuild()) {
				sendMessageResponse(lang.getError(Errors.GUILD_REQUIRED, true), new ShmamesCommandMessagingChannel(hook, event.getChannel()), lang);

				return;
			}
		}

		// Validate the command usage.
		Matcher usageMatcher = command.getCommandStructure().getPattern().matcher(cmdString);

		if (usageMatcher.matches()) {
			// Build command data.
			ShmamesCommandData data = new ShmamesCommandData(
					command,
					subCommand,
					arguments,
					new ShmamesCommandMessagingChannel(hook, event.getChannel()),
					author,
					server
			);

			// Execute the command.
			executeCommand(lang, brain, data);
		} else {
			sendMessageResponse(lang.wrongUsage(command.getUsage()), new ShmamesCommandMessagingChannel(event.getHook(), event.getChannel()), lang);
		}
	}

	public @Nullable ParsedCommandResult parseCommandString(String cmd) {
		for(ICommand c : commands) {
			Matcher nameMatcher = Pattern.compile("^(" + c.getCommandStructure().getName() + ")\\b(.+)?$", Pattern.CASE_INSENSITIVE).matcher(cmd);

			if (nameMatcher.matches()) {
				return new ParsedCommandResult(c, nameMatcher.group(2) != null ? nameMatcher.group(2).trim() : "");
			} else {
				for(String a : c.getCommandStructure().getAliases()) {
					Matcher aliasMatcher = Pattern.compile("^(" + a + ")\\b(.+)?$", Pattern.CASE_INSENSITIVE).matcher(cmd);

					if(aliasMatcher.matches()) {
						return new ParsedCommandResult(c, aliasMatcher.group(2) != null ? aliasMatcher.group(2).trim() : "");
					}
				}
			}
		}

		return null;
	}

	private void executeCommand(Lang lang, Brain brain, ShmamesCommandData data) {
		try {
			CompletableFuture.supplyAsync(() -> data.getCommand().run(lang, brain, data))
					.thenAccept(r -> sendMessageResponse(r, data.getMessagingChannel(), lang))
					.exceptionally(exception -> {
						if(data.getMessagingChannel().hasHook()) {
							data.getMessagingChannel().getHook().setEphemeral(true);
						}

						sendMessageResponse(lang.getError(Errors.BOT_ERROR, true), data.getMessagingChannel(), lang);
						ShmamesLogger.logException(exception);
						return null;
					});
		}catch (Exception e){
			if(data.getMessagingChannel().hasHook()) {
				data.getMessagingChannel().getHook().setEphemeral(true);
			}

			ShmamesLogger.logException(e);
			sendMessageResponse(lang.getError(Errors.BOT_ERROR, true), data.getMessagingChannel(), lang);
		}
	}

	private void sendMessageResponse(String r, ShmamesCommandMessagingChannel msg, Lang lang) {
		if (msg.hasHook()) {
			if (r.length() > 0) {
				for (String m : Utils.splitString(r, 2000)) {
					msg.getHook().sendMessage(m).queue();
				}
			} else {
				msg.getHook().setEphemeral(true);
				msg.getHook().sendMessage(lang.getMsg(Langs.GENERIC_SUCCESS)).queue();
			}
		} else {
			new TypingTask(r, msg.getChannel(), false);
		}
	}

	private LinkedHashMap<String, Object> buildArgumentsMap(CommandStructure c, Matcher usageMatcher, Guild server) {
		LinkedHashMap<String, Object> namedArguments = new LinkedHashMap<>();

		for(CommandParameter cp : c.getParameters()) {
			String group = usageMatcher.group(cp.getName());

			if(group != null) {
				insertArgumentWithType(namedArguments, group, cp, server);
			}
		}

		return namedArguments;
	}

	private void logCountCommandUsage(ICommand command) {
		String primaryCommandName = command.getCommandStructure().getName().toLowerCase();
		HashMap<String, Integer> stats = Shmames.getBrains().getMotherBrain().getCommandStats();
		int count = stats.getOrDefault(primaryCommandName, 1) + 1;

		stats.put(primaryCommandName, count);
	}

	private void updateSlashCommands(List<ICommand> commands) {
		// Update command syntax on individual test servers.
		if(Shmames.isDebug) {
			for(Guild g : Shmames.getJDA().getGuilds()) {
				CommandListUpdateAction cUpdate = Shmames.getJDA().getGuildById(g.getId()).updateCommands();

				for(ICommand command : commands) {
					if(command.getDescription().length() > 0) {
						cUpdate.addCommands(CommandBuilder.BuildCommandData(command));
					}
				}

				cUpdate.queue();
			}

			return;
		}

		// Update command syntax on Discord if configured to do so.
		if(Shmames.getBrains().getMotherBrain().doUpdateDiscordSlashCommands()) {
			CommandListUpdateAction cUpdate = Shmames.getJDA().updateCommands();

			for(ICommand command : commands) {
				cUpdate.addCommands(CommandBuilder.BuildCommandData(command));
			}

			cUpdate.queue();
			Shmames.getBrains().getMotherBrain().setUpdateDiscordSlashCommands(false);
		}
	}

	private void insertArgumentWithType(HashMap<String, Object> map, String value, CommandParameter parameter, @Nullable Guild guild) {
		switch(parameter.getType()) {
			case BOOLEAN:
				map.put(parameter.getName(), Boolean.valueOf(value));
				break;
			case INTEGER:
				map.put(parameter.getName(), Integer.parseInt(value));
				break;
			case DISCORD_ROLE:
				if(guild != null) {
					Matcher m = parameter.getPattern().matcher(value);

					if(m.find()){
						Role role = guild.getRoleById(m.group(2));
						map.put(parameter.getName(), role);
						break;
					}
				}
			case DISCORD_USER:
				if(guild != null) {
					Matcher m = parameter.getPattern().matcher(value);

					if(m.find()){
						User user  = guild.getMemberById(m.group(2)).getUser();
						map.put(parameter.getName(), user);
						break;
					}
				}
			case DISCORD_EMOTE:
				if(guild != null) {
					Matcher m = parameter.getPattern().matcher(value);

					if(m.find()){
						Emote emote = guild.getEmoteById(m.group(2));
						map.put(parameter.getName(), emote);
						break;
					}
				}
			case DISCORD_CHANNEL:
				if(guild != null) {
					Matcher m = parameter.getPattern().matcher(value);

					if(m.find()){
						MessageChannel channel = guild.getTextChannelById(m.group(2));
						map.put(parameter.getName(), channel);
						break;
					}
				}
			default:
				map.put(parameter.getName(), value);
		}
	}
}
