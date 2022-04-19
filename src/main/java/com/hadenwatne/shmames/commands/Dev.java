package com.hadenwatne.shmames.commands;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hadenwatne.shmames.App;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import com.hadenwatne.shmames.services.LoggingService;
import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import com.hadenwatne.shmames.models.data.MotherBrain;
import com.hadenwatne.shmames.services.DataService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import com.hadenwatne.shmames.Shmames;

/**
 * This command is not exposed to users by default, and is here only for the benefit of the bot developer.
 * The goal is to provide easy-access commands in the event of bot maintenance, or to gauge which bot features
 * are used most.
 */
public class Dev extends Command {
	public Dev() {
		super(false);
	}

	@Override
	protected CommandStructure buildCommandStructure() {
		return CommandBuilder.Create("dev", "")
				.addAlias("developer")
				.addParameters(
						new CommandParameter("subCommand", "The subcommand to run", ParameterType.SELECTION)
								.addSelectionOptions("addstatus")
								.addSelectionOptions("getguilds")
								.addSelectionOptions("getcommandstats")
								.addSelectionOptions("clearcommandstats")
								.addSelectionOptions("leave")
								.addSelectionOptions("getreports")
								.addSelectionOptions("savebrains")
								.setExample("addstatus"),
						new CommandParameter("data", "The optional data to send to the subcommand", ParameterType.STRING, false)
								.setExample("stuff")
				)
				.build();
	}

	@Override
	public EmbedBuilder run (ExecutingCommand executingCommand) {
		if (executingCommand.getChannel() instanceof PrivateChannel) {
			if (executingCommand.getAuthorUser().getId().equals(App.Shmames.getStorageService().getMotherBrain().getBotAdminID())) {
				String subCommand = executingCommand.getCommandArguments().getAsString("subCommand");
				String commandData = executingCommand.getCommandArguments().getAsString("data");

				switch (subCommand.toLowerCase()) {
					case "addstatus":
						return addStatus(commandData);
					case "getguilds":
						return getGuilds();
					case "getcommandstats":
						return getCommandStats();
					case "clearcommandstats":
						clearCommandStats();

						return response(EmbedType.SUCCESS)
								.setDescription("Command statistics cleared!");
					case "leave":
						leave(commandData);

						return response(EmbedType.SUCCESS)
								.setDescription(App.Shmames.getBotName()+" is queued to leave the server!");
					case "getreports":
						return getReports(executingCommand);
					case "savebrains":
						saveBrains();

						return response(EmbedType.SUCCESS)
								.setDescription("All brains were saved!");
					default:
						return response(EmbedType.ERROR)
								.setDescription("That command wasn't recognized!");
				}
			} else {
				return response(EmbedType.ERROR)
						.setDescription("You cannot use the Developer command! This is used for bot maintenance tasks, and is restricted " +
								"to the bot developer.");
			}
		}

		return null;
	}

	private EmbedBuilder addStatus(String args) {
		Matcher m = Pattern.compile("^([a-z]+)\\s(.+)$", Pattern.CASE_INSENSITIVE).matcher(args);

		if (m.find()) {
			try {
				MotherBrain b = App.Shmames.getStorageService().getMotherBrain();
				ActivityType type = ActivityType.valueOf(m.group(1).toUpperCase());
				String msg = m.group(2);

				b.getStatuses().put(msg, type);
				App.Shmames.getJDA().getPresence().setActivity(Activity.of(type, msg));
				App.Shmames.getStorageService().getBrainController().saveMotherBrain();

				return response(EmbedType.INFO)
						.setDescription("New status added!");
			} catch (Exception e) {
				LoggingService.LogException(e);
			}
		}

		return response(EmbedType.ERROR)
				.setDescription("There was a problem adding your new status.");
	}

	private EmbedBuilder getGuilds() {
		StringBuilder sb = new StringBuilder();

		for (Guild g : App.Shmames.getJDA().getGuilds()) {
			if (sb.length() > 0)
				sb.append("\n");

			sb.append("> ");
			sb.append(g.getName());
			sb.append(" (");
			sb.append(g.getId());
			sb.append(" )");
		}

		sb.insert(0, "**Guilds the bot runs on**\n");

		return response(EmbedType.INFO)
				.addField("Guilds the bot runs on", sb.toString(), false);
	}

	private EmbedBuilder getCommandStats() {
		StringBuilder answer = new StringBuilder();

		// Sort
		LinkedHashMap<String, Integer> cmdStats = DataService.SortHashMap(App.Shmames.getStorageService().getMotherBrain().getCommandStats());

		for (String c : cmdStats.keySet()) {
			if(answer.length() > 0) {
				answer.append("\n");
			}

			answer.append("`").append(c).append("`: ").append(cmdStats.get(c));
		}

		return response(EmbedType.INFO)
				.setDescription(answer.toString());
	}

	private void clearCommandStats() {
		App.Shmames.getStorageService().getMotherBrain().getCommandStats().clear();
	}

	private void leave(String gid) {
		for (Guild g : App.Shmames.getJDA().getGuilds()) {
			if (g.getId().equals(gid)) {
				g.leave().queue();

				break;
			}
		}
	}

	private EmbedBuilder getReports(ExecutingCommand executingCommand) {
		StringBuilder reports = new StringBuilder("== User Reports ==");

		// Build list of reports.
		for (Guild g : App.Shmames.getJDA().getGuilds()) {
			Brain b = App.Shmames.getStorageService().getBrain(g.getId());

			if (b.getFeedback().size() > 0) {
				reports.append("\n");
				reports.append("== ").append(g.getName()).append(" ==");

				for (String r : b.getFeedback()) {
					reports.append("\n");
					reports.append(r);
				}
			}
		}

		// Save to file.
		File dir = new File("reports");
		File f = new File("reports/" + System.currentTimeMillis() + ".txt");
		dir.mkdirs();

		try {
			f.createNewFile();

			FileOutputStream fo = new FileOutputStream(f);
			fo.write(reports.toString().getBytes());
			fo.flush();
			fo.close();
		} catch (Exception e) {
			LoggingService.LogException(e);
		}

		// Send to me.
		EmbedBuilder response = response(EmbedType.INFO)
				.setTitle("Download Reports", "attachment://"+f.getName());
		executingCommand.replyFile(f, response);

		// Clear out guild feedback.
		for (Guild g : App.Shmames.getJDA().getGuilds()) {
			Brain b = App.Shmames.getStorageService().getBrain(g.getId());

			b.getFeedback().clear();
		}

		return response;
	}

	private void saveBrains() {
		for (Brain b : App.Shmames.getStorageService().getBrainController().getBrains()) {
			App.Shmames.getStorageService().getBrainController().saveBrain(b);
		}

		App.Shmames.getStorageService().getBrainController().saveMotherBrain();

		LoggingService.Write();
	}
}
