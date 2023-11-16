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
import com.hadenwatne.fornax.command.types.ExecutionFailReason;
import com.hadenwatne.fornax.service.types.LogType;
import com.hadenwatne.fornax.utility.HTTPUtility;
import com.hadenwatne.fornax.utility.models.HTTPResponse;
import com.hadenwatne.shmames.Shmames;
import com.hadenwatne.shmames.language.ErrorKey;
import net.dv8tion.jda.api.Permission;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Wiki extends Command {
	private Shmames shmames;

	public Wiki(Shmames shmames) {
		super(false);
		this.shmames = shmames;
	}

	@Override
	protected Permission[] configureRequiredBotPermissions() {
		return new Permission[]{Permission.MESSAGE_SEND, Permission.MESSAGE_SEND_IN_THREADS, Permission.MESSAGE_EMBED_LINKS};
	}

	@Override
	protected Permission[] configureRequiredUserPermissions() {
		return new Permission[0];
	}

	@Override
	protected CommandStructure buildCommandStructure() {
		return CommandBuilder.Create("wiki", "Ask the oracle your question, and I shall answer. That, or the Internet will.")
				.addParameters(
						new CommandParameter("query", "A short search query", ParameterType.STRING)
								.setPattern(".{3,150}")
								.setExample("mass of Uranus")
				)
				.build();
	}

	@Override
	public void onCommandFailure(Execution execution) {
		CorvusBuilder builder = Corvus.error(execution.getBot());
		String errorMessage = "";

		if(execution.getFailureReason() == ExecutionFailReason.COMMAND_USAGE_INCORRECT) {
			errorMessage = execution.getLanguageProvider().getErrorFromKey(execution, ErrorKey.WRONG_USAGE.name());
		}

		builder.addBreadcrumbs(this.getCommandStructure().getName())
				.setDescription(errorMessage)
				.setEphemeral();

		Corvus.reply(execution, builder);
	}

	@Override
	public void run(Execution execution) {
		String query = execution.getArguments().get("query").getAsString();
		String result = getWolfram(query);
		CorvusBuilder builder;

		if (result == null) {
			result = execution.getLanguageProvider().getErrorFromKey(ErrorKey.ANSWER_NOT_FOUND.name());
			builder = Corvus.warning(execution.getBot());
		} else {
			builder = Corvus.info(execution.getBot());
		}

		builder.addBreadcrumbs(this.getCommandStructure().getName())
						.addField(query, result, false);

		Corvus.reply(execution, builder);
	}

	private String getWolfram(String query) {
		App.getLogger().Log(LogType.NETWORK, "[Wolfram Search: " + query + "]");

		query = URLEncoder.encode(query.trim(), StandardCharsets.UTF_8);
		String apiKey = shmames.getBrainController().getMotherBrain().getWolframAPIKey();
		HTTPResponse result = HTTPUtility.get("http://api.wolframalpha.com/v1/result?appid=" + apiKey + "&i=" + query);

		if (result.responseCode() == 200) {
			return result.response();
		}

		return null;
	}
}