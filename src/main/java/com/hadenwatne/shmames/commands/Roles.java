package com.hadenwatne.shmames.commands;

import com.hadenwatne.botcore.command.Command;
import com.hadenwatne.botcore.command.builder.CommandBuilder;
import com.hadenwatne.botcore.command.builder.CommandParameter;
import com.hadenwatne.botcore.command.builder.CommandStructure;
import com.hadenwatne.botcore.command.builder.types.ParameterType;
import com.hadenwatne.shmames.enums.*;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.factories.EmbedFactory;
import com.hadenwatne.shmames.models.RoleMessage;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Language;
import com.hadenwatne.shmames.services.RandomService;
import com.hadenwatne.shmames.services.ShmamesService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.requests.RestAction;

public class Roles extends Command {
	public Roles() {
		super(true);
	}

	@Override
	protected Permission[] configureRequiredBotPermissions() {
		return new Permission[]{Permission.MESSAGE_SEND, Permission.MESSAGE_SEND_IN_THREADS, Permission.MESSAGE_EMBED_LINKS, Permission.MANAGE_ROLES};
	}

	@Override
	protected CommandStructure buildCommandStructure() {
		return CommandBuilder.Create("roles", "Assign roles to server members by using reactions.")
				.addSubCommands(
						CommandBuilder.Create("new", "Create a new role-assignment post.")
								.addParameters(
										new CommandParameter("message", "An introduction message to display to members.", ParameterType.STRING)
												.setExample("Choose the roles you want!")
								)
								.build(),
						CommandBuilder.Create("add", "Add a new role option to an existing post.")
								.addParameters(
										new CommandParameter("postid", "The post ID to update.", ParameterType.STRING)
												.setExample("ABC456"),
										new CommandParameter("role", "The role to add.", ParameterType.DISCORD_ROLE)
												.setExample("@CoolKids"),
										new CommandParameter("emote", "The server emote others can react with.", ParameterType.DISCORD_EMOTE)
												.setExample(":emote:")
												.setPattern(RegexPatterns.EMOTE.getPattern()),
										new CommandParameter("description", "The server emote others can react with.", ParameterType.STRING)
												.setExample("Anime lovers' chat")
								)
								.build(),
						CommandBuilder.Create("update", "Update info about a role in a post.")
								.addParameters(
										new CommandParameter("postid", "The post ID to update.", ParameterType.STRING)
												.setExample("ABC456"),
										new CommandParameter("role", "The role to add.", ParameterType.DISCORD_ROLE)
												.setExample("@CoolKids"),
										new CommandParameter("emote", "The server emote others can react with.", ParameterType.DISCORD_EMOTE, false)
												.setExample(":emote:")
												.setPattern(RegexPatterns.EMOTE.getPattern()),
										new CommandParameter("description", "The server emote others can react with.", ParameterType.STRING, false)
												.setExample("Anime lovers' chat")
								)
								.build(),
						CommandBuilder.Create("remove", "Remove a role from the role post.")
								.addParameters(
										new CommandParameter("postid", "The post ID to update.", ParameterType.STRING)
												.setExample("ABC456"),
										new CommandParameter("role", "The role to remove.", ParameterType.DISCORD_ROLE)
												.setExample("@CoolKids")
								)
								.build(),
						CommandBuilder.Create("delete", "Delete an existing post.")
								.addParameters(
										new CommandParameter("postid", "The post ID to update.", ParameterType.STRING)
												.setExample("ABC456")
								)
								.build()
				)
				.build();
	}

	@Override
	public EmbedBuilder run (ExecutingCommand executingCommand) {
		String subCommand = executingCommand.getSubCommand();
		Brain brain = executingCommand.getBrain();
		Language language = executingCommand.getLanguage();

		if (ShmamesService.CheckUserPermission(executingCommand.getServer(), brain.getSettingFor(BotSettingName.ROLES_CONFIGURE), executingCommand.getAuthorMember())) {
			switch (subCommand) {
				case "new":
					return cmdNew(brain, language, executingCommand);
				case "add":
					return cmdAdd(brain, language, executingCommand);
				case "update":
					return cmdUpdate(brain, language, executingCommand);
				case "remove":
					return cmdRemove(brain, language, executingCommand);
				case "delete":
					return cmdDelete(brain, language, executingCommand);
			}

			return null;
		} else {
			return response(EmbedType.ERROR, ErrorKeys.NO_PERMISSION_USER.name())
					.setDescription(language.getError(ErrorKeys.NO_PERMISSION_USER));
		}
	}

	public EmbedBuilder cmdNew(Brain brain, Language language, ExecutingCommand executingCommand) {
		String message = executingCommand.getCommandArguments().getAsString("message");
		EmbedBuilder response = response(EmbedType.SUCCESS)
				.setDescription(language.getMsg(LanguageKeys.GENERIC_SUCCESS));

		executingCommand.reply(response, false, onSuccess -> {
			String roleMessageID = RandomService.CreateID();
			RoleMessage roleMessage = new RoleMessage(roleMessageID, onSuccess.getChannel().getId(),onSuccess.getId(), message);

			brain.getRoleMessages().add(roleMessage);

			EmbedBuilder newResponse = buildRoleMessageEmbed(roleMessage, executingCommand.getServer());

			onSuccess.editMessageEmbeds(newResponse.build()).queue();
		});

		return null;
	}

	public EmbedBuilder cmdAdd(Brain brain, Language language, ExecutingCommand executingCommand) {
		String postID = executingCommand.getCommandArguments().getAsString("postid");
		RoleMessage roleMessage = brain.getRoleMessageByID(postID);
		Role roleToAdd = executingCommand.getCommandArguments().getAsRole("role", executingCommand.getServer());
		CustomEmoji emote = executingCommand.getCommandArguments().getAsEmote("emote", executingCommand.getServer());
		String description = executingCommand.getCommandArguments().getAsString("description");

		if(roleToAdd != null && emote != null && roleMessage != null) {
			roleMessage.getEmoteRoleMap().put(emote.getId(), roleToAdd.getId());
			roleMessage.getEmoteTextMap().put(emote.getId(), description);

			// Update embed.
			updateEmbed(roleMessage, executingCommand);

			// Add new reaction to message
			getMessagePost(roleMessage,executingCommand).queue(success -> {
				success.addReaction(emote).queue();
			});

			return response(EmbedType.SUCCESS)
					.setDescription(language.getMsg(LanguageKeys.ITEM_ADDED));
		} else {
			return response(EmbedType.ERROR, ErrorKeys.NOT_FOUND.name())
					.setDescription(language.getError(ErrorKeys.NOT_FOUND));
		}
	}

	public EmbedBuilder cmdUpdate(Brain brain, Language language, ExecutingCommand executingCommand) {
		String postID = executingCommand.getCommandArguments().getAsString("postid");
		RoleMessage roleMessage = brain.getRoleMessageByID(postID);
		Role roleToUpdate = executingCommand.getCommandArguments().getAsRole("role", executingCommand.getServer());

		CustomEmoji newEmote = executingCommand.getCommandArguments().getAsEmote("emote", executingCommand.getServer());
		String description = executingCommand.getCommandArguments().getAsString("description");

		if(roleToUpdate != null && roleMessage != null) {
			String emoteIDKey = null;

			for(String key : roleMessage.getEmoteRoleMap().keySet()) {
				if(roleMessage.getEmoteRoleMap().get(key).equals(roleToUpdate.getId())) {
					emoteIDKey = key;
					break;
				}
			}

			if(emoteIDKey != null) {
				// If updating the emote.
				if(newEmote != null) {
					CustomEmoji oldEmote = executingCommand.getServer().getEmojiById(emoteIDKey);

					// Only bother updating if the emote is different.
					if(!newEmote.getId().equals(emoteIDKey)) {
						description = description != null ? description : roleMessage.getEmoteTextMap().get(emoteIDKey);

						roleMessage.getEmoteRoleMap().remove(emoteIDKey);
						roleMessage.getEmoteTextMap().remove(emoteIDKey);

						roleMessage.getEmoteRoleMap().put(newEmote.getId(), roleToUpdate.getId());
						roleMessage.getEmoteTextMap().put(newEmote.getId(), description);

						// Add new reaction to message
						getMessagePost(roleMessage, executingCommand).queue(success -> {
							success.clearReactions(oldEmote).queue();
							success.addReaction(newEmote).queue();
						});

						emoteIDKey = newEmote.getId();
					}
				}

				// If updating the description.
				if(description != null) {
					roleMessage.getEmoteTextMap().put(emoteIDKey, description);
				}

				// Update embed.
				updateEmbed(roleMessage, executingCommand);

				return response(EmbedType.SUCCESS)
						.setDescription(language.getMsg(LanguageKeys.ITEM_ADDED));
			}
		}

		return response(EmbedType.ERROR, ErrorKeys.NOT_FOUND.name())
				.setDescription(language.getError(ErrorKeys.NOT_FOUND));
	}

	public EmbedBuilder cmdRemove(Brain brain, Language language, ExecutingCommand executingCommand) {
		String postID = executingCommand.getCommandArguments().getAsString("postid");
		RoleMessage roleMessage = brain.getRoleMessageByID(postID);
		Role roleToRemove = executingCommand.getCommandArguments().getAsRole("role", executingCommand.getServer());

		if(roleToRemove != null && roleMessage != null) {
			String emoteIDKey = null;

			for(String key : roleMessage.getEmoteRoleMap().keySet()) {
				if(roleMessage.getEmoteRoleMap().get(key).equals(roleToRemove.getId())) {
					emoteIDKey = key;
					break;
				}
			}

			if(emoteIDKey != null) {
				CustomEmoji emote = executingCommand.getServer().getEmojiById(emoteIDKey);

				roleMessage.getEmoteRoleMap().remove(emoteIDKey);
				roleMessage.getEmoteTextMap().remove(emoteIDKey);

				// Update embed.
				updateEmbed(roleMessage, executingCommand);

				// Remove this emote from the post.
				getMessagePost(roleMessage, executingCommand).queue(success -> {
					success.clearReactions(emote).queue();
				});

				return response(EmbedType.SUCCESS)
						.setDescription(language.getMsg(LanguageKeys.ITEM_REMOVED, new String[]{roleToRemove.getAsMention()}));
			}
		}

		return response(EmbedType.ERROR, ErrorKeys.NOT_FOUND.name())
				.setDescription(language.getError(ErrorKeys.NOT_FOUND));
	}

	public EmbedBuilder cmdDelete(Brain brain, Language language, ExecutingCommand executingCommand) {
		String postID = executingCommand.getCommandArguments().getAsString("postid");
		RoleMessage roleMessage = brain.getRoleMessageByID(postID);

		if(roleMessage != null) {
			getMessagePost(roleMessage,executingCommand).queue(success -> {
				success.delete().queue();
			});

			brain.getRoleMessages().remove(roleMessage);

			return response(EmbedType.SUCCESS)
					.setDescription(language.getMsg(LanguageKeys.ITEM_REMOVED));
		} else {
			return response(EmbedType.ERROR, ErrorKeys.NOT_FOUND.name())
					.setDescription(language.getError(ErrorKeys.NOT_FOUND));
		}
	}

	private void updateEmbed(RoleMessage roleMessage, ExecutingCommand executingCommand) {
		EmbedBuilder response = buildRoleMessageEmbed(roleMessage, executingCommand.getServer());

		getMessagePost(roleMessage,executingCommand).queue(success -> {
			success.editMessageEmbeds(response.build()).queue();
		});
	}

	private EmbedBuilder buildRoleMessageEmbed(RoleMessage roleMessage, Guild server) {
		EmbedBuilder response = EmbedFactory.GetEmbed(EmbedType.INFO, "Role Assignment");

		response.setDescription(roleMessage.getInfoMessage());
		response.addField("How to use", "React below with the emote that matches the role you want.", false);
		response.addField(buildRolesField(roleMessage, server));

		response.setFooter("ID: " + roleMessage.getRoleMessageID());

		return response;
	}

	private MessageEmbed.Field buildRolesField(RoleMessage roleMessage, Guild server) {
		StringBuilder stringBuilder = new StringBuilder();

		for(String emoteID : roleMessage.getEmoteRoleMap().keySet()) {
			if(stringBuilder.length() > 0) {
				stringBuilder.append(System.lineSeparator());
			}

			stringBuilder.append(server.getEmojiById(emoteID).getAsMention());
			stringBuilder.append(": ");
			stringBuilder.append(server.getRoleById(roleMessage.getEmoteRoleMap().get(emoteID)).getAsMention());
			stringBuilder.append(" - ");
			stringBuilder.append(roleMessage.getEmoteTextMap().get(emoteID));
		}

		if(stringBuilder.length() == 0) {
			stringBuilder.append("Use **/roles add** to add roles!");
		}

		return new MessageEmbed.Field("Roles", stringBuilder.toString(), false);
	}

	private RestAction<Message> getMessagePost(RoleMessage roleMessage, ExecutingCommand executingCommand) {
		return executingCommand.getServer().getTextChannelById(roleMessage.getChannelID()).retrieveMessageById(roleMessage.getMessageID());
	}
}
