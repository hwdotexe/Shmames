package com.hadenwatne.shmames.commands;

import com.hadenwatne.corvus.Corvus;
import com.hadenwatne.corvus.CorvusBuilder;
import com.hadenwatne.fornax.App;
import com.hadenwatne.fornax.command.Command;
import com.hadenwatne.fornax.command.Execution;
import com.hadenwatne.fornax.command.builder.CommandBuilder;
import com.hadenwatne.fornax.command.builder.CommandParameter;
import com.hadenwatne.fornax.command.builder.CommandStructure;
import com.hadenwatne.fornax.command.builder.types.ParameterType;
import com.hadenwatne.shmames.Shmames;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.MotherBrain;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.dv8tion.jda.api.entities.Guild;

public class Dev extends Command {
	private Shmames shmames;

	public Dev(Shmames shmames) {
		super(false, false, false, false);
		this.shmames = shmames;
	}

	@Override
	protected CommandStructure buildCommandStructure() {
		return CommandBuilder.Create("dev", "A utility command for the developer.")
				.addSubCommands(CommandBuilder.Create("addstatus", "Add a new Discord status.")
								.addParameters(new CommandParameter("activity", "The activity type", ParameterType.SELECTION)
										.addSelectionOptions(ActivityType.PLAYING.name())
										.addSelectionOptions(ActivityType.WATCHING.name())
										.addSelectionOptions(ActivityType.LISTENING.name())
										.addSelectionOptions(ActivityType.CUSTOM_STATUS.name()))
								.addParameters(new CommandParameter("text", "The text to display", ParameterType.STRING))
								.build(),
						CommandBuilder.Create("getguilds", "List the guilds this bot has joined.")
								.build(),
						CommandBuilder.Create("leaveguild", "Force the bot to leave a guild.")
								.addParameters(new CommandParameter("serverid", "The server ID to leave", ParameterType.STRING))
								.build(),
						CommandBuilder.Create("savebrains", "Saves brain data to disk.")
								.build()
//						CommandBuilder.Create("getReports", "Get bug report data.")
//								.build()
				)
				.build();
	}

	@Override
	protected Permission[] configureRequiredBotPermissions() {
		return new Permission[]{Permission.MESSAGE_SEND, Permission.MESSAGE_SEND_IN_THREADS, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ATTACH_FILES};
	}

	@Override
	protected Permission[] configureRequiredUserPermissions() {
		return new Permission[]{Permission.ADMINISTRATOR};
	}

	@Override
	public void onCommandFailure(Execution execution) {

	}

	@Override
	public void run(Execution execution) {
		if (execution.getUser().getId().equals(execution.getBot().getBotDataStorageService().getBotConfiguration().adminDiscordID)) {
			String subCommand = execution.getSubCommand();

			switch (subCommand) {
				case "addstatus":
					addStatus(execution);
					break;
				case "getguilds":
					getGuilds(execution);
					break;
				case "leaveguild":
					leave(execution);
					break;
//				case "getreports":
//					return getReports(executingCommand);
				case "savebrains":
					saveBrains(execution);
					break;
			}
		} else {
			CorvusBuilder builder = Corvus.error(execution.getBot());

			builder.addBreadcrumbs(this.getCommandStructure().getName())
					.setDescription("Sorry! This command is reserved for the bot developer only.");

			Corvus.reply(execution, builder);
		}
	}

	private void addStatus(Execution execution) {
		try {
			MotherBrain b = shmames.getBrainController().getMotherBrain();
			ActivityType type = ActivityType.valueOf(execution.getArguments().get("activity").getAsString());
			String msg = execution.getArguments().get("text").getAsString();

			b.getStatuses().put(msg, type);
			execution.getBot().getJDA().getPresence().setActivity(Activity.of(type, msg));
			shmames.getBrainController().saveMotherBrain();

			CorvusBuilder builder = Corvus.success(execution.getBot());

			builder.addBreadcrumbs(this.getCommandStructure().getName())
					.setDescription("New activity added!");

			Corvus.reply(execution, builder);
		} catch (Exception e) {
			App.getLogger().LogException(e);
		}
	}

	private void getGuilds(Execution execution) {
		StringBuilder sb = new StringBuilder();

		for (Guild g : execution.getBot().getJDA().getGuilds()) {
			if (!sb.isEmpty())
				sb.append("\n");

			sb.append("> ");
			sb.append(g.getName());
			sb.append(" (");
			sb.append(g.getId());
			sb.append(")");
		}

		CorvusBuilder builder = Corvus.privileged(execution.getBot());

		builder.addBreadcrumbs(this.getCommandStructure().getName())
				.addField("Guilds the bot runs on", sb.toString(), false);

		Corvus.reply(execution, builder);
	}

	private void leave(Execution execution) {
		String gid = execution.getArguments().get("serverid").getAsString();

		for (Guild g : execution.getBot().getJDA().getGuilds()) {
			if (g.getId().equals(gid)) {
				g.leave().queue();

				CorvusBuilder builder = Corvus.success(execution.getBot());

				builder.addBreadcrumbs(this.getCommandStructure().getName())
						.setDescription("Bot has left " + g.getName());

				Corvus.reply(execution, builder);

				break;
			}
		}

		CorvusBuilder builder = Corvus.error(execution.getBot());

		builder.addBreadcrumbs(this.getCommandStructure().getName())
				.setDescription("Could not find that guild!");

		Corvus.reply(execution, builder);
	}

	/*
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
		EmbedBuilder response = response(EmbedType.SUCCESS)
				.setDescription("Reports were gathered and cleared from servers!");
		executingCommand.replyFile(f, response);

		// Clear out guild feedback.
		for (Guild g : App.Shmames.getJDA().getGuilds()) {
			Brain b = App.Shmames.getStorageService().getBrain(g.getId());

			b.getFeedback().clear();
		}

		return null;
	}
	 */

	private void saveBrains(Execution execution) {
		for (Brain b : shmames.getBrainController().getBrains()) {
			shmames.getBrainController().saveBrain(b);
		}

		shmames.getBrainController().saveMotherBrain();

		App.getLogger().Write();

		CorvusBuilder builder = Corvus.success(execution.getBot());

		builder.addBreadcrumbs(this.getCommandStructure().getName())
				.setDescription("Brains and logs saved!");

		Corvus.reply(execution, builder);
	}
}
