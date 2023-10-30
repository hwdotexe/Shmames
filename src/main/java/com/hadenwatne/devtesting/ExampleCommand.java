package com.hadenwatne.devtesting;

import com.hadenwatne.corvus.Corvus;
import com.hadenwatne.corvus.CorvusBuilder;
import com.hadenwatne.fornax.command.Command;
import com.hadenwatne.fornax.command.Execution;
import com.hadenwatne.fornax.command.builder.CommandBuilder;
import com.hadenwatne.fornax.command.builder.CommandParameter;
import com.hadenwatne.fornax.command.builder.CommandStructure;
import com.hadenwatne.fornax.command.builder.types.ParameterType;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectInteraction;

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
	protected CommandStructure buildCommandStructure() {
		return CommandBuilder.Create("test", "Just a test command")
				.addParameters(
						new CommandParameter("item", "A test param", ParameterType.STRING)
								.setExample("potato")
				)
				.build();
	}

	@Override
	public void onCommandFailure(Execution execution) {
		CorvusBuilder builder = Corvus.error(execution.getBot());

		builder.setEphemeral()
				.addBreadcrumbs("error")
				.setDescription("That didn't work :c");

		Corvus.reply(execution, builder);
	}

	@Override
	public void run(Execution execution) {
		String answer = execution.getLanguageProvider().getMessageFromKey("test");
		String question = execution.getArguments().get("item").getAsString();

		Button button = Button.primary(this.createButtonID("test"), "Primary");
		CorvusBuilder builder = Corvus.info(execution.getBot());

		builder.addBreadcrumbs("example")
						.addField(question, answer, false)
						.addLayoutComponent(ActionRow.of(button));

		Corvus.reply(execution, builder);
	}

	@Override
	public void onInteraction(String elementID, EntitySelectInteraction interaction) {
		switch(elementID) {
			case "test":
				interaction.reply("Nice").queue();
				break;
			default:
				interaction.reply("Nah").queue();
		}
	}
}