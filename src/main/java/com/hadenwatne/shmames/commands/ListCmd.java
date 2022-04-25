package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.enums.Langs;
import com.hadenwatne.shmames.enums.UserListType;
import com.hadenwatne.shmames.factories.EmbedFactory;
import com.hadenwatne.shmames.models.PaginatedList;
import com.hadenwatne.shmames.models.UserCustomList;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import com.hadenwatne.shmames.models.command.ExecutingCommandArguments;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import com.hadenwatne.shmames.services.PaginationService;
import com.hadenwatne.shmames.services.RandomService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListCmd extends Command {
	public ListCmd() {
		super(true);
	}

	@Override
	protected CommandStructure buildCommandStructure() {
		return CommandBuilder.Create("list", "Create and manage custom lists for all your things.")
				.addSubCommands(
						CommandBuilder.Create("create", "Create a new list.")
								.addParameters(
										new CommandParameter("listName", "The name of the list to create.", ParameterType.STRING)
												.setPattern("[\\w\\d-_]+")
												.setExample("myList"),
										new CommandParameter("privacy", "Whether others can view and add to the list.", ParameterType.SELECTION, false)
												.addSelectionOptions("public", "private")
												.setExample("public")
								)
								.build(),
						CommandBuilder.Create("add", "Add an item to a list.")
								.addParameters(
										new CommandParameter("listName", "The name of the list to add to.", ParameterType.STRING)
												.setPattern("[\\w\\d-_]+")
												.setExample("myList"),
										new CommandParameter("item", "The item to add to the list.", ParameterType.STRING)
												.setExample("newItem")
								)
								.build(),
						CommandBuilder.Create("remove", "Remove an item from a list.")
								.addParameters(
										new CommandParameter("listName", "The name of the list to remove from.", ParameterType.STRING)
												.setPattern("[\\w\\d-_]+")
												.setExample("myList"),
										new CommandParameter("index", "The item position to remove.", ParameterType.INTEGER)
												.setExample("3")
								)
								.build(),
						CommandBuilder.Create("delete", "Delete a list.")
								.addParameters(
										new CommandParameter("listName", "The name of the list to delete.", ParameterType.STRING)
												.setPattern("[\\w\\d-_]+")
												.setExample("myList")
								)
								.build(),
						CommandBuilder.Create("toggle", "Toggle a List between Public and Private.")
								.addParameters(
										new CommandParameter("listName", "The name of the list to toggle.", ParameterType.STRING)
												.setPattern("[\\w\\d-_]+")
												.setExample("myList")
								)
								.build(),
						CommandBuilder.Create("random", "Get a random item from a list.")
								.addParameters(
										new CommandParameter("listName", "The name of the list to get a random item from.", ParameterType.STRING)
												.setPattern("[\\w\\d-_]+")
												.setExample("myList")
								)
								.build(),
						CommandBuilder.Create("list", "Show all available lists.")
								.build(),
						CommandBuilder.Create("view", "View the items in a list.")
								.addParameters(
										new CommandParameter("listName", "The name of the list to view.", ParameterType.STRING)
												.setPattern("[\\w\\d-_]+")
												.setExample("myList"),
										new CommandParameter("page", "The name of the list to view.", ParameterType.INTEGER, false)
												.setExample("2")
								)
								.build()
				)
				.build();
	}

	@Override
	public EmbedBuilder run(ExecutingCommand executingCommand) {
		String subCommand = executingCommand.getSubCommand();
		Lang lang = executingCommand.getLanguage();
		Brain brain = executingCommand.getBrain();
		String authorID = executingCommand.getAuthorUser().getId();

		switch (subCommand) {
			case "create":
				return cmdCreate(brain, lang, authorID, executingCommand.getCommandArguments());
			case "add":
				return cmdAdd(brain, lang, authorID, executingCommand.getCommandArguments());
			case "remove":
				return cmdRemove(brain, lang, authorID, executingCommand.getCommandArguments());
			case "delete":
				return cmdDelete(brain, lang, authorID, executingCommand.getCommandArguments());
			case "toggle":
				return cmdToggle(brain, lang, authorID, executingCommand.getCommandArguments());
			case "random":
				return cmdRandom(brain, lang, authorID, executingCommand.getCommandArguments());
			case "list":
				return cmdList(brain, authorID);
			case "view":
				return cmdView(brain, lang, authorID, executingCommand.getCommandArguments());
		}

		return null;
	}

	private EmbedBuilder cmdCreate(Brain brain, Lang lang, String userID, ExecutingCommandArguments subCmdArgs) {
		String listName = subCmdArgs.getAsString("listName").toLowerCase();
		String privacy = subCmdArgs.getAsString("privacy");
		UserListType listType = privacy != null ? UserListType.parseOrPrivate(privacy) : UserListType.PRIVATE;

		if (getList(userID, listName, brain) == null) {
			UserCustomList newList = new UserCustomList(userID, listType, listName);
			brain.getUserLists().add((newList));

			return response(EmbedType.SUCCESS)
					.setDescription(lang.getMsg(Langs.LIST_CREATED));
		} else {
			return response(EmbedType.ERROR)
					.setDescription(lang.getError(Errors.ALREADY_EXISTS));
		}
	}

	private EmbedBuilder cmdAdd(Brain brain, Lang lang, String userID, ExecutingCommandArguments subCmdArgs) {
		String listName = subCmdArgs.getAsString("listName").toLowerCase();
		String item = subCmdArgs.getAsString("item");
		UserCustomList existingList = getList(userID, listName, brain);

		if (existingList != null) {
			existingList.getValues().add(item);

			return response(EmbedType.SUCCESS)
					.setDescription(lang.getMsg(Langs.ITEM_ADDED));
		} else {
			return response(EmbedType.ERROR)
					.setDescription(lang.getError(Errors.NOT_FOUND));
		}
	}

	private EmbedBuilder cmdRemove(Brain brain, Lang lang, String userID, ExecutingCommandArguments subCmdArgs) {
		String listName = subCmdArgs.getAsString("listName").toLowerCase();
		int index = subCmdArgs.getAsInteger("index") - 1;
		UserCustomList existingList = getList(userID, listName, brain);

		if (existingList != null && index >= 0) {
			if (existingList.getValues().size() > index) {
				String removed = existingList.getValues().get(index);
				existingList.getValues().remove(index);

				return response(EmbedType.SUCCESS)
						.setDescription(lang.getMsg(Langs.ITEM_REMOVED, new String[]{removed}));
			} else {
				return response(EmbedType.ERROR)
						.setDescription(lang.getError(Errors.NOT_FOUND));
			}
		} else {
			return response(EmbedType.ERROR)
					.setDescription(lang.getError(Errors.NOT_FOUND));
		}
	}

	private EmbedBuilder cmdDelete(Brain brain, Lang lang, String userID, ExecutingCommandArguments subCmdArgs) {
		String listName = subCmdArgs.getAsString("listName").toLowerCase();
		UserCustomList existingList = getList(userID, listName, brain);

		if (existingList != null) {
			if (existingList.getOwnerID().equals(userID)) {
				brain.getUserLists().remove((existingList));

				return response(EmbedType.SUCCESS)
						.setDescription(lang.getMsg(Langs.LIST_DELETED, new String[]{existingList.getName()}));
			} else {
				return response(EmbedType.ERROR)
						.setDescription(lang.getError(Errors.NO_PERMISSION_USER));
			}
		} else {
			return response(EmbedType.ERROR)
					.setDescription(lang.getError(Errors.NOT_FOUND));
		}
	}

	private EmbedBuilder cmdToggle(Brain brain, Lang lang, String userID, ExecutingCommandArguments subCmdArgs) {
		String listName = subCmdArgs.getAsString("listName").toLowerCase();
		UserCustomList existingList = getList(userID, listName, brain);

		if (existingList != null) {
			if (existingList.getOwnerID().equals(userID)) {
				if (existingList.getType() == UserListType.PUBLIC) {
					existingList.setType(UserListType.PRIVATE);
				} else {
					existingList.setType(UserListType.PUBLIC);
				}

				return response(EmbedType.SUCCESS)
						.setDescription(lang.getMsg(Langs.LIST_PRIVACY_TOGGLED, new String[]{existingList.getName(), existingList.getType().toString()}));
			} else {
				return response(EmbedType.ERROR)
						.setDescription(lang.getError(Errors.NO_PERMISSION_USER));
			}
		} else {
			return response(EmbedType.ERROR)
					.setDescription(lang.getError(Errors.NOT_FOUND));
		}
	}

	private EmbedBuilder cmdRandom(Brain brain, Lang lang, String userID, ExecutingCommandArguments subCmdArgs) {
		String listName = subCmdArgs.getAsString("listName").toLowerCase();
		UserCustomList existingList = getList(userID, listName, brain);

		if (existingList != null) {
			String randomItem = RandomService.GetRandomObjectFromList(existingList.getValues());
			int randomItemIndex = existingList.getValues().indexOf(randomItem) + 1;
			String randomitemDisplay = "#" + randomItemIndex + ": " + randomItem;
			List<String> userList = PaginationService.SplitString(randomitemDisplay, MessageEmbed.VALUE_MAX_LENGTH);
			String embedFieldTitle = listName;
			EmbedBuilder response = response(EmbedType.INFO);

			for (String listSegment : userList) {
				response.addField(embedFieldTitle, listSegment, false);
				embedFieldTitle = "";
			}

			return response;
		} else {
			return response(EmbedType.ERROR)
					.setDescription(lang.getError(Errors.NOT_FOUND));
		}
	}

	private EmbedBuilder cmdList(Brain brain, String userID) {
		HashMap<String, String> userLists = new HashMap<>();
		List<String> publicLists = new ArrayList<>();

		for (UserCustomList l : brain.getUserLists()) {
			if (l.getOwnerID().equals(userID)) {
				userLists.put(l.getName(), l.getType().name());
			}

			if (l.getType() == UserListType.PUBLIC) {
				publicLists.add(l.getName());
			}
		}

		String userListsFormatted = userLists.size() > 0
				? PaginationService.GenerateList(userLists, 1, false)
				: "No lists found";

		String publicListsFormatted = publicLists.size() > 0
				? PaginationService.GenerateList(publicLists, -1, false, false)
				: "No lists found";

		EmbedBuilder eBuilder = EmbedFactory.GetEmbed(EmbedType.INFO, "list");

		eBuilder.addField("Your Lists", userListsFormatted, false);
		eBuilder.addField("Public Lists", publicListsFormatted, false);

		return eBuilder;
	}

	private EmbedBuilder cmdView(Brain brain, Lang lang, String userID, ExecutingCommandArguments subCmdArgs) {
		String listName = subCmdArgs.getAsString("listName").toLowerCase();
		int page = subCmdArgs.getAsInteger("page");
		UserCustomList list = getList(userID, listName, brain);

		if (list != null) {
			List<String> listItems = list.getValues();

			if (listItems.size() == 0) {
				return response(EmbedType.ERROR)
						.setDescription(lang.getError(Errors.ITEMS_NOT_FOUND));
			}

			PaginatedList paginatedList = PaginationService.GetPaginatedList(listItems, 10, -1, true);

			return PaginationService.DrawEmbedPage(paginatedList, Math.max(1, page), listName, Color.ORANGE, lang);
		} else {
			return response(EmbedType.ERROR)
					.setDescription(lang.getError(Errors.NOT_FOUND));
		}
	}

	private UserCustomList getList(String userID, String listName, Brain brain) {
		UserCustomList list = null;

		for (UserCustomList l : brain.getUserLists()) {
			if (l.getName().equalsIgnoreCase(listName)) {
				list = l;
				break;
			}
		}

		if (list == null) {
			return null;
		}

		if (list.getType() == UserListType.PUBLIC || list.getOwnerID().equals(userID)) {
			return list;
		}

		return null;
	}
}