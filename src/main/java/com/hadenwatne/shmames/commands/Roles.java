package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
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
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.RestAction;

public class Roles extends Command {
	public Roles() {
		super(true);
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
										new CommandParameter("emote", "The emote others can react with.", ParameterType.DISCORD_EMOTE)
												.setExample(":emote:")
												.setPattern(RegexPatterns.EMOTE.getPattern())
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
		Emote emote = executingCommand.getCommandArguments().getAsEmote("emote", executingCommand.getServer());

		if(roleToAdd != null && emote != null && roleMessage != null) {
			roleMessage.getRoleEmoteMap().put(emote.getId(), roleToAdd.getId());

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

	public EmbedBuilder cmdDelete(Brain brain, Language language, ExecutingCommand executingCommand) {
		String postID = executingCommand.getCommandArguments().getAsString("postid");
		RoleMessage roleMessage = brain.getRoleMessageByID(postID);

		if(roleMessage != null) {
			getMessagePost(roleMessage,executingCommand).queue(success -> {
				success.delete().queue();
			});

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

		for(String emoteID : roleMessage.getRoleEmoteMap().keySet()) {
			if(stringBuilder.length() > 0) {
				stringBuilder.append(System.lineSeparator());
			}

			stringBuilder.append(server.getEmoteById(emoteID).getAsMention());
			stringBuilder.append(": ");
			stringBuilder.append(server.getRoleById(roleMessage.getRoleEmoteMap().get(emoteID)).getAsMention());
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
