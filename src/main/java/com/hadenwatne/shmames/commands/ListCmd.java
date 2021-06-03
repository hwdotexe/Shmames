package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.Shmames;
import com.hadenwatne.shmames.Utils;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.enums.Langs;
import com.hadenwatne.shmames.enums.UserListType;
import com.hadenwatne.shmames.models.Brain;
import com.hadenwatne.shmames.models.Lang;
import com.hadenwatne.shmames.models.UserCustomList;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ListCmd implements ICommand {
	private Lang lang;
	private Brain brain;

	private final Pattern basePattern = Pattern.compile("^((create)|(add)|(remove)|(delete)|(random)|(list)|([a-z_\\-0-9]+))(\\s(.+))?$", Pattern.CASE_INSENSITIVE);
	private final Pattern argsPattern = Pattern.compile("^([a-z_\\-0-9]+)(\\s(.+))?$", Pattern.CASE_INSENSITIVE);

	@Override
	public String getDescription() {
		return "Create and manage custom lists for all your things. Other server members can add to public lists, or you can keep it private for just yourself.";
	}

	@Override
	public String getUsage() {
		return "list <create|add|remove|delete|random|list|[name]> [name] [public|private] [item]";
	}

	@Override
	public String getExamples() {
		return "`list create myList public`\n" +
				"`list add myList Eggs`\n" +
				"`list remove myList 1`\n" +
				"`list delete myList`\n" +
				"`list random myList`\n" +
				"`list list`\n" +
				"`list myList`";
	}

	@Override
	public String run(String args, User author, Message message) {
		Matcher m = basePattern.matcher(args);

		if(m.find()) {
			String cmd = m.group(1).toLowerCase();
			String cmdArgs = m.group(10);
			String userID = author.getId();

			switch(cmd){
				case "create":
					if(cmdArgs != null) {
						Matcher a = argsPattern.matcher(cmdArgs);

						if(a.find() && a.group(3) != null){
							String listName = a.group(1).toLowerCase();
							UserListType listType = UserListType.parseOrPrivate(a.group(3));

							if(getList(userID, listName) == null) {
								UserCustomList newList = new UserCustomList(userID, listType, listName);
								brain.getUserLists().add((newList));

								return lang.getMsg(Langs.LIST_CREATED);
							}else{
								return lang.getError(Errors.ALREADY_EXISTS, true);
							}
						}
					}

					return lang.wrongUsage(getUsage());
				case "add":
					if(cmdArgs != null) {
						Matcher a = argsPattern.matcher(cmdArgs);

						if(a.find()){
							String listName = a.group(1).toLowerCase();
							String item = a.group(3);

							if(item != null) {
								UserCustomList existingList = getList(userID, listName);

								if(existingList != null) {
									existingList.getValues().add(item);

									return lang.getMsg(Langs.ITEM_ADDED);
								}else{
									return lang.getError(Errors.NOT_FOUND, true);
								}
							}
						}
					}

					return lang.wrongUsage(getUsage());
				case "remove":
					if(cmdArgs != null) {
						Matcher a = argsPattern.matcher(cmdArgs);

						if(a.find()){
							String listName = a.group(1).toLowerCase();
							String item = a.group(3);

							if(item != null && Utils.isInt(item)) {
								UserCustomList existingList = getList(userID, listName);
								int index = Integer.parseInt(item) - 1;

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
						}
					}

					return lang.wrongUsage(getUsage());
				case "delete":
					if(cmdArgs != null) {
						Matcher a = argsPattern.matcher(cmdArgs);

						if(a.find()){
							String listName = a.group(1).toLowerCase();
							UserCustomList existingList = getList(userID, listName);

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
					}

					return lang.wrongUsage(getUsage());
				case "random":
					if(cmdArgs != null) {
						Matcher a = argsPattern.matcher(cmdArgs);

						if(a.find()){
							String listName = a.group(1).toLowerCase();
							UserCustomList existingList = getList(userID, listName);

							if(existingList != null) {
								return Utils.getRandomStringFromList(existingList.getValues());
							}else{
								return lang.getError(Errors.NOT_FOUND, true);
							}
						}
					}

					return lang.wrongUsage(getUsage());
				case "list":
					// List lists
					HashMap<String, String> listNames = new HashMap<>();

					for(UserCustomList l : brain.getUserLists()) {
						if(l.getOwnerID().equals(userID)) {
							listNames.put(l.getName(), l.getType().name());
						}
					}

					String userLists = listNames.size() > 0
							?  Utils.generateList(listNames, 1, false) //Utils.generateList(listNames, 1, false, false)
							: "No lists found";

					message.getChannel().sendMessage(buildListEmbed("Your Lists", userLists).build()).queue();

					return "";
				default:
					// List elements in the list
					UserCustomList list = getList(userID, cmd.toLowerCase());

					if(list != null) {
						String listValues = list.getValues().size() > 0
								? Utils.generateList(list.getValues(), 1, true, false)
								: "No items found";

						message.getChannel().sendMessage(buildListEmbed(list.getName(), listValues).build()).queue();

						return "";
					}else{
						return lang.getError(Errors.NOT_FOUND, true);
					}
			}
		}else{
			return lang.wrongUsage(getUsage());
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"list"};
	}

	@Override
	public void setRunContext(Lang lang, @Nullable Brain brain) {
		this.lang = lang;
		this.brain = brain;
	}
	
	@Override
	public boolean requiresGuild() {
		return true;
	}

	private EmbedBuilder buildListEmbed(String title, String list) {
		EmbedBuilder eBuilder = new EmbedBuilder();

		eBuilder.setColor(new Color(219, 217, 157));
		eBuilder.setAuthor(Shmames.getBotName(), null, Shmames.getJDA().getSelfUser().getAvatarUrl());
		eBuilder.addField(title,list,false);

		return eBuilder;
	}

	private UserCustomList getList(String userID, String name) {
		UserCustomList list = null;

		for (UserCustomList l : brain.getUserLists()) {
			if(l.getName().equalsIgnoreCase(name)) {
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
