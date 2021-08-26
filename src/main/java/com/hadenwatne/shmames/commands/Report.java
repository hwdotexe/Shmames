package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.enums.Langs;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import com.hadenwatne.shmames.Shmames;
import com.hadenwatne.shmames.tasks.ReportCooldownTask;

import javax.annotation.Nullable;

public class Report implements ICommand {
	private final CommandStructure commandStructure;

	public Report() {
		this.commandStructure = CommandBuilder.Create("report")
				.addAlias("feedback")
				.addParameters(
						new CommandParameter("reportType", "The type of report you are sending.", ParameterType.SELECTION)
								.addSelectionOptions("Bug", "Feature", "Suggestion", "Enhancement"),
						new CommandParameter("reportText", "The detailed report to send.", ParameterType.STRING)
				)
				.build();
	}

	@Override
	public CommandStructure getCommandStructure() {
		return this.commandStructure;
	}

	@Override
	public String getDescription() {
		return "Send feedback about " + Shmames.getBotName() + " to the developer. Your username, server's name, and message will be recorded.";
	}

	public String getUsage() {
		return this.commandStructure.getUsage();
	}

	@Override
	public String getExamples() {
		return "`report bug Shmames isn't funny`\n" +
				"`report feature Shmames rolls some dice`";
	}

	@Override
	public String run(Lang lang, Brain brain, ShmamesCommandData data) {
		if (!brain.getReportCooldown()) {
			String reportType = data.getArguments().getAsString("reportType");
			String reportText = data.getArguments().getAsString("reportText");

			brain.getFeedback().add(data.getAuthor().getName() + " (" + data.getServer().getName() + "): [" + reportType.toUpperCase() + "] " + reportText);

			// Start a cooldown
			new ReportCooldownTask(brain);

			return lang.getMsg(Langs.FEEDBACK_SENT);
		} else {
			return lang.getMsg(Langs.FEEDBACK_COOLDOWN);
		}
	}

	@Override
	public boolean requiresGuild() {
		return true;
	}
}
