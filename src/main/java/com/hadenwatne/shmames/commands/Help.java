package com.hadenwatne.shmames.commands;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.models.Brain;
import com.hadenwatne.shmames.models.Lang;
import com.hadenwatne.shmames.enums.Langs;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.Shmames;
import com.hadenwatne.shmames.Utils;

public class Help implements ICommand {
	private final CommandStructure commandStructure;

	public Help() {
		this.commandStructure = CommandBuilder.Create("help")
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
	public String getDescription() {
		return "Shows help & additional information.";
	}
	
	@Override
	public String getUsage() {
		return this.commandStructure.getUsage();
	}

	@Override
	public String getExamples() {
		return "`help`\n" +
				"`help gif`";
	}

	@Override
	public String run (Lang lang, Brain brain, HashMap<String, Object> args, User author, MessageChannel channel) {
		if(args.size() > 0) {
			String commandHelp = (String) args.get("command");

			// Wants help on specific command.
			for(ICommand c : Shmames.getCommandHandler().getLoadedCommands()) {
				if(c.getCommandStructure().getName().equalsIgnoreCase(commandHelp)) {
					// Create list of aliases
					String list = Utils.generateList(c.getCommandStructure().getAliases(), -1, false, false);

					EmbedBuilder eBuilder = new EmbedBuilder();

					eBuilder.setAuthor("Help » "+c.getCommandStructure().getName());
					eBuilder.setColor(Color.MAGENTA);
					eBuilder.addField("Description", c.getDescription(), false);
					eBuilder.addField("Aliases", list, true);
					eBuilder.addField("Server-only", c.requiresGuild() ? "Yes" : "No", true);
					eBuilder.addField("Usage", c.getUsage(), false);
					eBuilder.addField("Examples", c.getExamples(), false);

					channel.sendMessageEmbeds(eBuilder.build()).queue();

					return "";
				}
			}
		}else {
			// Wants a list of all commands.
			List<String> cmds = new ArrayList<String>();

			for(ICommand c : Shmames.getCommandHandler().getLoadedCommands()) {
				if(c.getDescription().length() > 0) {
					cmds.add(c.getCommandStructure().getName());
				}
			}

			String list = Utils.generateList(cmds, -1, false, false);

			EmbedBuilder eBuilder = new EmbedBuilder();

			eBuilder.setColor(Color.MAGENTA);
			eBuilder.setTitle("Command Help for "+Shmames.getBotName());
			eBuilder.addField("All Commands", list, false);
			eBuilder.addField("Information", "View additional information for each command by using `"+Shmames.getBotName()+" help <command>`!", false);

			if(channel.getType() == ChannelType.TEXT){
				author.openPrivateChannel().queue((c) -> c.sendMessage(eBuilder.build()).queue());

				return lang.getMsg(Langs.SENT_PRIVATE_MESSAGE);
			}else{
				channel.sendMessageEmbeds(eBuilder.build()).queue();

				return "";
			}
		}

		return lang.getError(Errors.COMMAND_NOT_FOUND, true);
	}

	@Override
	public boolean requiresGuild() {
		return false;
	}
}
