package com.hadenwatne.shmames.commands;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hadenwatne.shmames.services.LoggingService;
import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import com.hadenwatne.shmames.models.data.MotherBrain;
import com.hadenwatne.shmames.services.DataService;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import com.hadenwatne.shmames.Shmames;

/**
 * This command is not exposed to users by default, and is here only for the benefit of the bot developer.
 * The goal is to provide easy-access commands in the event of bot maintenance, or to gauge which bot features
 * are used most.
 */
public class Dev implements ICommand {
	private final CommandStructure commandStructure;

	public Dev() {
		this.commandStructure = CommandBuilder.Create("dev", "")
				.addAlias("developer")
				.addParameters(
						new CommandParameter("subCommand", "The subcommand to run", ParameterType.SELECTION)
								.addSelectionOptions("addstatus")
								.addSelectionOptions("getguilds")
								.addSelectionOptions("getcommandstats")
								.addSelectionOptions("clearcommandstats")
								.addSelectionOptions("leave")
								.addSelectionOptions("getreports")
								.addSelectionOptions("savebrains"),
						new CommandParameter("data", "The optional data to send to the subcommand", ParameterType.STRING, false)
				)
				.build();
	}

	@Override
	public CommandStructure getCommandStructure() {
		return this.commandStructure;
	}

	@Override
	public String getExamples() {
		return "";
	}

	@Override
	public String run(Lang lang, Brain brain, ShmamesCommandData data) {
		if (data.getMessagingChannel().getChannel() instanceof PrivateChannel) {
			if (data.getAuthor().getId().equals("294671756155682828")) {
				String subCommand = data.getArguments().getAsString("subCommand");
				String commandData = data.getArguments().getAsString("data");

				switch (subCommand.toLowerCase()) {
					case "addstatus":
						return addStatus(commandData) ? "Status change successful!" : "Invalid syntax or bot error.";
					case "getguilds":
						return getGuilds();
					case "getcommandstats":
						return getCommandStats();
					case "clearcommandstats":
						clearCommandStats();
						return "Command statistics cleared!";
					case "leave":
						return leave(commandData) ? "Successfully left the server!" : "Could not leave that server.";
					case "getreports":
						getReports(data.getMessagingChannel().getChannel());
						return "";
					case "savebrains":
						saveBrains();
						return "All brains were saved!";
					default:
						return "That command wasn't recognized!";
				}
			} else {
				return "You cannot use the Developer command! This is used for bot maintenance tasks, and is restricted" +
						"to the bot developer.";
			}
		}

		return null;
	}

	@Override
	public boolean requiresGuild() {
		return false;
	}

	private boolean addStatus(String args) {
		Matcher m = Pattern.compile("^([a-z]+)\\s(.+)$", Pattern.CASE_INSENSITIVE).matcher(args);

		if (m.find()) {
			try {
				MotherBrain b = Shmames.getBrains().getMotherBrain();
				ActivityType type = ActivityType.valueOf(m.group(1).toUpperCase());
				String msg = m.group(2);

				b.getStatuses().put(msg, type);
				Shmames.getJDA().getPresence().setActivity(Activity.of(type, msg));
				Shmames.getBrains().saveMotherBrain();

				return true;
			} catch (Exception e) {
				return false;
			}
		} else {
			return false;
		}
	}

	private String getGuilds() {
		StringBuilder sb = new StringBuilder();

		for (Guild g : Shmames.getJDA().getGuilds()) {
			if (sb.length() > 0)
				sb.append("\n");

			sb.append("> ");
			sb.append(g.getName());
			sb.append(" (");
			sb.append(g.getId());
			sb.append(" )");
		}

		sb.insert(0, "**Guilds the bot runs on**\n");

		return sb.toString();
	}

	private String getCommandStats() {
		StringBuilder answer = new StringBuilder("**Command Usage Statistics**");

		// Sort
		LinkedHashMap<String, Integer> cmdStats = DataService.SortHashMap(Shmames.getBrains().getMotherBrain().getCommandStats());

		for (String c : cmdStats.keySet()) {
			answer.append("\n");
			answer.append("`").append(c).append("`: ").append(cmdStats.get(c));
		}

		return answer.toString();
	}

	private void clearCommandStats() {
		Shmames.getBrains().getMotherBrain().getCommandStats().clear();
	}

	private boolean leave(String gid) {
		for (Guild g : Shmames.getJDA().getGuilds()) {
			if (g.getId().equals(gid)) {
				g.leave().queue();

				return true;
			}
		}

		return false;
	}

	private void getReports(MessageChannel c) {
		StringBuilder reports = new StringBuilder("== User Reports ==");

		// Build list of reports.
		for (Guild g : Shmames.getJDA().getGuilds()) {
			Brain b = Shmames.getBrains().getBrain(g.getId());

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
		c.sendFile(f).complete();

		// Delete report on disk.
		f.delete();

		// Clear out guild feedback.
		for (Guild g : Shmames.getJDA().getGuilds()) {
			Brain b = Shmames.getBrains().getBrain(g.getId());

			b.getFeedback().clear();
		}
	}

	private void saveBrains() {
		for (Brain b : Shmames.getBrains().getBrains()) {
			Shmames.getBrains().saveBrain(b);
		}

		Shmames.getBrains().saveMotherBrain();

		LoggingService.Write();
	}
}
