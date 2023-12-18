package com.hadenwatne.shmames.commands;

import com.hadenwatne.corvus.Corvus;
import com.hadenwatne.corvus.CorvusBuilder;
import com.hadenwatne.fornax.App;
import com.hadenwatne.fornax.command.Command;
import com.hadenwatne.fornax.command.Execution;
import com.hadenwatne.fornax.command.IInteractable;
import com.hadenwatne.fornax.command.builder.CommandBuilder;
import com.hadenwatne.fornax.command.builder.CommandParameter;
import com.hadenwatne.fornax.command.builder.CommandStructure;
import com.hadenwatne.fornax.command.builder.types.ParameterType;
import com.hadenwatne.fornax.service.ILanguageProvider;
import com.hadenwatne.shmames.Shmames;
import com.hadenwatne.shmames.language.ErrorKey;
import com.hadenwatne.shmames.models.PollModel;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.services.DataService;
import com.hadenwatne.shmames.services.PollService;
import com.hadenwatne.shmames.services.settings.types.BotSettingName;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectInteraction;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction;
import net.dv8tion.jda.api.interactions.modals.ModalInteraction;

import java.util.ArrayList;
import java.util.List;

public class Poll extends Command implements IInteractable {
	private Shmames shmames;

	public Poll(Shmames shmames) {
		super(true);

		this.shmames = shmames;
	}

	@Override
	protected Permission[] configureRequiredBotPermissions() {
		return new Permission[]{Permission.MESSAGE_SEND, Permission.MESSAGE_SEND_IN_THREADS, Permission.MESSAGE_EMBED_LINKS};
	}

	@Override
	protected Permission[] configureRequiredUserPermissions() {
		return null;
	}

	@Override
	protected CommandStructure buildCommandStructure() {
		return CommandBuilder.Create("poll", "Create server polls.")
				.addParameters(
						new CommandParameter("time", "The amount of time the poll should last.", ParameterType.TIMECODE)
								.setExample("24h"),
						new CommandParameter("question", "The question or topic of the poll.", ParameterType.STRING)
								.setExample("Thoughts?"),
						new CommandParameter("options", "The poll's options, separated by ';'", ParameterType.STRING)
								.setPattern("(.+;)+(.+);?")
								.setExample("Yes; No; Maybe"),
						new CommandParameter("multiple", "Allow voters to select more than 1?", ParameterType.BOOLEAN)
								.setExample("True")
				)
				.build();
	}

	@Override
	public void onCommandFailure(Execution execution) {

	}

	@Override
	public void run(Execution execution) {
		Guild server = execution.getServer();
		Brain brain = shmames.getBrainController().getBrain(server.getId());
		ILanguageProvider language = execution.getLanguageProvider();

		if (shmames.checkPermission(server, brain.getSettingFor(BotSettingName.POLL_CREATE), execution.getMember())) {
			String time = execution.getArguments().get("time").getAsString();
			String question = execution.getArguments().get("question").getAsString();
			String options = execution.getArguments().get("options").getAsString();
			boolean multiple = execution.getArguments().get("multiple").getAsBoolean();
			int seconds = DataService.ConvertTimeStringToSeconds(time);

			if (seconds > 0) {
				List<String> optionsList = new ArrayList<>();

				for (String s : options.split(";")) {
					optionsList.add(s.trim());
				}

				if (optionsList.size() > 1 && optionsList.size() <= 25) {
					PollModel poll = new PollModel(execution.getUser().getId(), question, optionsList, seconds, multiple);
					CorvusBuilder builder = PollService.BuildPoll(shmames, poll);

					// TODO begin a task to close the poll
					// TODO be able to retrieve/update poll message even after restart
					// ^ The corvus builder will know its message id, so let's use that

					poll.setChannelID(execution.getChannel().getId());
					builder.setSuccessCallback(success -> success.retrieveOriginal().queue(message -> poll.setMessageID(message.getId())));
					brain.getActivePolls().add(poll);

					Corvus.reply(execution, builder);
				} else {
					CorvusBuilder builder = Corvus.error(execution.getBot());

					builder.setDescription(execution.getLanguageProvider().getErrorFromKey(execution, ErrorKey.POLL_ITEM_COUNT_INCORRECT.name()));
					builder.setEphemeral();

					Corvus.reply(execution, builder);
				}
			} else {
				CorvusBuilder builder = Corvus.error(execution.getBot());

				builder.setDescription(execution.getLanguageProvider().getErrorFromKey(execution, ErrorKey.TIME_VALUE_INCORRECT.name()));
				builder.setEphemeral();

				Corvus.reply(execution, builder);
			}
		} else {
			CorvusBuilder builder = Corvus.error(execution.getBot());

			builder.setDescription(execution.getLanguageProvider().getErrorFromKey(execution, ErrorKey.MISSING_USER_PERMISSION.name()));
			builder.setEphemeral();

			Corvus.reply(execution, builder);
		}
	}

	@Override
	public String[] getInteractionIDs() {
		return new String[]{"pollDropdown"};
	}

	@Override
	public void onButtonClick(ButtonInteraction buttonInteraction) {

	}

	@Override
	public void onStringClick(StringSelectInteraction stringSelectInteraction) {
		if (stringSelectInteraction.getComponentId().equalsIgnoreCase("pollDropdown")) {
			Brain brain = shmames.getBrainController().getBrain(stringSelectInteraction.getGuild().getId());

			for (PollModel model : brain.getActivePolls()) {
				if (model.getChannelID().equalsIgnoreCase(stringSelectInteraction.getChannelId()) && model.getMessageID().equalsIgnoreCase(stringSelectInteraction.getMessageId())) {
					if (model.isActive()) {
						List<SelectOption> options = stringSelectInteraction.getSelectedOptions();
						List<Integer> optionMap = new ArrayList<>();

						for (SelectOption option : options) {
							try {
								int optionIndex = Integer.parseInt(option.getValue());

								optionMap.add(optionIndex);
							} catch (Exception e) {
								App.getLogger().LogException(e);
							}
						}

						PollService.AddVote(model, stringSelectInteraction.getUser(), optionMap);

						CorvusBuilder builder = PollService.BuildPoll(shmames, model);

						stringSelectInteraction.editMessageEmbeds(Corvus.convert(builder).build()).queue();
					}

					break;
				}
			}
		}
	}

	@Override
	public void onEntityClick(EntitySelectInteraction entitySelectInteraction) {

	}

	@Override
	public void onModalSubmit(ModalInteraction modalInteraction) {

	}
}