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
		this.commandStructure = CommandBuilder.Create("report", "Send feedback to the bot developer")
				.addAlias("feedback")
				.addParameters(
						new CommandParameter("reportType", "The type of report you are sending.", ParameterType.SELECTION)
								.addSelectionOptions("Bug", "Feature", "Suggestion", "Enhancement")
								.setExample("bug"),
						new CommandParameter("reportText", "The detailed report to send.", ParameterType.STRING)
								.setExample("the bot smells weird")
				)
				.build();
	}

	@Override
	public CommandStructure getCommandStructure() {
		return this.commandStructure;
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
