package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.App;
import com.hadenwatne.shmames.commandbuilder.*;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.enums.Langs;
import com.hadenwatne.shmames.enums.RegexPatterns;
import com.hadenwatne.shmames.models.Family;
import com.hadenwatne.shmames.models.command.ShmamesCommandArguments;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import com.hadenwatne.shmames.models.command.ShmamesCommandMessagingChannel;
import com.hadenwatne.shmames.models.command.ShmamesSubCommandData;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import com.hadenwatne.shmames.services.PaginationService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import com.hadenwatne.shmames.Shmames;
import com.hadenwatne.shmames.services.ShmamesService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FamilyCmd implements ICommand {
	private final CommandStructure commandStructure;

	public FamilyCmd() {
		this.commandStructure = CommandBuilder.Create("family", "Create and manage server families.")
				.addSubCommands(
						CommandBuilder.Create("create", "Create a new family.")
								.addParameters(
										new CommandParameter("familyName", "The amount of time the poll should last.", ParameterType.STRING)
												.setPattern(RegexPatterns.ALPHANUMERIC.getPattern())
												.setExample("coolFam")
								)
								.build(),
						CommandBuilder.Create("leave", "Leave a Family.")
								.addParameters(
										new CommandParameter("familyName", "The Family to leave.", ParameterType.STRING)
												.setPattern(RegexPatterns.ALPHANUMERIC.getPattern())
												.setExample("coolFam")
								)
								.build(),
						CommandBuilder.Create("kick", "Kick another server from a Family.")
								.addParameters(
										new CommandParameter("familyName", "The Family to leave.", ParameterType.STRING)
												.setPattern(RegexPatterns.ALPHANUMERIC.getPattern())
												.setExample("coolFam"),
										new CommandParameter("serverNumber", "The server to kick.", ParameterType.INTEGER)
												.setExample("3")
								)
								.build()
				)
				.addSubCommandGroups(
						new SubCommandGroup("code", "Create or redeem Join Codes.")
								.addSubCommands(
										CommandBuilder.Create("generate", "Create a new Join Code.")
												.addParameters(
														new CommandParameter("familyName", "The Family to create a Join Code for.", ParameterType.STRING)
																.setPattern(RegexPatterns.ALPHANUMERIC.getPattern())
																.setExample("coolFam")
												)
												.build(),
										CommandBuilder.Create("redeem", "Redeem a Join Code to join a Family.")
												.addParameters(
														new CommandParameter("joinCode", "The Join Code to use.", ParameterType.STRING)
																.setExample("ABC123")
												)
												.build()
								),
						new SubCommandGroup("view", "View Family information.")
								.addSubCommands(
										CommandBuilder.Create("servers", "View a list of servers in a Family.")
												.addParameters(
														new CommandParameter("familyName", "The Family to view.", ParameterType.STRING)
																.setPattern(RegexPatterns.ALPHANUMERIC.getPattern())
																.setExample("coolFam")
												)
												.build(),
										CommandBuilder.Create("emotes", "View a list of Family emotes the server has access to.")
												.build(),
										CommandBuilder.Create("families", "View a list of Families this server belongs to.")
												.build()
								)
				)
				.build();
	}

	@Override
	public CommandStructure getCommandStructure() {
		return this.commandStructure;
	}

	@Override
	public String run(Lang lang, Brain brain, ShmamesCommandData data) {
		ShmamesSubCommandData subCommand = data.getSubCommandData();
		Guild server = data.getServer();
		Member author = server.getMember(data.getAuthor());
		String nameOrGroup = subCommand.getNameOrGroup();

		switch(nameOrGroup) {
			case "create":
				return cmdCreate(lang, brain, server, author, subCommand.getArguments());
			case "leave":
				return cmdLeave(lang, brain, server, author, subCommand.getArguments());
			case "kick":
				return cmdKick(lang, brain, author, subCommand.getArguments());
			case "code":
				return cmdCode(lang, brain, server, author, subCommand);
			case "view":
				return cmdView(lang, brain, server, author, subCommand, data.getMessagingChannel());
			default:
				return lang.wrongUsage(commandStructure.getUsage());
		}
	}
	
	@Override
	public boolean requiresGuild() {
		return true;
	}

	private String cmdCreate(Lang lang, Brain brain, Guild server, Member author, ShmamesCommandArguments args) {
		String familyName = args.getAsString("familyName").toLowerCase();

		if (author.hasPermission(Permission.ADMINISTRATOR) || App.IsDebug) {
			for (Family f : App.Shmames.getStorageService().getMotherBrain().getServerFamilies()) {
				if (f.getFamilyOwner() == author.getIdLong()) {
					if (f.getFamName().equals(familyName)) {
						return lang.getError(Errors.FAMILY_ALREADY_EXISTS, true);
					}
				}
			}

			Family newFam = new Family(UUID.randomUUID().toString(), familyName, author.getIdLong());

			// Tell the family it contains this server.
			newFam.addToFamily(server.getIdLong());

			// Add the Family to the system.
			App.Shmames.getStorageService().getMotherBrain().getServerFamilies().add(newFam);
			brain.getFamilies().add(newFam.getFamID());

			return lang.getMsg(Langs.FAMILY_CREATED);
		} else {
			return lang.getError(Errors.NO_PERMISSION_USER, true);
		}
	}

	private String cmdLeave(Lang lang, Brain brain, Guild server, Member author, ShmamesCommandArguments args) {
		String familyName = args.getAsString("familyName").toLowerCase();

		if (author.hasPermission(Permission.ADMINISTRATOR) || App.IsDebug) {
			for (Family f : App.Shmames.getStorageService().getMotherBrain().getServerFamilies()) {
				if (f.getFamName().equals(familyName)) {
					// Remove this server from the Family.
					brain.getFamilies().remove(f.getFamID());
					f.getMemberGuilds().remove(server.getIdLong());

					// Delete the Family if empty.
					if (f.getMemberGuilds().size() == 0) {
						App.Shmames.getStorageService().getMotherBrain().getServerFamilies().remove(f);
					}

					return lang.getMsg(Langs.FAMILY_REMOVED_SERVER, new String[]{server.getName(), f.getFamName()});
				}
			}

			return lang.getError(Errors.NOT_FOUND, true);
		} else {
			return lang.getError(Errors.NO_PERMISSION_USER, true);
		}
	}

	private String cmdKick(Lang lang, Brain brain, Member author, ShmamesCommandArguments args) {
		String familyName = args.getAsString("familyName").toLowerCase();
		int serverIndex = args.getAsInteger("serverNumber")-1;

		for (Family f : App.Shmames.getStorageService().getMotherBrain().getServerFamilies()) {
			if (f.getFamilyOwner() == author.getIdLong() && f.getFamName().equals(familyName)) {
				if (f.getMemberGuilds().size() >= serverIndex) {
					long guildID = f.getMemberGuilds().get(serverIndex);
					Guild g = App.Shmames.getJDA().getGuildById(guildID);
					String gName = "";

					// If the Guild is empty but was found in the list, remove it from the Family Guild list.
					if(g == null){
						f.getMemberGuilds().remove(guildID);
						gName = "that server";
					}else{
						brain.getFamilies().remove(f.getFamID());
						f.getMemberGuilds().remove(g.getIdLong());
						gName = g.getName();
					}

					// Remove the family if empty.
					if(f.getMemberGuilds().size() == 0){
						App.Shmames.getStorageService().getMotherBrain().getServerFamilies().remove(f);
					}

					return lang.getMsg(Langs.FAMILY_REMOVED_SERVER, new String[]{ gName, f.getFamName() });
				} else {
					return lang.getError(Errors.FAMILY_NOT_JOINED, true);
				}
			}
		}

		return lang.getError(Errors.NOT_FOUND, true);
	}

	private String cmdCode(Lang lang, Brain brain, Guild server, Member author, ShmamesSubCommandData commandData) {
		ShmamesCommandArguments args = commandData.getArguments();
		String subCommand = commandData.getCommandName();

		switch (subCommand.toLowerCase()) {
			case "create":
				return cmdCodeCreate(lang, author.getUser(), args);
			case "redeem":
				return cmdCodeRedeem(lang, brain, server, author, args);
			default:
				return lang.wrongUsage(commandStructure.getUsage());
		}
	}

	private String cmdCodeCreate(Lang lang, User author, ShmamesCommandArguments args) {
		String familyName = args.getAsString("familyName").toLowerCase();

		for(Family f : App.Shmames.getStorageService().getMotherBrain().getServerFamilies()) {
			if (f.getFamilyOwner() == author.getIdLong() && f.getFamName().equalsIgnoreCase(familyName)) {
				if (f.getMemberGuilds().size() < 7) {
					author.openPrivateChannel().queue((c) -> c.sendMessage(lang.getMsg(Langs.FAMILY_JOIN_CODE, new String[]{f.getFamName(), f.getNewJoinCode()})).queue());

					return lang.getMsg(Langs.SENT_PRIVATE_MESSAGE);
				} else {
					return lang.getError(Errors.FAMILY_MEMBER_MAXIMUM_REACHED, true);
				}
			}
		}

		return lang.getError(Errors.NOT_FOUND, true);
	}

	private String cmdCodeRedeem(Lang lang, Brain brain, Guild server, Member author, ShmamesCommandArguments args) {
		String joinCode = args.getAsString("joinCode").toLowerCase();

		for(Family f : App.Shmames.getStorageService().getMotherBrain().getServerFamilies()){
			if(f.validateCode(joinCode)){
				f.clearCode();

				if(author.hasPermission(Permission.ADMINISTRATOR) || App.IsDebug) {
					if(brain.getFamilies().size() < 3) {
						if (!brain.getFamilies().contains(f.getFamID())) {
							f.addToFamily(server.getIdLong());
							brain.getFamilies().add(f.getFamID());

							return lang.getMsg(Langs.FAMILY_JOINED, new String[]{ server.getName(), f.getFamName() });
						} else {
							return Errors.FAMILY_ALREADY_JOINED+"\n"+lang.getMsg(Langs.FAMILY_JOIN_CODE_INVALIDATED);
						}
					}else{
						return Errors.FAMILY_MAXIMUM_REACHED+"\n"+lang.getMsg(Langs.FAMILY_JOIN_CODE_INVALIDATED);
					}
				}else{
					return Errors.NO_PERMISSION_USER+"\n"+lang.getMsg(Langs.FAMILY_JOIN_CODE_INVALIDATED);
				}
			}
		}

		return lang.getError(Errors.FAMILY_INVALID_DETAIL, true);
	}

	private String cmdView(Lang lang, Brain brain, Guild server, Member author, ShmamesSubCommandData commandData, ShmamesCommandMessagingChannel messagingChannel) {
		ShmamesCommandArguments args = commandData.getArguments();
		String subCommand = commandData.getCommandName();

		switch (subCommand.toLowerCase()) {
			case "servers":
				return cmdViewServers(lang, brain, author, args);
			case "emotes":
				return cmdViewEmotes(brain, server, messagingChannel);
			case "families":
				return cmdViewFamilies(lang, brain, author);
			default:
				return lang.wrongUsage(commandStructure.getUsage());
		}
	}

	private String cmdViewServers(Lang lang, Brain brain, Member author, ShmamesCommandArguments args) {
		String familyName = args.getAsString("familyName").toLowerCase();

		for (Family f : App.Shmames.getStorageService().getMotherBrain().getServerFamilies()) {
			// View the family if the user is the family owner, or if they are an Admin and this server is a member
			if ((f.getFamilyOwner() == author.getIdLong() || (brain.getFamilies().contains(f.getFamID()) && author.hasPermission(Permission.ADMINISTRATOR))) && f.getFamName().equalsIgnoreCase(familyName)) {
				StringBuilder sb = new StringBuilder();

				sb.append("**");
				sb.append(lang.getMsg(Langs.FAMILY_SERVER_LIST, new String[]{f.getFamName()}));
				sb.append("**");
				sb.append("\n");

				boolean contains = false;
				List<String> memberGuilds = new ArrayList<String>();

				for (long g : new ArrayList<>(f.getMemberGuilds())) {
					Guild guild = App.Shmames.getJDA().getGuildById(g);

					// Quick null check!
					if (guild == null) {
						f.getMemberGuilds().remove(g);
						continue;
					}

					memberGuilds.add(guild.getName());
					contains = true;
				}

				if (contains) {
					sb.append(PaginationService.GenerateList(memberGuilds, -1, true, true));
				} else {
					sb.append("_");
					sb.append(lang.getError(Errors.FAMILY_SERVER_LIST_EMPTY, false));
					sb.append("_");
				}

				return sb.toString();
			}
		}

		return lang.getError(Errors.NOT_FOUND, true);
	}

	private String cmdViewEmotes(Brain brain, Guild server, ShmamesCommandMessagingChannel messagingChannel) {
		// Get all the emotes from all the servers in all the families.
		EmbedBuilder embed = new EmbedBuilder();

		// This server first.
		addEmoteListField(embed, server, messagingChannel);

		for(Guild family : ShmamesService.GetConnectedFamilyGuilds(brain, server)) {
			addEmoteListField(embed, family, messagingChannel);
		}

		messagingChannel.sendMessage(embed);

		return "";
	}

	private String cmdViewFamilies(Lang lang, Brain brain, Member author) {
		if(author.hasPermission(Permission.ADMINISTRATOR) || App.IsDebug) {
			StringBuilder sb = new StringBuilder();
			sb.append("**");
			sb.append(lang.getMsg(Langs.SERVER_FAMILY_LIST));
			sb.append("**");
			sb.append("\n");

			boolean contains = false;
			List<String> families = new ArrayList<String>();

			for(String id : brain.getFamilies()){
				for(Family f : App.Shmames.getStorageService().getMotherBrain().getServerFamilies()){
					if(f.getFamID().equals(id)){
						families.add(f.getFamName());
						contains = true;

						break;
					}
				}
			}

			if(contains) {
				sb.append(PaginationService.GenerateList(families, 3, false, false));
			}else{
				sb.append("_");
				sb.append(lang.getError(Errors.SERVER_FAMILY_LIST_EMPTY, false));
				sb.append("_");
			}

			return sb.toString();
		}else{
			return lang.getError(Errors.NO_PERMISSION_USER, true);
		}
	}

	private void addEmoteListField(EmbedBuilder embed, Guild g, ShmamesCommandMessagingChannel channel) {
		StringBuilder guildEmotes = new StringBuilder();
		int tempCounter = 0;

		for(Emote e : g.getEmotes()) {
			if(guildEmotes.length() > 0)
				guildEmotes.append(" ");

			guildEmotes.append(e.getAsMention());

			tempCounter++;

			if(tempCounter == 10) {
				guildEmotes.append("\n");
				tempCounter = 0;
			}
		}

		List<String> emoteLists = PaginationService.SplitString(guildEmotes.toString(), MessageEmbed.VALUE_MAX_LENGTH);

		if((embed.length() + emoteLists.get(0).length()) > MessageEmbed.EMBED_MAX_LENGTH_BOT) {
			channel.sendMessage(embed);

			embed.clearFields();
		}

		embed.addField(g.getName(), emoteLists.get(0), false);

		if(emoteLists.size() > 1) {
			for(int i=1; i<emoteLists.size(); i++) {
				if((embed.length() + emoteLists.get(i).length()) > MessageEmbed.EMBED_MAX_LENGTH_BOT) {
					channel.sendMessage(embed);

					embed.clearFields();
				}

				embed.addField("", emoteLists.get(i), false);
			}
		}
	}
}
