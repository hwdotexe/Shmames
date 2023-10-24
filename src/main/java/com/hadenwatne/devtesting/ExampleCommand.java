package com.hadenwatne.devtesting;

import com.hadenwatne.botcore.command.Command;
import com.hadenwatne.botcore.command.Execution;
import com.hadenwatne.botcore.command.builder.CommandBuilder;
import com.hadenwatne.botcore.command.builder.CommandParameter;
import com.hadenwatne.botcore.command.builder.CommandStructure;
import com.hadenwatne.botcore.command.builder.types.ParameterType;
import com.hadenwatne.shmames.enums.EmbedType;
import net.dv8tion.jda.api.Permission;

public class ExampleCommand extends Command {
	public ExampleCommand() {
		super(false, false, true, false);
	}

	@Override
	protected Permission[] configureRequiredBotPermissions() {
		return new Permission[]{Permission.MESSAGE_SEND, Permission.MESSAGE_SEND_IN_THREADS, Permission.MESSAGE_EMBED_LINKS};
	}

	@Override
	protected Permission[] configureEnabledUserPermissions() {
		return new Permission[]{Permission.MESSAGE_SEND};
	}

	@Override
	public void onCommandFailure(Execution execution) {
		execution.reply(response(EmbedType.ERROR).setDescription("Didn't work :("));
	}

	@Override
	protected CommandStructure buildCommandStructure() {
		return CommandBuilder.Create("test", "Just a test command")
				.addParameters(
						new CommandParameter("item", "A test param", ParameterType.STRING)
								.setExample("potato")
				)
				.build();
	}

	@Override
	public void run(Execution execution) {
		String answer = execution.getLanguageProvider().getMessageFromKey("test");
		String question = execution.getArguments().get("item").getAsString();

		execution.reply(response(EmbedType.INFO).addField(question, answer, false));
	}
}