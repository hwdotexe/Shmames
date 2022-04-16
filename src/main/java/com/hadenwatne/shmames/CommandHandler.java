package com.hadenwatne.shmames;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.SubCommandGroup;
import com.hadenwatne.shmames.commands.*;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.factories.EmbedFactory;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import com.hadenwatne.shmames.models.command.ExecutingCommandArguments;
import com.hadenwatne.shmames.services.LoggingService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandHandler {
	private final List<Command> commands;

	public CommandHandler() {
		commands = new ArrayList<>();

		commands.add(new Blame());
//		commands.add(new Cactpot());
		commands.add(new Choose());
//		commands.add(new CringeThat());
//		commands.add(new Dev());
//		commands.add(new EightBall());
//		commands.add(new Enhance());
//		commands.add(new FamilyCmd());
//		commands.add(new ForumWeapon());
//		commands.add(new GIF());
//		commands.add(new Hangman());
//		commands.add(new Help());
//		commands.add(new IdiotThat());
//		commands.add(new ListCmd());
//		commands.add(new ListEmoteStats());
//		commands.add(new Minesweeper());
//		commands.add(new Modify());
//		commands.add(new Music());
//		commands.add(new PinThat());
//		commands.add(new Poll());
//		commands.add(new React());
//		commands.add(new Report());
//		commands.add(new ResetEmoteStats());
//		commands.add(new Response());
//		commands.add(new Roll());
//		commands.add(new SimonSays());
//		commands.add(new Storytime());
//		commands.add(new Tally());
//		commands.add(new Thoughts());
//		commands.add(new Timer());
//		commands.add(new Trigger());
//		commands.add(new WhatAreTheOdds());
//		commands.add(new WhatShouldIDo());
//		commands.add(new When());
//		commands.add(new Wiki());

		// Send Discord the syntax we plan to use for slash commands.
		updateSlashCommands();
	}

	/**
	 * Gets a list of commands actively loaded.
	 * @return A list of commands.
	 */
	public List<Command> getLoadedCommands(){
		return commands;
	}

	/**
	 * Attempts to match the raw command data to a Command. This does not check for usage errors.
	 * @param commandText The raw text of the command being run.
	 * @return A Command object if found, otherwise null.
	 */
	public Command PreProcessCommand(String commandText) {
		for(Command command : commands) {
			if(commandText.matches(command.getCommandStructure().getPattern().pattern())) {
				return command;
			}
		}

		return null;
	}

	/**
	 * Attempts to validate the command for usage errors.
	 * @param command The command to validate against.
	 * @param commandText The command usage to validate.
	 * @return True, if the validation was successful. Otherwise false.
	 */
	public boolean ValidateCommand(Command command, String commandText) {
		Matcher commandMatcher = command.getCommandStructure().getPattern().matcher(commandText);

		if(commandMatcher.find()) {
			return commandMatcher.group("context") != null;
		}

		return false;
	}


	// Requires we build a basic executingCommand in advance, which this function will append to.
	public ExecutingCommand ParseCommand(Command command, String commandText, ExecutingCommand executingCommand) {
		// TODO can we call the pattern matcher once instead of 3 times?

		Matcher commandMatcher = command.getCommandStructure().getPattern().matcher(commandText);

		if(commandMatcher.find()) {
			final String context = commandMatcher.group("context");
			final CommandStructure commandStructure = command.getCommandStructure();

			// Sub-command-group Specific
			for(SubCommandGroup subCommandGroup : commandStructure.getSubcommandGroups()) {
				final String groupMatch = matchStringToCommand(context, subCommandGroup.getName(), subCommandGroup.getAliases());

				if(groupMatch != null) {
					executingCommand.setSubCommandGroup(groupMatch);

					// Sub-command Specific
					for(CommandStructure subCommand : subCommandGroup.getSubcommands()) {
						final String subCommandMatch = matchStringToCommand(context, subCommand.getName(), subCommand.getAliases());

						if(subCommandMatch != null) {
							executingCommand.setSubCommand(subCommandMatch);
							ExecutingCommandArguments commandArguments = new ExecutingCommandArguments();

							// Parameter-specific
							for(CommandParameter commandParameter : subCommand.getParameters()) {
								String parameterMatch = commandMatcher.group(commandParameter.getRegexName());

								if(parameterMatch != null) {
									commandArguments.add(commandParameter.getName(), parameterMatch);
								}
							}

							executingCommand.setCommandArguments(commandArguments);

							break;
						}
					}

					break;
				}
			}

			// TODO sub-command only (!has group)

			// TODO parameter only (!has subcommand)
		}

		return executingCommand;
	}

	/**
	 * Asynchronously executes the provided command given an ExecuringCommand context.
	 * @param command The command to execute.
	 * @param executingCommand An object containing context for the command running.
	 */
	public void ExecuteCommand(Command command, ExecutingCommand executingCommand) {
		// TODO log command (log it here before or after validation? Spam vs. Debugging?)
		// TODO clean up typing indicator
		// TODO splitting up a long reply (message service!)

		CompletableFuture.supplyAsync(() -> command.run(executingCommand))
				.thenAccept(executingCommand::reply)
				.exceptionally(exception -> {
					EmbedBuilder embedBuilder = EmbedFactory.GetEmbed(EmbedType.ERROR, Errors.BOT_ERROR.name());
					embedBuilder.addField(null, executingCommand.getLanguage().getError(Errors.BOT_ERROR, true), false);

					executingCommand.reply(embedBuilder);
					LoggingService.LogException(exception);

					return null;
				});
	}

	private String matchStringToCommand(String toMatch, String commandName, List<String> aliases) {
		String match = null;
		Matcher commandBorderMatcher = Pattern.compile("^"+commandName+"\\b(.+)?", Pattern.CASE_INSENSITIVE).matcher(toMatch);

		if(commandBorderMatcher.find()) {
			match = commandName;
		} else {
			for(String alias : aliases) {
				Matcher aliasBorderMatcher = Pattern.compile("^"+alias+"\\b(.+)?", Pattern.CASE_INSENSITIVE).matcher(toMatch);

				if(aliasBorderMatcher.find()) {
					match = alias;
					break;
				}
			}
		}

		return match;
	}

	private void updateSlashCommands() {
		// Update command syntax on individual test servers.
		if(App.IsDebug) {
			for(Guild g : App.Shmames.getJDA().getGuilds()) {
				CommandListUpdateAction cUpdate = App.Shmames.getJDA().getGuildById(g.getId()).updateCommands();

				issueSlashCommandUpdate(cUpdate);
			}

			return;
		}

		// Update command syntax on Discord if configured to do so.
		if(App.Shmames.getStorageService().getMotherBrain().doUpdateDiscordSlashCommands()) {
			CommandListUpdateAction cUpdate = App.Shmames.getJDA().updateCommands();

			issueSlashCommandUpdate(cUpdate);
			App.Shmames.getStorageService().getMotherBrain().setUpdateDiscordSlashCommands(false);
		}
	}

	private void issueSlashCommandUpdate(CommandListUpdateAction cUpdate) {
		try {
			for (Command command : this.commands) {
				if (command.getCommandStructure().getDescription().length() > 0) {
					cUpdate.addCommands(CommandBuilder.BuildSlashCommandData(command));
				}
			}

			cUpdate.queue();
		}catch (Exception e) {
			LoggingService.LogException(e);
		}
	}
}
