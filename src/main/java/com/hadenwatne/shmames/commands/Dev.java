package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.App;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.factories.EmbedFactory;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.MotherBrain;
import com.hadenwatne.shmames.services.DataService;
import com.hadenwatne.shmames.services.LoggingService;
import com.hadenwatne.shmames.services.MessageService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;

import java.io.File;
import java.io.FileOutputStream;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This command is not exposed to users by default, and is here only for the benefit of the bot developer.
 * The goal is to provide easy-access commands in the event of bot maintenance, or to gauge which bot features
 * are used most.
 */
public class Dev {
	public static EmbedBuilder run (Message message, String command) {
		String[] cmdSplit = command.split("\\s", 2);

		String subCommand = cmdSplit[0];
		String commandData = cmdSplit[Math.min(1, cmdSplit.length-1)];

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
						.setDescription(App.Shmames.getBotName() + " is queued to leave the server!");
			case "getreports":
				return getReports(message);
			case "savebrains":
				saveBrains();

				return response(EmbedType.SUCCESS)
						.setDescription("All brains were saved!");
			default:
				return response(EmbedType.ERROR)
						.setDescription("Valid commands: addstatus, getguilds, getcommandstats, clearcommandstats, leave, getreports, savebrains");
		}
	}

	private static EmbedBuilder addStatus(String args) {
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

	private static EmbedBuilder getGuilds() {
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

		return response(EmbedType.INFO)
				.addField("Guilds the bot runs on", sb.toString(), false);
	}

	private static EmbedBuilder getCommandStats() {
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

	private static void clearCommandStats() {
		App.Shmames.getStorageService().getMotherBrain().getCommandStats().clear();
	}

	private static void leave(String gid) {
		for (Guild g : App.Shmames.getJDA().getGuilds()) {
			if (g.getId().equals(gid)) {
				g.leave().queue();

				break;
			}
		}
	}

	private static EmbedBuilder getReports(Message message) {
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

				b.getFeedback().clear();
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
		EmbedBuilder response = response(EmbedType.SUCCESS)
				.setDescription("Reports were gathered and cleared from servers!");

		MessageService.ReplyToMessage(message, f, response, false);

		return null;
	}

	private static void saveBrains() {
		for (Brain b : App.Shmames.getStorageService().getBrainController().getBrains()) {
			App.Shmames.getStorageService().getBrainController().saveBrain(b);
		}

		App.Shmames.getStorageService().getBrainController().saveMotherBrain();

		LoggingService.Write();
	}

	private static EmbedBuilder response(EmbedType type) {
		return EmbedFactory.GetEmbed(type, "dev");
	}
}
