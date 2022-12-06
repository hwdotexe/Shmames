package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.App;
import com.hadenwatne.shmames.commandbuilder.*;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.enums.ErrorKeys;
import com.hadenwatne.shmames.enums.LanguageKeys;
import com.hadenwatne.shmames.enums.RegexPatterns;
import com.hadenwatne.shmames.factories.EmbedFactory;
import com.hadenwatne.shmames.models.Family;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import com.hadenwatne.shmames.models.command.ExecutingCommandArguments;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Language;
import com.hadenwatne.shmames.services.MessageService;
import com.hadenwatne.shmames.services.PaginationService;
import com.hadenwatne.shmames.services.ShmamesService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FamilyCmd extends Command {
	private final int MAX_FAMILIES = 3;

	public FamilyCmd() {
		super(true);
	}

	@Override
	protected CommandStructure buildCommandStructure() {
		return CommandBuilder.Create("family", "Create and manage server families.")
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
	public EmbedBuilder run (ExecutingCommand executingCommand) {
		String subCommand = executingCommand.getSubCommand();
		String subCommandGroup = executingCommand.getSubCommandGroup();
		Language language = executingCommand.getLanguage();
		Brain brain = executingCommand.getBrain();
		Guild server = executingCommand.getServer();
		Member author = executingCommand.getAuthorMember();

		switch (subCommandGroup) {
			case "code":
				return cmdCode(language, brain, server, author, executingCommand);
			case "view":
				return cmdView(language, brain, server, author, executingCommand);
		}

		switch(subCommand) {
			case "create":
				return cmdCreate(language, brain, server, author, executingCommand.getCommandArguments());
			case "leave":
				return cmdLeave(language, brain, server, author, executingCommand.getCommandArguments());
			case "kick":
				return cmdKick(language, brain, author, executingCommand.getCommandArguments());
		}

		return null;
	}

	private EmbedBuilder cmdCreate(Language language, Brain brain, Guild server, Member author, ExecutingCommandArguments args) {
		String familyName = args.getAsString("familyName").toLowerCase();

		if (author.hasPermission(Permission.ADMINISTRATOR) || App.IsDebug) {
			for (Family f : App.Shmames.getStorageService().getMotherBrain().getServerFamilies()) {
				if (f.getFamilyOwner() == author.getIdLong()) {
					if (f.getFamName().equals(familyName)) {
						return response(EmbedType.ERROR, ErrorKeys.FAMILY_ALREADY_EXISTS.name())
								.setDescription(language.getError(ErrorKeys.FAMILY_ALREADY_EXISTS));
					}
				}
			}

			Family newFam = new Family(UUID.randomUUID().toString(), familyName, author.getIdLong());

			// Tell the family it contains this server.
			newFam.addToFamily(server.getIdLong());

			// Add the Family to the system.
			App.Shmames.getStorageService().getMotherBrain().getServerFamilies().add(newFam);
			brain.getFamilies().add(newFam.getFamID());

			return response(EmbedType.SUCCESS)
					.setDescription(language.getMsg(LanguageKeys.FAMILY_CREATED));
		} else {
			return response(EmbedType.ERROR, ErrorKeys.NO_PERMISSION_USER.name())
					.setDescription(language.getError(ErrorKeys.NO_PERMISSION_USER));
		}
	}

	private EmbedBuilder cmdLeave(Language language, Brain brain, Guild server, Member author, ExecutingCommandArguments args) {
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

					return response(EmbedType.SUCCESS)
							.setDescription(language.getMsg(LanguageKeys.FAMILY_REMOVED_SERVER, new String[]{server.getName(), f.getFamName()}));
				}
			}

			return response(EmbedType.ERROR, ErrorKeys.NOT_FOUND.name())
					.setDescription(language.getError(ErrorKeys.NOT_FOUND));
		} else {
			return response(EmbedType.ERROR, ErrorKeys.NO_PERMISSION_USER.name())
					.setDescription(language.getError(ErrorKeys.NO_PERMISSION_USER));
		}
	}

	private EmbedBuilder cmdKick(Language language, Brain brain, Member author, ExecutingCommandArguments args) {
		String familyName = args.getAsString("familyName").toLowerCase();
		int serverIndex = args.getAsInteger("serverNumber")-1;

		for (Family f : App.Shmames.getStorageService().getMotherBrain().getServerFamilies()) {
			if (f.getFamilyOwner() == author.getIdLong() && f.getFamName().equals(familyName)) {
				if (f.getMemberGuilds().size() >= serverIndex) {
					long guildID = f.getMemberGuilds().get(serverIndex);
					Guild g = App.Shmames.getJDA().getGuildById(guildID);
					String gName = "";

					// If the Guild is empty but was found in the list, remove it from the Family Guild list.
					if (g == null) {
						f.getMemberGuilds().remove(guildID);
						gName = "that server";
					} else {
						brain.getFamilies().remove(f.getFamID());
						f.getMemberGuilds().remove(g.getIdLong());
						gName = g.getName();
					}

					// Remove the family if empty.
					if (f.getMemberGuilds().size() == 0) {
						App.Shmames.getStorageService().getMotherBrain().getServerFamilies().remove(f);
					}

					return response(EmbedType.SUCCESS)
							.setDescription(language.getMsg(LanguageKeys.FAMILY_REMOVED_SERVER, new String[]{gName, f.getFamName()}));
				} else {
					return response(EmbedType.ERROR, ErrorKeys.FAMILY_NOT_JOINED.name())
							.setDescription(language.getError(ErrorKeys.FAMILY_NOT_JOINED));
				}
			}
		}

		return response(EmbedType.ERROR, ErrorKeys.NOT_FOUND.name())
				.setDescription(language.getError(ErrorKeys.NOT_FOUND));
	}

	private EmbedBuilder cmdCode(Language language, Brain brain, Guild server, Member author, ExecutingCommand executingCommand) {
		ExecutingCommandArguments args = executingCommand.getCommandArguments();
		String subCommand = executingCommand.getSubCommand();

		switch (subCommand) {
			case "generate":
				return cmdCodeGenerate(language, author.getUser(), args);
			case "redeem":
				return cmdCodeRedeem(language, brain, server, author, args);
		}

		return null;
	}

	private EmbedBuilder cmdCodeGenerate(Language language, User author, ExecutingCommandArguments args) {
		String familyName = args.getAsString("familyName").toLowerCase();

		for(Family f : App.Shmames.getStorageService().getMotherBrain().getServerFamilies()) {
			if (f.getFamilyOwner() == author.getIdLong() && f.getFamName().equalsIgnoreCase(familyName)) {
				if (f.getMemberGuilds().size() < 7) {
					EmbedBuilder embedBuilder = EmbedFactory.GetEmbed(EmbedType.INFO, LanguageKeys.FAMILY_JOIN_CODE.name())
									.setDescription(language.getMsg(LanguageKeys.FAMILY_JOIN_CODE, new String[]{f.getFamName(), f.getNewJoinCode()}));

					MessageService.SendDirectMessage(author, embedBuilder);

					return response(EmbedType.SUCCESS)
							.setDescription(language.getMsg(LanguageKeys.SENT_PRIVATE_MESSAGE));
				} else {
					return response(EmbedType.ERROR, ErrorKeys.FAMILY_MEMBER_MAXIMUM_REACHED.name())
							.setDescription(language.getError(ErrorKeys.FAMILY_MEMBER_MAXIMUM_REACHED));
				}
			}
		}

		return response(EmbedType.ERROR, ErrorKeys.NOT_FOUND.name())
				.setDescription(language.getError(ErrorKeys.NOT_FOUND));
	}

	private EmbedBuilder cmdCodeRedeem(Language language, Brain brain, Guild server, Member author, ExecutingCommandArguments args) {
		String joinCode = args.getAsString("joinCode").toLowerCase();

		for (Family f : App.Shmames.getStorageService().getMotherBrain().getServerFamilies()) {
			if (f.validateCode(joinCode)) {
				f.clearCode();

				if (author.hasPermission(Permission.ADMINISTRATOR) || App.IsDebug) {
					if (brain.getFamilies().size() < MAX_FAMILIES) {
						if (!brain.getFamilies().contains(f.getFamID())) {
							f.addToFamily(server.getIdLong());
							brain.getFamilies().add(f.getFamID());

							return response(EmbedType.SUCCESS)
									.setDescription(language.getMsg(LanguageKeys.FAMILY_JOINED, new String[]{server.getName(), f.getFamName()}));
						} else {
							return response(EmbedType.ERROR, ErrorKeys.FAMILY_ALREADY_JOINED.name())
									.setDescription(language.getError(ErrorKeys.FAMILY_ALREADY_JOINED))
									.appendDescription(System.lineSeparator())
									.appendDescription(language.getMsg(LanguageKeys.FAMILY_JOIN_CODE_INVALIDATED));
						}
					} else {
						return response(EmbedType.ERROR, ErrorKeys.FAMILY_MAXIMUM_REACHED.name())
								.setDescription(language.getError(ErrorKeys.FAMILY_MAXIMUM_REACHED, new String[]{Integer.toString(MAX_FAMILIES)}))
								.appendDescription(System.lineSeparator())
								.appendDescription(language.getMsg(LanguageKeys.FAMILY_JOIN_CODE_INVALIDATED));
					}
				} else {
					return response(EmbedType.ERROR, ErrorKeys.NO_PERMISSION_USER.name())
							.setDescription(language.getError(ErrorKeys.NO_PERMISSION_USER))
							.appendDescription(System.lineSeparator())
							.appendDescription(language.getMsg(LanguageKeys.FAMILY_JOIN_CODE_INVALIDATED));
				}
			}
		}

		return response(EmbedType.ERROR, ErrorKeys.FAMILY_INVALID_DETAIL.name())
				.setDescription(language.getError(ErrorKeys.FAMILY_INVALID_DETAIL));
	}

	private EmbedBuilder cmdView(Language language, Brain brain, Guild server, Member author, ExecutingCommand executingCommand) {
		ExecutingCommandArguments args = executingCommand.getCommandArguments();
		String subCommand = executingCommand.getSubCommand();

		switch (subCommand) {
			case "servers":
				return cmdViewServers(language, brain, author, args);
			case "emotes":
				return cmdViewEmotes(brain, server, executingCommand);
			case "families":
				return cmdViewFamilies(language, brain, author);
		}

		return null;
	}

	private EmbedBuilder cmdViewServers(Language language, Brain brain, Member author, ExecutingCommandArguments args) {
		String familyName = args.getAsString("familyName").toLowerCase();

		for (Family f : App.Shmames.getStorageService().getMotherBrain().getServerFamilies()) {
			// View the family if the user is the family owner, or if they are an Admin and this server is a member
			if ((f.getFamilyOwner() == author.getIdLong() || (brain.getFamilies().contains(f.getFamID()) && author.hasPermission(Permission.ADMINISTRATOR))) && f.getFamName().equalsIgnoreCase(familyName)) {
				List<String> memberGuilds = new ArrayList<>();

				for (long g : new ArrayList<>(f.getMemberGuilds())) {
					Guild guild = App.Shmames.getJDA().getGuildById(g);

					// Quick null check!
					if (guild == null) {
						f.getMemberGuilds().remove(g);
						continue;
					}

					memberGuilds.add(guild.getName());
				}

				String serverList = "";
				if(memberGuilds.size() > 0) {
					serverList = PaginationService.GenerateList(memberGuilds, 1, true, false);
				} else {
					serverList = language.getError(ErrorKeys.FAMILY_SERVER_LIST_EMPTY);
				}

				return response(EmbedType.INFO)
						.addField(language.getMsg(LanguageKeys.FAMILY_SERVER_LIST, new String[]{f.getFamName()}), serverList, false);
			}
		}

		return response(EmbedType.ERROR, ErrorKeys.NOT_FOUND.name())
				.setDescription(language.getError(ErrorKeys.NOT_FOUND));
	}

	private EmbedBuilder cmdViewEmotes(Brain brain, Guild server, ExecutingCommand executingCommand) {
		// Get all the emotes from all the servers in all the families.
		EmbedBuilder embed = response(EmbedType.INFO, "Emotes");

		// This server first.
		addEmoteListField(embed, server, executingCommand);

		// Add each connected server's emotes.
		for(Guild family : ShmamesService.GetConnectedFamilyGuilds(brain, server)) {
			addEmoteListField(embed, family, executingCommand);
		}

		return embed;
	}

	private EmbedBuilder cmdViewFamilies(Language language, Brain brain, Member author) {
		if(author.hasPermission(Permission.ADMINISTRATOR) || App.IsDebug) {
			List<String> families = new ArrayList<String>();

			for(String id : brain.getFamilies()){
				for(Family f : App.Shmames.getStorageService().getMotherBrain().getServerFamilies()){
					if(f.getFamID().equals(id)){
						families.add(f.getFamName());
						break;
					}
				}
			}

			String familyList = "";
			if(families.size() > 0) {
				familyList = PaginationService.GenerateList(families, 1, true, false);
			} else {
				familyList = language.getError(ErrorKeys.FAMILY_LIST_EMPTY);
			}

			return response(EmbedType.INFO)
					.addField(language.getMsg(LanguageKeys.FAMILY_LIST), familyList, false);
		}else{
			return response(EmbedType.ERROR, ErrorKeys.NO_PERMISSION_USER.name())
					.setDescription(language.getError(ErrorKeys.NO_PERMISSION_USER));
		}
	}

	private void addEmoteListField(EmbedBuilder embed, Guild g, ExecutingCommand executingCommand) {
		StringBuilder guildEmotes = new StringBuilder();
		final int emotesPerLine = 10;
		int tempCounter = 0;

		for(CustomEmoji e : g.getEmojis()) {
			if(guildEmotes.length() > 0)
				guildEmotes.append(" ");

			guildEmotes.append(e.getAsMention());

			tempCounter++;

			if(tempCounter == emotesPerLine) {
				guildEmotes.append(System.lineSeparator());
				tempCounter = 0;
			}
		}

		// Break the emotes down into chunks that will fit within a MessageEmbed
		List<String> emoteLists = PaginationService.SplitString(guildEmotes.toString(), MessageEmbed.VALUE_MAX_LENGTH);

		// Try to add the emote list to the embed. If the length is exceeded, send it as-is and clear out the fields.
		for(String emoteList : emoteLists) {
			if((embed.length() + emoteList.length()) > MessageEmbed.EMBED_MAX_LENGTH_BOT) {
				executingCommand.reply(embed);

				embed.clearFields();
				continue;
			}

			embed.addField(g.getName(), emoteList, true);
		}
	}
}
