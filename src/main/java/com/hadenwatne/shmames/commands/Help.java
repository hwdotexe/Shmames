package com.hadenwatne.shmames.commands;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.command.ShmamesCommandMessagingChannel;
import com.hadenwatne.shmames.models.data.Lang;
import com.hadenwatne.shmames.enums.Langs;
import com.hadenwatne.shmames.models.command.ShmamesCommandArguments;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.Shmames;
import com.hadenwatne.shmames.Utils;

public class Help implements ICommand {
	private final CommandStructure commandStructure;

	public Help() {
		this.commandStructure = CommandBuilder.Create("help", "Shows help & additional information.")
				.addAlias("how do i use")
				.addAlias("how do you use")
				.addParameters(
						new CommandParameter("command", "The command you need help with", ParameterType.STRING, false)
				)
				.build();
	}

	@Override
	public CommandStructure getCommandStructure() {
		return this.commandStructure;
	}

	@Override
	public String getExamples() {
		return "`help`\n" +
				"`help gif`";
	}

	@Override
	public String run (Lang lang, Brain brain, ShmamesCommandData data) {
		ShmamesCommandArguments args = data.getArguments();
		ShmamesCommandMessagingChannel messagingChannel = data.getMessagingChannel();

		if(args.count() > 0) {
			String commandHelp = args.getAsString("command");

			// Wants help on specific command.
			for(ICommand c : Shmames.getCommandHandler().getLoadedCommands()) {
				if(c.getCommandStructure().getName().equalsIgnoreCase(commandHelp)) {
					// Create list of aliases
					String list = Utils.generateList(c.getCommandStructure().getAliases(), -1, false, false);

					EmbedBuilder eBuilder = new EmbedBuilder();

					eBuilder.setAuthor("Help » "+c.getCommandStructure().getName());
					eBuilder.setColor(Color.MAGENTA);
					eBuilder.addField("Description", c.getCommandStructure().getDescription(), false);
					eBuilder.addField("Aliases", list, true);
					eBuilder.addField("Server-only", c.requiresGuild() ? "Yes" : "No", true);
					eBuilder.addField("Usage", c.getCommandStructure().getUsage(), false);
					eBuilder.addField("Examples", c.getExamples(), false);

					messagingChannel.sendMessage(eBuilder);

					return "";
				}
			}

			return lang.getError(Errors.COMMAND_NOT_FOUND, true);
		} else {
			// Wants a list of all commands.
			List<String> cmds = new ArrayList<>();

			for(ICommand c : Shmames.getCommandHandler().getLoadedCommands()) {
				if(c.getCommandStructure().getDescription().length() > 0) {
					cmds.add(c.getCommandStructure().getName());
				}
			}

			String list = Utils.generateList(cmds, -1, false, false);

			EmbedBuilder eBuilder = new EmbedBuilder();

			eBuilder.setColor(Color.MAGENTA);
			eBuilder.setTitle("Command Help for "+Shmames.getBotName());
			eBuilder.addField("All Commands", list, false);
			eBuilder.addField("Information", "View additional information for each command by using `"+Shmames.getBotName()+" help <command>`!", false);

			if(messagingChannel.hasHook()) {
				messagingChannel.sendMessage(eBuilder);
			} else {
				if (messagingChannel.getChannel().getType() == ChannelType.TEXT) {
					data.getAuthor().openPrivateChannel().queue((c) -> c.sendMessageEmbeds(eBuilder.build()).queue(), (error) -> messagingChannel.sendMessage(eBuilder));

					return lang.getMsg(Langs.SENT_PRIVATE_MESSAGE);
				} else if (messagingChannel.getChannel().getType() == ChannelType.PRIVATE) {
					messagingChannel.sendMessage(eBuilder);
				}
			}

			return "";
		}
	}

	@Override
	public boolean requiresGuild() {
		return false;
	}
}
