package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.Shmames;
import com.hadenwatne.shmames.Utils;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.enums.Langs;
import com.hadenwatne.shmames.models.Brain;
import com.hadenwatne.shmames.models.Lang;
import com.hadenwatne.shmames.models.UserCustomList;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ListCmd implements ICommand {
	private Lang lang;
	private Brain brain;

	private final Pattern basePattern = Pattern.compile("^((create)|(add)|(remove)|(delete)|(random)|(list)|([a-z_\\-0-9]+))(\\s(.+))?$", Pattern.CASE_INSENSITIVE);
	private final Pattern argsPattern = Pattern.compile("^([a-z_\\-0-9]+)(\\s(.+))?$", Pattern.CASE_INSENSITIVE);

	@Override
	public String getDescription() {
		return "Create and manage custom lists for all your things.";
	}

	@Override
	public String getUsage() {
		return "list <create|add|remove|delete|random|list|[name]> [name] [item]";
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

						if(a.find()){
							String listName = a.group(1).toLowerCase();

							if(getList(userID, listName) == null) {
								UserCustomList newList = new UserCustomList(listName);
								brain.getUserLists(userID).add((newList));

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
									if(existingList.getValues().size() > index) {
										String removed = existingList.getValues().get(index);
										existingList.getValues().remove(index);

										return lang.getMsg(Langs.ITEM_REMOVED, new String[]{removed});
									}else{
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
								brain.getUserLists(userID).remove((existingList));

								return lang.getMsg(Langs.LIST_DELETED, new String[] { existingList.getName() });
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
					List<String> listNames = new ArrayList<>();

					for(UserCustomList l : brain.getUserLists(userID)) {
						listNames.add(l.getName());
					}

					String userLists = listNames.size() > 0
							? Utils.generateList(listNames, 1, false, false)
							: "No lists found";

					message.getChannel().sendMessage(buildListEmbed("Your Lists", userLists).build()).queue();

					return "";
				default:
					// List elements in the list
					UserCustomList existingList = getList(userID, cmd.toLowerCase());

					if(existingList != null) {
						String listValues = existingList.getValues().size() > 0
								? Utils.generateList(existingList.getValues(), 1, true, false)
								: "No items found";

						message.getChannel().sendMessage(buildListEmbed(existingList.getName(), listValues).build()).queue();

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
		List<UserCustomList> userLists = brain.getUserLists(userID);

		if(userLists != null) {
			for (UserCustomList list : userLists) {
				if(list.getName().equals(name)) {
					return list;
				}
			}
		}

		return null;
	}
}