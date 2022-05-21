package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.App;
import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.enums.ErrorKeys;
import com.hadenwatne.shmames.factories.EmbedFactory;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import com.hadenwatne.shmames.models.command.ExecutingCommandArguments;
import com.hadenwatne.shmames.services.PaginationService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.ArrayList;
import java.util.List;

public class Help extends Command {
	public Help() {
		super(false);
	}

	@Override
	protected CommandStructure buildCommandStructure() {
		return CommandBuilder.Create("help", "Shows help & additional information.")
				.addAlias("how do i use")
				.addAlias("how do you use")
				.addParameters(
						new CommandParameter("command", "The command you need help with", ParameterType.STRING, false)
								.setExample("gif")
				)
				.build();
	}

	@Override
	public EmbedBuilder run (ExecutingCommand executingCommand) {
		ExecutingCommandArguments args = executingCommand.getCommandArguments();
		String commandHelp = args.getAsString("command");
		EmbedBuilder embedBuilder = null;

		if(commandHelp != null) {
			embedBuilder = getCommandHelp(commandHelp);

			if(embedBuilder == null) {
				return response(EmbedType.ERROR)
						.addField(ErrorKeys.COMMAND_NOT_FOUND.name(), executingCommand.getLanguage().getError(ErrorKeys.COMMAND_NOT_FOUND), false);
			}
		} else {
			List<String> cmds = new ArrayList<>();

			for(Command command : App.Shmames.getCommandHandler().getLoadedCommands()) {
				if(command.getCommandStructure().getDescription().length() > 0) {
					cmds.add(command.getCommandStructure().getName());
				}
			}

			String list = PaginationService.GenerateList(cmds, -1, false, false);

			embedBuilder = EmbedFactory.GetEmbed(EmbedType.INFO, "Help");

			embedBuilder.addField("All Commands", list, false);
			embedBuilder.addField("Information", "View additional information for each command by using `"+App.Shmames.getBotName()+" help <command>`!", false);

			embedBuilder.setFooter(App.Shmames.getBotName() + (App.IsDebug ? " **Debug Mode**" : ""));

		}

		embedBuilder.setThumbnail(App.Shmames.getBotAvatarUrl());

		embedBuilder.addField("Syntax", "`<angle brackets>` are **required**\n" +
				"`[square brackets]` are **optional**\n" +
				"`[items|in|a|list]` are **possible values**", false);

		return embedBuilder;
	}

	private EmbedBuilder getCommandHelp(String commandHelp) {
		for(Command command : App.Shmames.getCommandHandler().getLoadedCommands()) {
			String commandName = command.getCommandStructure().getName();

			if(commandName.equalsIgnoreCase(commandHelp) || isAlias(command.getCommandStructure(), commandHelp)) {
				EmbedBuilder embedBuilder = EmbedFactory.GetEmbed(EmbedType.INFO, "Help", commandName);

				for(MessageEmbed.Field field : command.getHelpFields()) {
					embedBuilder.addField(field);
				}

				return embedBuilder;
			}
		}

		return null;
	}

	private boolean isAlias(CommandStructure structure, String commandHelp) {
		for(String alias : structure.getAliases()) {
			if(alias.equalsIgnoreCase(commandHelp)) {
				return true;
			}
		}

		return false;
	}
}
