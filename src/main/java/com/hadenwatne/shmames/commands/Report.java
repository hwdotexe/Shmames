package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.enums.LanguageKeys;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.tasks.ReportCooldownTask;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;

public class Report extends Command {
	public Report() {
		super(true);
	}

	@Override
	protected Permission[] configureRequiredBotPermissions() {
		return new Permission[]{Permission.MESSAGE_SEND, Permission.MESSAGE_SEND_IN_THREADS, Permission.MESSAGE_EMBED_LINKS};
	}

	@Override
	protected CommandStructure buildCommandStructure() {
		return CommandBuilder.Create("report", "Send feedback to the bot developer")
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
	public EmbedBuilder run (ExecutingCommand executingCommand) {
		Brain brain = executingCommand.getBrain();

		if (!brain.getReportCooldown()) {
			String reportType = executingCommand.getCommandArguments().getAsString("reportType");
			String reportText = executingCommand.getCommandArguments().getAsString("reportText");

			brain.getFeedback().add(executingCommand.getAuthorUser().getName() + " (" + executingCommand.getServer().getName() + "): [" + reportType.toUpperCase() + "] " + reportText);

			// Start a cooldown
			new ReportCooldownTask(brain);

			return response(EmbedType.SUCCESS)
					.setDescription(executingCommand.getLanguage().getMsg(LanguageKeys.FEEDBACK_SENT));
		} else {
			return response(EmbedType.ERROR)
					.setDescription(executingCommand.getLanguage().getMsg(LanguageKeys.FEEDBACK_COOLDOWN));
		}
	}
}
