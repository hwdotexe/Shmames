package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.App;
import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.enums.BotSettingName;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.enums.Langs;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import com.hadenwatne.shmames.models.command.ExecutingCommandArguments;
import com.hadenwatne.shmames.models.data.BotSetting;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import com.hadenwatne.shmames.services.PaginationService;
import com.hadenwatne.shmames.services.ShmamesService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;

import java.util.ArrayList;
import java.util.List;

public class Modify extends Command {
	public Modify() {
		super(true);
	}

	@Override
	protected CommandStructure buildCommandStructure() {
		CommandParameter setting = new CommandParameter("setting", "The setting to view or update", ParameterType.SELECTION);

		for (BotSettingName name : BotSettingName.values()) {
			setting.addSelectionOptions(name.name());
		}

		return CommandBuilder.Create("modify", "The Administrator's command to customize bot settings and behavior.")
				.addAlias("settings")
				.addSubCommands(
						CommandBuilder.Create("set", "Change a setting.")
								.addParameters(
										setting
												.setExample("server_lang"),
										new CommandParameter("value", "The new value for this setting.", ParameterType.STRING)
												.setExample("pirate")
								)
								.build(),
						CommandBuilder.Create("view", "View all current settings.")
								.build(),
						CommandBuilder.Create("help", "View help information for a setting.")
								.addParameters(
										setting
												.setExample("server_lang")
								)
								.build()
				)
				.build();
	}

	@Override
	public EmbedBuilder run(ExecutingCommand executingCommand) {
		Lang lang = executingCommand.getLanguage();
		Brain brain = executingCommand.getBrain();
		BotSetting canModify = brain.getSettingFor(BotSettingName.ALLOW_MODIFY);
		User author = executingCommand.getAuthorUser();
		Guild server = executingCommand.getServer();
		String subCommand = executingCommand.getSubCommand();

		// Disallow users if they don't have permission.
		if(!ShmamesService.CheckUserPermission(server, canModify, author)) {
			return response(EmbedType.ERROR, Errors.NO_PERMISSION_USER.name())
					.setDescription(executingCommand.getLanguage().getError(Errors.NO_PERMISSION_USER));
		}

		switch (subCommand) {
			case "set":
				return cmdSet(lang, brain, server, executingCommand);
			case "view":
				return cmdView(lang, brain, server);
			case "help":
				return cmdHelp(brain, server, executingCommand.getCommandArguments());
		}

		return null;
	}

	private EmbedBuilder cmdView(Lang lang, Brain brain, Guild server) {
		EmbedBuilder embedBuilder = response(EmbedType.INFO, lang.getMsg(Langs.SETTING_LIST_TITLE));

		for(BotSetting botSetting : brain.getSettings()) {
			embedBuilder.addField(getFormattedSettingField(botSetting, server));
		}

		return embedBuilder;
	}

	private EmbedBuilder cmdHelp(Brain brain, Guild server, ExecutingCommandArguments arguments) {
		String settingName = arguments.getAsString("setting").toUpperCase();
		BotSettingName botSettingName = BotSettingName.valueOf(settingName);
		BotSetting botSetting = brain.getSettingFor(botSettingName);
		EmbedBuilder embedBuilder = response(EmbedType.INFO, settingName);

		embedBuilder.addField(getFormattedSettingField(botSetting, server));
		embedBuilder.setDescription(botSettingName.getDescription());
		embedBuilder.addField("Type", botSetting.getType().name(), true);
		embedBuilder.addField("Possible Values", getSettingPossibleValues(botSetting), false);

		return embedBuilder;
	}

	private EmbedBuilder cmdSet(Lang lang, Brain brain, Guild server, ExecutingCommand executingCommand) {
		String settingName = executingCommand.getCommandArguments().getAsString("setting").toUpperCase();
		String settingValue = executingCommand.getCommandArguments().getAsString("value");
		BotSettingName botSettingName = BotSettingName.valueOf(settingName);
		BotSetting botSetting = brain.getSettingFor(botSettingName);

		// Ensure that this setting is only changed by an Administrator.
		if (botSettingName == BotSettingName.ALLOW_MODIFY) {
			Member member = executingCommand.getAuthorMember();

			if (!member.hasPermission(Permission.ADMINISTRATOR) && !App.IsDebug) {
				return response(EmbedType.ERROR, Errors.NO_PERMISSION_USER.name())
						.setDescription(lang.getError(Errors.NO_PERMISSION_USER));
			}
		}

		// Ensure that this setting is only changed to an existing Lang.
		if (botSettingName == BotSettingName.SERVER_LANG) {
			boolean found = false;

			for(Lang l : App.Shmames.getLanguageService().getAllLangs()) {
				if (l.getLangName().equalsIgnoreCase(settingValue)) {
					found = true;
					break;
				}
			}

			if(!found) {
				return response(EmbedType.ERROR, Errors.NOT_FOUND.name())
						.setDescription(lang.getError(Errors.NOT_FOUND));
			}
		}

		// If the value is a mentionable, retrieve its ID.
		switch(botSetting.getType()) {
			case ROLE:
				Role role = executingCommand.getCommandArguments().getAsRole("value", server);

				if(role != null) {
					// I have to do it this way, otherwise JDA returns a Snowflake.
					settingValue = Long.toString(role.getIdLong());
					break;
				}

				if(executingCommand.getCommandArguments().getAsString("value").equalsIgnoreCase("administrator")) {
					settingValue = "administrator";
				}

				break;
			case EMOTE:
				Emote emote = executingCommand.getCommandArguments().getAsEmote("value", server);

				if(emote != null) {
					settingValue = emote.getId();
				}

				break;
			case CHANNEL:
				MessageChannel channel = executingCommand.getCommandArguments().getAsChannel("value", server);

				if(channel != null) {
					settingValue = channel.getId();
				}

				break;
		}

		// Set the value and return a success message if complete.
		if (botSetting.setValue(settingValue, brain)) {
			return response(EmbedType.SUCCESS, botSettingName.name())
					.setDescription(lang.getMsg(Langs.SETTING_UPDATED_SUCCESS))
					.addField(getFormattedSettingField(botSetting, server));
		} else {
			// Not successful
			return response(EmbedType.ERROR, Errors.SETTING_VALUE_INVALID.name())
					.setDescription(lang.getError(Errors.SETTING_VALUE_INVALID));
		}
	}

	private String getSettingPossibleValues(BotSetting botSetting) {
		if(botSetting.getName() == BotSettingName.SERVER_LANG) {
			StringBuilder sb = new StringBuilder();
			List<String> langs = new ArrayList<>();

			for(Lang l : App.Shmames.getLanguageService().getAllLangs()) {
				langs.add(l.getLangName());
			}

			sb.append(PaginationService.GenerateList(langs, -1, false, false));

			return sb.toString();
		} else {
			switch(botSetting.getType()) {
				case BOOLEAN:
					return "`true`, `false`";
				case CHANNEL:
					return "#any_channel";
				case EMOTE:
					return ":AnyServerEmoji:";
				case NUMBER:
					return "1-99";
				case ROLE:
					return "@Any_Role, `administrator`, `everyone`";
				default:
					return "any";
			}
		}
	}

	private MessageEmbed.Field getFormattedSettingField(BotSetting setting, Guild server){
		boolean isValid = false;
		String mention = "";
		String settingValue = setting.getAsString();

		switch(setting.getType()){
			case CHANNEL:
				try {
					TextChannel mc = server.getTextChannelById(settingValue);

					if (mc != null) {
						isValid = true;
						mention = mc.getAsMention();
					}
				} catch (NumberFormatException e) {
					isValid = false;
				}

				break;
			case EMOTE:
				try {
					Emote em = server.getEmoteById(settingValue);

					if(em != null) {
						isValid = true;
						mention = em.getAsMention();
					}
				} catch (NumberFormatException e) {
					isValid = false;
				}

				break;
			case ROLE:
				try {
					if(settingValue.equals("administrator")) {
						mention = settingValue;
						isValid = true;
						break;
					}

					Role role = server.getRoleById(settingValue);

					if(role != null) {
						isValid = true;
						mention = role.getAsMention();
					}
				} catch (Exception e) {
					isValid = false;
				}

				break;
			default:
				isValid = true;
				mention = settingValue;
		}

		String value;

		if(isValid) {
			value = "**Current Value:** " + mention;
		} else {
			value = ":warning: INVALID :warning:";
		}

		return new MessageEmbed.Field("**__"+setting.getName()+"__**", value, true);
	}
}
