package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.Shmames;
import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.enums.Langs;
import com.hadenwatne.shmames.enums.UserListType;
import com.hadenwatne.shmames.models.PaginatedList;
import com.hadenwatne.shmames.models.UserCustomList;
import com.hadenwatne.shmames.models.command.ShmamesCommandArguments;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import com.hadenwatne.shmames.models.command.ShmamesCommandMessagingChannel;
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

public class ListCmd implements ICommand {
	private final CommandStructure commandStructure;

	public ListCmd() {
		this.commandStructure = CommandBuilder.Create("list", "Create and manage custom lists for all your things.")
				.addSubCommands(
						CommandBuilder.Create("create", "Create a new list.")
								.addParameters(
										new CommandParameter("listName", "The name of the list to create.", ParameterType.STRING)
												.setPattern("[\\w\\d-_]+"),
										new CommandParameter("privacy", "Whether others can view and add to the list.", ParameterType.SELECTION, false)
												.addSelectionOptions("public", "private")
								)
								.build(),
						CommandBuilder.Create("add", "Add an item to a list.")
								.addParameters(
										new CommandParameter("listName", "The name of the list to add to.", ParameterType.STRING)
												.setPattern("[\\w\\d-_]+"),
										new CommandParameter("item", "The item to add to the list.", ParameterType.STRING)
								)
								.build(),
						CommandBuilder.Create("remove", "Remove an item from a list.")
								.addParameters(
										new CommandParameter("listName", "The name of the list to remove from.", ParameterType.STRING)
												.setPattern("[\\w\\d-_]+"),
										new CommandParameter("index", "The item position to remove.", ParameterType.INTEGER)
								)
								.build(),
						CommandBuilder.Create("delete", "Delete a list.")
								.addParameters(
										new CommandParameter("listName", "The name of the list to delete.", ParameterType.STRING)
												.setPattern("[\\w\\d-_]+")
										)
								.build(),
						CommandBuilder.Create("toggle", "Toggle a List between Public and Private.")
								.addParameters(
										new CommandParameter("listName", "The name of the list to toggle.", ParameterType.STRING)
												.setPattern("[\\w\\d-_]+")
								)
								.build(),
						CommandBuilder.Create("random", "Get a random item from a list.")
								.addParameters(
										new CommandParameter("listName", "The name of the list to get a random item from.", ParameterType.STRING)
												.setPattern("[\\w\\d-_]+")
										)
								.build(),
						CommandBuilder.Create("list", "Show all available lists.")
								.build(),
						CommandBuilder.Create("view", "View the items in a list.")
								.addParameters(
										new CommandParameter("listName", "The name of the list to view.", ParameterType.STRING)
												.setPattern("[\\w\\d-_]+"),
										new CommandParameter("page", "The name of the list to view.", ParameterType.INTEGER, false)
										)
								.build()
				)
				.build();
	}

	@Override
	public CommandStructure getCommandStructure() {
		return this.commandStructure;
	}

	@Override
	public String getExamples() {
		return "`list create myList public`\n" +
				"`list add myList Eggs`\n" +
				"`list remove myList 1`\n" +
				"`list delete myList`\n" +
				"`list toggle myList`\n" +
				"`list random myList`\n" +
				"`list list`\n" +
				"`list view myList`";
	}

	@Override
	public String run(Lang lang, Brain brain, ShmamesCommandData data) {
		String subCmd = data.getSubCommandData().getCommandName();
		ShmamesCommandArguments subCmdArgs = data.getSubCommandData().getArguments();

		switch(subCmd.toLowerCase()) {
			case "create":
				return cmdCreate(brain, lang, data.getAuthor().getId(), subCmdArgs);
			case "add":
				return cmdAdd(brain, lang, data.getAuthor().getId(), subCmdArgs);
			case "remove":
				return cmdRemove(brain, lang, data.getAuthor().getId(), subCmdArgs);
			case "delete":
				return cmdDelete(brain, lang, data.getAuthor().getId(), subCmdArgs);
			case "toggle":
				return cmdToggle(brain, lang, data.getAuthor().getId(), subCmdArgs);
			case "random":
				return cmdRandom(brain, lang, data.getAuthor().getId(), subCmdArgs, data.getMessagingChannel());
			case "list":
				EmbedBuilder eBuilder = cmdList(brain, data.getAuthor().getId());

				data.getMessagingChannel().sendMessage(eBuilder);

				return "";
			case "view":
				return cmdView(brain, lang, data.getAuthor().getId(), subCmdArgs, data.getMessagingChannel());
			default:
				return "";
		}
	}
	
	@Override
	public boolean requiresGuild() {
		return true;
	}

	private String cmdCreate(Brain brain, Lang lang, String userID, ShmamesCommandArguments subCmdArgs) {
		String listName = subCmdArgs.getAsString("listName").toLowerCase();
		String privacy = subCmdArgs.getAsString("privacy");
		UserListType listType = privacy != null ? UserListType.parseOrPrivate(privacy) : UserListType.PRIVATE;

		if(getList(userID, listName, brain) == null) {
			UserCustomList newList = new UserCustomList(userID, listType, listName);
			brain.getUserLists().add((newList));

			return lang.getMsg(Langs.LIST_CREATED);
		}else{
			return lang.getError(Errors.ALREADY_EXISTS, true);
		}
	}

	private String cmdAdd(Brain brain, Lang lang, String userID, ShmamesCommandArguments subCmdArgs) {
		String listName = subCmdArgs.getAsString("listName").toLowerCase();
		String item = subCmdArgs.getAsString("item");
		UserCustomList existingList = getList(userID, listName, brain);

		if(existingList != null) {
			existingList.getValues().add(item);

			return lang.getMsg(Langs.ITEM_ADDED);
		}else{
			return lang.getError(Errors.NOT_FOUND, true);
		}
	}

	private String cmdRemove(Brain brain, Lang lang, String userID, ShmamesCommandArguments subCmdArgs) {
		String listName = subCmdArgs.getAsString("listName").toLowerCase();
		int index = subCmdArgs.getAsInteger("index") - 1;
		UserCustomList existingList = getList(userID, listName, brain);

		if (existingList != null && index >= 0) {
			if (existingList.getValues().size() > index) {
				String removed = existingList.getValues().get(index);
				existingList.getValues().remove(index);

				return lang.getMsg(Langs.ITEM_REMOVED, new String[]{removed});
			} else {
				return lang.getError(Errors.NOT_FOUND, true);
			}
		} else {
			return lang.getError(Errors.NOT_FOUND, true);
		}
	}

	private String cmdDelete(Brain brain, Lang lang, String userID, ShmamesCommandArguments subCmdArgs) {
		String listName = subCmdArgs.getAsString("listName").toLowerCase();
		UserCustomList existingList = getList(userID, listName, brain);

		if(existingList != null) {
			if(existingList.getOwnerID().equals(userID)) {
				brain.getUserLists().remove((existingList));

				return lang.getMsg(Langs.LIST_DELETED, new String[]{existingList.getName()});
			}else{
				return lang.getError(Errors.NO_PERMISSION_USER, true);
			}
		}else{
			return lang.getError(Errors.NOT_FOUND, true);
		}
	}

	private String cmdToggle(Brain brain, Lang lang, String userID, ShmamesCommandArguments subCmdArgs) {
		String listName = subCmdArgs.getAsString("listName").toLowerCase();
		UserCustomList existingList = getList(userID, listName, brain);

		if (existingList != null) {
			if (existingList.getOwnerID().equals(userID)) {
				if (existingList.getType() == UserListType.PUBLIC) {
					existingList.setType(UserListType.PRIVATE);
				} else {
					existingList.setType(UserListType.PUBLIC);
				}

				return lang.getMsg(Langs.LIST_PRIVACY_TOGGLED, new String[]{existingList.getName(), existingList.getType().toString()});
			} else {
				return lang.getError(Errors.NO_PERMISSION_USER, true);
			}
		} else {
			return lang.getError(Errors.NOT_FOUND, true);
		}
	}

	private String cmdRandom(Brain brain, Lang lang, String userID, ShmamesCommandArguments subCmdArgs, ShmamesCommandMessagingChannel messagingChannel) {
		String listName = subCmdArgs.getAsString("listName").toLowerCase();
		UserCustomList existingList = getList(userID, listName, brain);

		if (existingList != null) {
			String randomItem = RandomService.GetRandomStringFromList(existingList.getValues());
			int randomItemIndex = existingList.getValues().indexOf(randomItem) + 1;


			EmbedBuilder eBuilder = buildViewEmbed(existingList.getName(), "#" + randomItemIndex + ": " + randomItem);

			messagingChannel.sendMessage(eBuilder);

			return "";
		} else {
			return lang.getError(Errors.NOT_FOUND, true);
		}
	}

	private EmbedBuilder cmdList(Brain brain, String userID) {
		HashMap<String, String> userLists = new HashMap<>();
		List<String> publicLists = new ArrayList<>();

		for(UserCustomList l : brain.getUserLists()) {
			if(l.getOwnerID().equals(userID)) {
				userLists.put(l.getName(), l.getType().name());
			}

			if(l.getType() == UserListType.PUBLIC) {
				publicLists.add(l.getName());
			}
		}

		String userListsFormatted = userLists.size() > 0
				?  PaginationService.GenerateList(userLists, 1, false)
				: "No lists found";

		String publicListsFormatted = publicLists.size() > 0
				? PaginationService.GenerateList(publicLists, -1, false, false)
				: "No lists found";

		return buildListEmbed(userListsFormatted, publicListsFormatted);
	}

	private String cmdView(Brain brain, Lang lang, String userID, ShmamesCommandArguments subCmdArgs, ShmamesCommandMessagingChannel messagingChannel) {
		String listName = subCmdArgs.getAsString("listName").toLowerCase();
		int page = subCmdArgs.getAsInteger("page");
		UserCustomList list = getList(userID, listName, brain);

		if(list != null) {
			List<String> listItems = list.getValues();

			if(listItems.size() == 0) {
				return lang.getError(Errors.ITEMS_NOT_FOUND, true);
			}

			PaginatedList paginatedList = PaginationService.GetPaginatedList(listItems, 10, -1, true);

			messagingChannel.sendMessage(PaginationService.DrawEmbedPage(paginatedList, Math.max(1, page), listName, Color.YELLOW, lang));

			return "";
		}else{
			return lang.getError(Errors.NOT_FOUND, true);
		}
	}

	private EmbedBuilder buildListEmbed(String userLists, String publicLists) {
		EmbedBuilder eBuilder = new EmbedBuilder();

		eBuilder.setColor(new Color(219, 217, 157));
		eBuilder.setAuthor(Shmames.getBotName(), null, Shmames.getJDA().getSelfUser().getAvatarUrl());

		eBuilder.addField("Your Lists",userLists,false);
		eBuilder.addField("Public Lists",publicLists,false);

		return eBuilder;
	}

	private EmbedBuilder buildViewEmbed(String listName, String listString) {
		EmbedBuilder eBuilder = new EmbedBuilder();

		eBuilder.setColor(new Color(219, 217, 157));
		eBuilder.setAuthor(Shmames.getBotName(), null, Shmames.getJDA().getSelfUser().getAvatarUrl());

		List<String> userList = PaginationService.SplitString(listString, MessageEmbed.VALUE_MAX_LENGTH);
		String embedFieldTitle = listName;

		for(String listSegment : userList) {
			eBuilder.addField(embedFieldTitle,listSegment,false);
			embedFieldTitle = "";
		}

		return eBuilder;
	}

	private UserCustomList getList(String userID, String listName, Brain brain) {
		UserCustomList list = null;

		for (UserCustomList l : brain.getUserLists()) {
			if(l.getName().equalsIgnoreCase(listName)) {
				list = l;
				break;
			}
		}

		if(list == null) {
			return null;
		}

		if(list.getType() == UserListType.PUBLIC || list.getOwnerID().equals(userID)) {
			return list;
		}

		return null;
	}
}
