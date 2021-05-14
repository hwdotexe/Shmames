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

	private final Pattern basePattern = Pattern.compile("^((create)|(add)|(remove)|(delete)|(random)|(list))(\\s([a-z_\\-0-9]+)(\\s(.+))?)?$", Pattern.CASE_INSENSITIVE);

	@Override
	public String getDescription() {
		return "Create and manage custom lists for all your things.";
	}

	@Override
	public String getUsage() {
		return "list <create|add|remove|delete|random|list> [name] [item]";
	}

	@Override
	public String run(String args, User author, Message message) {
		Matcher m = basePattern.matcher(args);

		if(m.find()) {
			String cmd = m.group(1).toLowerCase();
			String listName = m.group(9);
			String item = m.group(11);
			String userID = author.getId();

			switch(cmd){
				case "create":
					if(listName != null) {
						if(getList(userID, listName) == null) {
							UserCustomList newList = new UserCustomList(listName);
							brain.getUserLists(userID).add((newList));

							return lang.getMsg(Langs.LIST_CREATED);
						}else{
							return lang.getError(Errors.ALREADY_EXISTS, true);
						}
					}else{
						return lang.wrongUsage(getUsage());
					}
				case "add":
					if(listName != null && item != null) {
						UserCustomList existingList = getList(userID, listName);

						if(existingList != null) {
							existingList.getValues().add(item);

							return lang.getMsg(Langs.ITEM_ADDED);
						}else{
							return lang.getError(Errors.NOT_FOUND, true);
						}
					}else{
						return lang.wrongUsage(getUsage());
					}
				case "remove":
					if(listName != null && item != null && Utils.isInt(item)) {
						UserCustomList existingList = getList(userID, listName);
						int index = Integer.parseInt(item) - 1;

						if(existingList != null) {
							if(existingList.getValues().size() >= index) {
								existingList.getValues().remove(index);

								return lang.getMsg(Langs.ITEM_REMOVED);
							}else{
								return lang.getError(Errors.NOT_FOUND, true);
							}
						}else{
							return lang.getError(Errors.NOT_FOUND, true);
						}
					}else{
						return lang.wrongUsage(getUsage());
					}
				case "delete":
					if(listName != null) {
						UserCustomList existingList = getList(userID, listName);

						if(existingList != null) {
							brain.getUserLists(userID).remove((existingList));

							return lang.getMsg(Langs.LIST_DELETED, new String[] { existingList.getName() });
						}else{
							return lang.getError(Errors.NOT_FOUND, true);
						}
					}else{
						return lang.wrongUsage(getUsage());
					}
				case "random":
					if(listName != null) {
						UserCustomList existingList = getList(userID, listName);

						if(existingList != null) {
							return Utils.getRandomStringFromList(existingList.getValues());
						}else{
							return lang.getError(Errors.NOT_FOUND, true);
						}
					}else{
						return lang.wrongUsage(getUsage());
					}
				case "list":
					if(listName != null) {
						// List elements in the list
						UserCustomList existingList = getList(userID, listName);

						if(existingList != null) {
							String lists = Utils.generateList(existingList.getValues(), 1, true);

							message.getChannel().sendMessage(buildListEmbed(existingList.getName(), lists).build()).queue();
							return "";
						}else{
							return lang.getError(Errors.NOT_FOUND, true);
						}
					}else{
						// List lists
						List<String> listNames = new ArrayList<>();

						for(UserCustomList l : brain.getUserLists(userID)) {
							listNames.add(l.getName());
						}

						String lists = Utils.generateList(listNames, 1, false);

						message.getChannel().sendMessage(buildListEmbed("Your Lists", lists).build()).queue();
						return "";
					}
				default:
					return lang.getError(Errors.COMMAND_NOT_FOUND, true);
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
