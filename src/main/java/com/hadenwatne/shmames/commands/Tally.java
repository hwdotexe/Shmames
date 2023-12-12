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
import com.hadenwatne.fornax.utility.FileUtility;
import com.hadenwatne.shmames.Shmames;
import com.hadenwatne.shmames.language.ErrorKey;
import com.hadenwatne.shmames.language.LanguageKey;
import com.hadenwatne.shmames.models.PaginatedList;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.services.DataService;
import com.hadenwatne.shmames.services.PaginationService;
import com.hadenwatne.shmames.services.settings.types.BotSettingName;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectInteraction;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction;
import net.dv8tion.jda.api.interactions.modals.ModalInteraction;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class Tally extends Command implements IInteractable {
	private Shmames shmames;

	public Tally(Shmames shmames) {
		super(true);

		this.shmames = shmames;
	}

	@Override
	protected Permission[] configureRequiredBotPermissions() {
		return new Permission[]{Permission.MESSAGE_SEND, Permission.MESSAGE_SEND_IN_THREADS, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ATTACH_FILES};
	}

	@Override
	protected Permission[] configureRequiredUserPermissions() {
		return null;
	}

	@Override
	protected CommandStructure buildCommandStructure() {
		return CommandBuilder.Create("tally", "Manage server tallies.")
				.addSubCommands(
						CommandBuilder.Create("add", "Increment a tally or create a new one.")
								.addParameters(
										new CommandParameter("tallyname", "The tally to adjust.", ParameterType.STRING)
												.setPattern("[\\w\\d\\s]{3,}")
												.setExample("myTally")
								)
								.build(),
						CommandBuilder.Create("drop", "Decrement a tally or delete it if 0.")
								.addParameters(
										new CommandParameter("tallyname", "The tally to adjust.", ParameterType.STRING)
												.setPattern("[\\w\\d\\s]{3,}")
												.setExample("myTally")
								)
								.build(),
						CommandBuilder.Create("set", "Overwrite the value of a tally.")
								.addParameters(
										new CommandParameter("tallyname", "The tally to adjust.", ParameterType.STRING)
												.setPattern("[\\w\\d\\s]{3,}")
												.setExample("myTally"),
										new CommandParameter("count", "The new count for this tally.", ParameterType.INTEGER)
												.setExample("3")
								)
								.build(),
						CommandBuilder.Create("list", "Display all of the current tallies.")
								.build(),
						CommandBuilder.Create("reset", "Export current tallies and clear them out.")
								.build()
				)
				.build();
	}

	@Override
	public void onCommandFailure(Execution execution) {

	}

	@Override
	public void run(Execution execution) {
		String subCommand = execution.getSubCommand();
		Brain brain = shmames.getBrainController().getBrain(execution.getServer().getId());

		switch (subCommand) {
			case "add":
				cmdAdd(execution, brain);
				break;
			case "drop":
				cmdDrop(execution, brain);
				break;
			case "set":
				cmdSet(execution, brain);
				break;
			case "list":
				cmdList(execution, brain);
				break;
			case "reset":
				cmdReset(execution, brain);
				break;
		}
	}

	private void cmdAdd(Execution execution, Brain brain) {
		String rawTally = execution.getArguments().get("tallyname").getAsString();
		String tally = formatTally(rawTally);
		int newTally = brain.getTallies().getOrDefault(tally, 0) + 1;

		brain.getTallies().put(tally, newTally);

		String response = execution.getLanguageProvider().getMessageFromKey(execution, LanguageKey.TALLY_CURRENT_VALUE.name(), tally, Integer.toString(newTally));

		CorvusBuilder builder = Corvus.success(execution.getBot());

		builder.addBreadcrumbs(this.getCommandStructure().getName())
				.setDescription(response);

		Corvus.reply(execution, builder);
	}

	private void cmdDrop(Execution execution, Brain brain) {
		String rawTally = execution.getArguments().get("tallyname").getAsString();
		String tally = formatTally(rawTally);
		int newTally = brain.getTallies().getOrDefault(tally, 0) - 1;

		if (newTally == -1) {
			String response = execution.getLanguageProvider().getErrorFromKey(execution, ErrorKey.TALLY_NOT_FOUND.name());
			CorvusBuilder builder = Corvus.error(execution.getBot());

			builder.addBreadcrumbs(this.getCommandStructure().getName())
					.setDescription(response);

			Corvus.reply(execution, builder);
		} else if (newTally == 0) {
			brain.getTallies().remove(tally);

			String response = execution.getLanguageProvider().getMessageFromKey(execution, LanguageKey.TALLY_REMOVED.name(), tally);
			CorvusBuilder builder = Corvus.success(execution.getBot());

			builder.addBreadcrumbs(this.getCommandStructure().getName())
					.setDescription(response);

			Corvus.reply(execution, builder);
		} else {
			brain.getTallies().put(tally, newTally);

			String response = execution.getLanguageProvider().getMessageFromKey(execution, LanguageKey.TALLY_CURRENT_VALUE.name(), tally, Integer.toString(newTally));

			CorvusBuilder builder = Corvus.info(execution.getBot());

			builder.addBreadcrumbs(this.getCommandStructure().getName())
					.setDescription(response);

			Corvus.reply(execution, builder);
		}
	}

	private void cmdSet(Execution execution, Brain brain) {
		String rawTally = execution.getArguments().get("tallyname").getAsString();
		int count = execution.getArguments().get("count").getAsInt();
		String tally = formatTally(rawTally);

		if (count > 0) {
			brain.getTallies().put(tally, count);

			String response = execution.getLanguageProvider().getMessageFromKey(execution, LanguageKey.TALLY_CURRENT_VALUE.name(), tally, Integer.toString(count));

			CorvusBuilder builder = Corvus.info(execution.getBot());

			builder.addBreadcrumbs(this.getCommandStructure().getName())
					.setDescription(response);

			Corvus.reply(execution, builder);
		} else {
			brain.getTallies().remove(tally);

			String response = execution.getLanguageProvider().getMessageFromKey(execution, LanguageKey.TALLY_REMOVED.name(), tally);
			CorvusBuilder builder = Corvus.success(execution.getBot());

			builder.addBreadcrumbs(this.getCommandStructure().getName())
					.setDescription(response);

			Corvus.reply(execution, builder);
		}
	}

	private void cmdList(Execution execution, Brain brain) {
		final String cacheKey = shmames.getCacheService().GenerateCacheKey(execution.getServer().getIdLong(), execution.getChannel().getIdLong(), execution.getUser().getIdLong(), "tally-list");
		//final PaginatedList cachedList = shmames.getCacheService().RetrieveItem(cacheKey, PaginatedList.class);

		LinkedHashMap<String, Integer> tSorted = DataService.SortHashMap(brain.getTallies());
		List<String> talliesFormatted = formatTalliesToStringList(tSorted);

		PaginatedList paginatedList = PaginationService.GetPaginatedList(talliesFormatted, 15, -1, false);

		shmames.getCacheService().StoreItem(cacheKey, paginatedList);

		CorvusBuilder builder = PaginationService.DrawEmbedPage(paginatedList, 1, shmames, execution.getLanguageProvider());

		if (paginatedList.getPaginatedList().size() > 1) {
			Button next = Button.secondary("nextpage", "Next Page");
			Button prev = Button.secondary("prevpage", "Previous Page").asDisabled();

			builder.addLayoutComponent(ActionRow.of(prev, next));
		}

		Corvus.reply(execution, builder);
	}

	private void cmdReset(Execution execution, Brain brain) {
		Guild server = execution.getServer();

		if (shmames.checkPermission(server, brain.getSettingFor(BotSettingName.RESET_TALLIES), execution.getMember())) {
			HashMap<String, Integer> tallies = brain.getTallies();
			File file = buildTalliesList(server.getName(), tallies);
			CorvusBuilder builder = Corvus.success(execution.getBot());

			builder.addBreadcrumbs(this.getCommandStructure().getName())
					.setDescription(execution.getLanguageProvider().getMessageFromKey(execution, LanguageKey.TALLIES_CLEARED.name(), Integer.toString(tallies.size())));

			try {
				builder.attach(file.toURI().toURL(), "tallies");
			} catch (Exception e){
				App.getLogger().LogException(e);
			}

			Corvus.reply(execution, builder);

			tallies.clear();
		} else {
			CorvusBuilder builder = Corvus.error(execution.getBot());

			builder.setDescription(execution.getLanguageProvider().getErrorFromKey(execution, ErrorKey.MISSING_USER_PERMISSION.name()));

			Corvus.reply(execution, builder);
		}
	}

	private List<String> formatTalliesToStringList(LinkedHashMap<String, Integer> tallies) {
		List<String> tallyList = new ArrayList<>();

		for(String key : tallies.keySet()) {
			tallyList.add(key + ": **" + tallies.get(key) + "**");
		}

		return tallyList;
	}

	private String formatTally(String rawTally) {
		return rawTally.trim().replaceAll("\\s", "_").replaceAll("\\W", "").toLowerCase();
	}

	private File buildTalliesList(String guildName, HashMap<String, Integer> tallies) {
		StringBuilder pruned = new StringBuilder("Pruned Tallies\n");

		pruned.append("=======================\n");
		pruned.append("= Count:\t\tName =\n");
		pruned.append("=======================\n");

		// Build list.
		for(String tally : tallies.keySet()) {
			pruned.append("\n");
			pruned.append(tallies.get(tally));
			pruned.append(":\t");
			pruned.append(tally);
		}

		// Save to file.
		return FileUtility.WriteBytesToFile("reports", guildName+".txt", pruned.toString().getBytes());
	}

	@Override
	public String[] getInteractionIDs() {
		return new String[]{"nextpage", "prevpage"};
	}

	@Override
	public void onButtonClick(ButtonInteraction buttonInteraction) {
		final String cacheKey = shmames.getCacheService().GenerateCacheKey(buttonInteraction.getGuild().getIdLong(), buttonInteraction.getChannel().getIdLong(), buttonInteraction.getUser().getIdLong(), "tally-list");
		final PaginatedList cachedList = shmames.getCacheService().RetrieveItem(cacheKey, PaginatedList.class);

		if (cachedList != null) {
			int page = 0;

			if (buttonInteraction.getComponentId().equalsIgnoreCase("nextpage")) {
				page = cachedList.getNextPage();
			} else if (buttonInteraction.getComponentId().equalsIgnoreCase("prevpage")) {
				page = cachedList.getLastPage();
			}

			CorvusBuilder builder = PaginationService.DrawEmbedPage(cachedList, page, shmames, shmames.getLanguageProvider());

			Button next = Button.secondary("nextpage", "Next Page");
			Button prev = Button.secondary("prevpage", "Previous Page");

			if (page > 1) {
				prev = prev.asEnabled();
			} else if (page == 1) {
				prev = prev.asDisabled();
			}

			if (page == cachedList.getPaginatedList().size()) {
				next = next.asDisabled();
			} else {
				next = next.asEnabled();
			}

			builder.addLayoutComponent(ActionRow.of(prev, next));
			shmames.getCacheService().StoreItem(cacheKey, cachedList);

			buttonInteraction.editMessageEmbeds(Corvus.convert(builder).build())
					.setComponents(builder.getLayoutComponents()).queue();
		} else {
			Brain brain = shmames.getBrainController().getBrain(buttonInteraction.getGuild().getId());
			LinkedHashMap<String, Integer> tSorted = DataService.SortHashMap(brain.getTallies());
			List<String> talliesFormatted = formatTalliesToStringList(tSorted);

			if (!talliesFormatted.isEmpty()) {
				PaginatedList paginatedList = PaginationService.GetPaginatedList(talliesFormatted, 15, -1, false);

				shmames.getCacheService().StoreItem(cacheKey, paginatedList);

				CorvusBuilder builder = PaginationService.DrawEmbedPage(paginatedList, 1, shmames, shmames.getLanguageProvider());

				if (paginatedList.getPaginatedList().size() > 1) {
					Button next = Button.secondary("nextpage", "Next Page");
					Button prev = Button.secondary("prevpage", "Previous Page").asDisabled();

					builder.addLayoutComponent(ActionRow.of(prev, next));
				}

				buttonInteraction.editMessageEmbeds(Corvus.convert(builder).build())
						.setComponents(builder.getLayoutComponents()).queue();
			} else {
				CorvusBuilder builder = Corvus.error(shmames);

				builder.setDescription(shmames.getLanguageProvider().getErrorFromKey(ErrorKey.GENERIC_ERROR.name()));

				buttonInteraction.editMessageEmbeds(Corvus.convert(builder).build())
						.setComponents(builder.getLayoutComponents()).queue();
			}
		}
	}

	@Override
	public void onStringClick(StringSelectInteraction stringSelectInteraction) {

	}

	@Override
	public void onEntityClick(EntitySelectInteraction entitySelectInteraction) {

	}

	@Override
	public void onModalSubmit(ModalInteraction modalInteraction) {

	}
}
