package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.Shmames;
import com.hadenwatne.shmames.Utils;
import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.enums.Langs;
import com.hadenwatne.shmames.enums.UserListType;
import com.hadenwatne.shmames.models.UserCustomList;
import com.hadenwatne.shmames.models.command.ShmamesCommandArguments;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import com.hadenwatne.shmames.models.command.ShmamesCommandMessagingChannel;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListCmd implements ICommand {
	private final CommandStructure commandStructure;

	public ListCmd() {
		this.commandStructure = CommandBuilder.Create("list")
				.addSubCommands(
						CommandBuilder.Create("create")
								.addParameters(
										new CommandParameter("newListName", "The name of the list to create.", ParameterType.STRING)
												.setPattern("[\\w\\d-_]+"),
										new CommandParameter("privacy", "Whether others can view and add to the list.", ParameterType.SELECTION, false)
												.addSelectionOptions("public", "private")
								)
								.build(),
						CommandBuilder.Create("add")
								.addParameters(
										new CommandParameter("addListName", "The name of the list to add to.", ParameterType.STRING)
												.setPattern("[\\w\\d-_]+"),
										new CommandParameter("item", "The item to add to the list.", ParameterType.STRING)
								)
								.build(),
						CommandBuilder.Create("remove")
								.addParameters(
										new CommandParameter("removeListName", "The name of the list to remove from.", ParameterType.STRING)
												.setPattern("[\\w\\d-_]+"),
										new CommandParameter("index", "The item position to remove.", ParameterType.INTEGER)
								)
								.build(),
						CommandBuilder.Create("delete")
								.addParameters(
										new CommandParameter("deleteListName", "The name of the list to delete.", ParameterType.STRING)
												.setPattern("[\\w\\d-_]+")
										)
								.build(),
						CommandBuilder.Create("random")
								.addParameters(
										new CommandParameter("randomListName", "The name of the list to get a random item from.", ParameterType.STRING)
												.setPattern("[\\w\\d-_]+")
										)
								.build(),
						CommandBuilder.Create("list")
								.build(),
						CommandBuilder.Create("view")
								.addParameters(
										new CommandParameter("viewListName", "The name of the list to view.", ParameterType.STRING)
												.setPattern("[\\w\\d-_]+")
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
	public String getDescription() {
		return "Create and manage custom lists for all your things. Other server members can add to public lists, or you can keep it private for just yourself.";
	}

	@Override
	public String getUsage() {
		return this.commandStructure.getUsage();
	}

	@Override
	public String getExamples() {
		return "`list create myList public`\n" +
				"`list add myList Eggs`\n" +
				"`list remove myList 1`\n" +
				"`list delete myList`\n" +
				"`list random myList`\n" +
				"`list list`\n" +
				"`list view myList`";
	}

	@Override
	public String run(Lang lang, Brain brain, ShmamesCommandData data) {
		String subCmd = data.getSubCommand().getCommandName();
		ShmamesCommandArguments subCmdArgs = data.getSubCommand().getArguments();

		switch(subCmd.toLowerCase()) {
			case "create":
				return cmdCreate(brain, lang, data.getAuthor().getId(), subCmdArgs);
			case "add":
				return cmdAdd(brain, lang, data.getAuthor().getId(), subCmdArgs);
			case "remove":
				return cmdRemove(brain, lang, data.getAuthor().getId(), subCmdArgs);
			case "delete":
				return cmdDelete(brain, lang, data.getAuthor().getId(), subCmdArgs);
			case "random":
				return cmdRandom(brain, lang, data.getAuthor().getId(), subCmdArgs);
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
		String listName = subCmdArgs.getAsString("newListName").toLowerCase();
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
		String listName = subCmdArgs.getAsString("addListName").toLowerCase();
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
		String listName = subCmdArgs.getAsString("removeListName").toLowerCase();
		int index = subCmdArgs.getAsInteger("index");
		UserCustomList existingList = getList(userID, listName, brain);

		if(existingList != null && index >= 0) {
			if (existingList.getValues().size() > index) {
				String removed = existingList.getValues().get(index);
				existingList.getValues().remove(index);

				return lang.getMsg(Langs.ITEM_REMOVED, new String[]{removed});
			} else {
				return lang.getError(Errors.NOT_FOUND, true);
			}
		}else{
			return lang.getError(Errors.NOT_FOUND, true);
		}
	}

	private String cmdDelete(Brain brain, Lang lang, String userID, ShmamesCommandArguments subCmdArgs) {
		String listName = subCmdArgs.getAsString("deleteListName").toLowerCase();
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

	private String cmdRandom(Brain brain, Lang lang, String userID, ShmamesCommandArguments subCmdArgs) {
		String listName = subCmdArgs.getAsString("randomListName").toLowerCase();
		UserCustomList existingList = getList(userID, listName, brain);

		if(existingList != null) {
			return Utils.getRandomStringFromList(existingList.getValues());
		}else{
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
				?  Utils.generateList(userLists, 1, false)
				: "No lists found";

		String publicListsFormatted = publicLists.size() > 0
				? Utils.generateList(publicLists, -1, false, false)
				: "No lists found";

		return buildListEmbed(userListsFormatted, publicListsFormatted);
	}

	private String cmdView(Brain brain, Lang lang, String userID, ShmamesCommandArguments subCmdArgs, ShmamesCommandMessagingChannel messagingChannel) {
		String listName = subCmdArgs.getAsString("viewListName").toLowerCase();
		UserCustomList list = getList(userID, listName, brain);

		if(list != null) {
			String listValues = list.getValues().size() > 0
					? Utils.generateList(list.getValues(), 1, true, false)
					: "No items found";

			EmbedBuilder eBuilder = buildViewEmbed(list.getName(), listValues);

			messagingChannel.sendMessage(eBuilder);

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

		eBuilder.addField(listName,listString,false);

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
