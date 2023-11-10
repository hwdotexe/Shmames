package com.hadenwatne.shmames.commands;

import com.hadenwatne.corvus.Corvus;
import com.hadenwatne.corvus.CorvusBuilder;
import com.hadenwatne.fornax.command.Command;
import com.hadenwatne.fornax.command.Execution;
import com.hadenwatne.fornax.command.builder.CommandBuilder;
import com.hadenwatne.fornax.command.builder.CommandParameter;
import com.hadenwatne.fornax.command.builder.CommandStructure;
import com.hadenwatne.fornax.command.builder.types.ParameterType;
import com.hadenwatne.fornax.command.types.ExecutionFailReason;
import com.hadenwatne.shmames.language.ErrorKey;
import com.hadenwatne.shmames.language.LanguageKey;
import net.dv8tion.jda.api.Permission;

public class Say extends Command {
	public Say() {
		super(false);
	}

	@Override
	protected Permission[] configureRequiredBotPermissions() {
		return new Permission[]{Permission.MESSAGE_SEND, Permission.MESSAGE_SEND_IN_THREADS, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_EXT_EMOJI};
	}

	@Override
	protected Permission[] configureRequiredUserPermissions() {
		return null;
	}


	@Override
	protected CommandStructure buildCommandStructure() {
		return CommandBuilder.Create("say", "I'll repeat after you! Send messages, links, or server emotes!")
				.addParameters(
						new CommandParameter("message", "The message you want me to repeat.", ParameterType.STRING)
								.setExample("Am I kawaii??")
				)
				.build();
	}

	@Override
	public void onCommandFailure(Execution execution) {
		CorvusBuilder builder = Corvus.error(execution.getBot());
		String errorMessage;

		if(execution.getFailureReason() == ExecutionFailReason.BOT_MISSING_PERMISSION) {
			errorMessage = execution.getLanguageProvider().getErrorFromKey(execution, ErrorKey.MISSING_BOT_PERMISSION.name(), execution.getFailureReasonDetails());
		} else {
			errorMessage = execution.getLanguageProvider().getErrorFromKey(execution, ErrorKey.GENERIC_ERROR.name());
		}

		builder.addBreadcrumbs(this.getCommandStructure().getName())
				.setDescription(errorMessage)
				.setEphemeral();

		Corvus.reply(execution, builder);
	}

	@Override
	public void run(Execution execution) {
		String message = execution.getArguments().get("message").getAsString();
		String success = execution.getLanguageProvider().getMessageFromKey(execution, LanguageKey.GENERIC_SUCCESS.name());

		CorvusBuilder builder = Corvus.success(execution.getBot());

		builder.addBreadcrumbs(this.getCommandStructure().getName())
				.setDescription(success)
				.setEphemeral();

		Corvus.reply(execution, builder);
		execution.getChannel().sendMessage(message).queue();
	}
}
