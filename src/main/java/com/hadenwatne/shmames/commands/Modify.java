package com.hadenwatne.shmames.commands;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.enums.BotSettingName;
import com.hadenwatne.shmames.enums.Langs;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import com.hadenwatne.shmames.models.command.ShmamesCommandMessagingChannel;
import com.hadenwatne.shmames.models.data.BotSetting;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.Shmames;
import com.hadenwatne.shmames.Utils;

public class Modify implements ICommand {
	private final CommandStructure commandStructure;

	public Modify() {
		CommandParameter setting = new CommandParameter("setting", "The setting to view or update", ParameterType.SELECTION, false);

		for(BotSettingName name : BotSettingName.values()) {
			setting.addSelectionOptions(name.name());
		}

		this.commandStructure = CommandBuilder.Create("modify", "The Administrator's command to customize bot settings and behavior.")
				.addParameters(
						setting,
						new CommandParameter("value", "The new value for this setting.", ParameterType.STRING, false)
				)
				.build();
	}

	@Override
	public CommandStructure getCommandStructure() {
		return this.commandStructure;
	}

	@Override
	public String getExamples() {
		return "`modify`\n" +
				"`modify ALLOW_MODIFY ServerPolice`\n" +
				"`modify SERVER_LANG`";
	}

	@Override
	public String run (Lang lang, Brain brain, ShmamesCommandData data) {
		BotSetting canModify = brain.getSettingFor(BotSettingName.ALLOW_MODIFY);
		User author = data.getAuthor();
		Guild server = data.getServer();

		if(Utils.checkUserPermission(server, canModify, author)) {
			String settingName = data.getArguments().getAsString("setting");
			String settingValue = data.getArguments().getAsString("value");
			ShmamesCommandMessagingChannel messagingChannel = data.getMessagingChannel();

			// Send a list if no setting is provided.
			if(settingName == null) {
				EmbedBuilder eBuilder = new EmbedBuilder();

				eBuilder.setColor(Color.ORANGE);
				eBuilder.setTitle(lang.getMsg(Langs.SETTING_LIST_TITLE));
				eBuilder.setFooter("Use \""+Shmames.getBotName()+" modify <setting>\" for info.");

				for(BotSetting v : brain.getSettings()) {
					appendValueEmbedField(eBuilder, v, server);
				}

				messagingChannel.sendMessage(eBuilder);

				return "";
			}

			// Make this all uppercase.
			settingName = settingName.toUpperCase();

			if(BotSettingName.contains(settingName)) {
				BotSettingName botSettingName = BotSettingName.valueOf(settingName);
				BotSetting setting = brain.getSettingFor(botSettingName);

				// Show the current value if no new value is provided.
				if(settingValue == null) {
					displayCurrentSetting(setting, server, messagingChannel);
					return "";
				}

				// Ensure that this setting is only changed by an Administrator.
				if (botSettingName == BotSettingName.ALLOW_MODIFY) {
					Member member = server.getMember(author);

					if (!member.hasPermission(Permission.ADMINISTRATOR) && !Shmames.isDebug) {
						return lang.getError(Errors.NO_PERMISSION_USER, true);
					}
				}

				// Ensure that this setting is only changed to an existing Lang.
				if (botSettingName == BotSettingName.SERVER_LANG) {
					boolean found = false;

					for(Lang l : Shmames.getLangs().getAllLangs()) {
						if(l.getLangName().equalsIgnoreCase(settingValue)){
							found=true;
							break;
						}
					}

					if(!found) {
						List<String> langNames = new ArrayList<>();

						for(Lang l : Shmames.getLangs().getAllLangs()){
							langNames.add(l.getLangName());
						}

						String langList = Utils.generateList(langNames, 0, false, false);

						return lang.getError(Errors.NOT_FOUND, true) + System.lineSeparator() + "Options: " + langList;
					}
				}

				// Adjust the value before trying to set.
				switch(setting.getType()) {
					case ROLE:
						Role role = data.getArguments().getAsRole("value", server);

						if(role != null) {
							settingValue = role.getName();
						}

						break;
					case EMOTE:
						Emote emote = data.getArguments().getAsEmote("value", server);

						if(emote != null) {
							settingValue = emote.getName();
						}

						break;
					case CHANNEL:
						MessageChannel channel = data.getArguments().getAsChannel("value", server);

						if(channel != null) {
							settingValue = channel.getName();
						}

						break;
				}

				// Set the value and return a success message if complete.
				if (setting.setValue(settingValue, brain)) {
					EmbedBuilder eBuilder = new EmbedBuilder();

					eBuilder.setColor(Color.ORANGE);
					appendValueEmbedField(eBuilder, setting, server);
					eBuilder.addField("Status", lang.getMsg(Langs.SETTING_UPDATED_SUCCESS), false);

					messagingChannel.sendMessage(eBuilder);

					return "";
				} else {
					// Not successful
					return lang.wrongUsage("`modify " + setting.getName().toString() + " <" + setting.getType().toString() + ">`");
				}
			}else {
				return lang.getError(Errors.SETTING_NOT_FOUND, true);
			}
		}else {
			return lang.getError(Errors.NO_PERMISSION_USER, true);
		}
	}

	private void displayCurrentSetting(BotSetting setting, Guild server, ShmamesCommandMessagingChannel messagingChannel) {
		EmbedBuilder eBuilder = new EmbedBuilder();
		BotSettingName settingName = setting.getName();

		eBuilder.setColor(Color.ORANGE);
		appendValueEmbedField(eBuilder, setting, server);
		eBuilder.addField("Description", settingName.getDescription(), false);
		eBuilder.addField("Type", setting.getType().name(), false);

		switch(settingName) {
			case PIN_POLLS:
				eBuilder.addField("Possible Values", "`true`, `false`", false);
				break;
			case PIN_CHANNEL:
				eBuilder.addField("Possible Values", "Any text channel", false);
				break;
			case MANAGE_MUSIC:
			case ALLOW_MODIFY:
			case ALLOW_POLLS:
			case ALLOW_NICKNAME:
			case RESET_EMOTE_STATS:
				eBuilder.addField("Possible Values", "Any role, `administrator`, `everyone`", false);
				break;
			case REMOVAL_EMOTE:
			case APPROVAL_EMOTE:
				eBuilder.addField("Possible Values", "Any server emoji", false);
				break;
			case REMOVAL_THRESHOLD:
			case APPROVAL_THRESHOLD:
				eBuilder.addField("Possible Values", "Any positive number", false);
				break;
			case SERVER_LANG:
				StringBuilder sb = new StringBuilder();
				List<String> langs = new ArrayList<>();

				for(Lang l : Shmames.getLangs().getAllLangs()) {
					langs.add(l.getLangName());
				}

				sb.append(Utils.generateList(langs, -1, false, false));

				eBuilder.addField("Possible Values", sb.toString(), false);
				break;
			default:
		}

		messagingChannel.sendMessage(eBuilder);
	}

	private void appendValueEmbedField(EmbedBuilder eBuilder, BotSetting setting, Guild g){
		String mention = "";

		switch(setting.getType()){
			case CHANNEL:
				try {
					TextChannel mc = g.getTextChannelById(setting.getValue());

					if (mc != null) {
						mention = mc.getAsMention();
					}
				} catch (NumberFormatException e) {
					mention = ":warning: INVALID";
				}

				break;
			case EMOTE:
				try {
					Emote em = g.getEmoteById(setting.getValue());

					if(em != null) {
						mention = em.getAsMention();
					}
				} catch (NumberFormatException e) {
					mention = ":warning: INVALID";
				}

				break;
			default:
				mention = setting.getValue();
		}

		String value = "**Type:** " + setting.getType() + "\n**Value:** " + mention;

		eBuilder.addField("**__"+setting.getName()+"__**", value, true);
	}

	@Override
	public boolean requiresGuild() {
		return true;
	}
}
