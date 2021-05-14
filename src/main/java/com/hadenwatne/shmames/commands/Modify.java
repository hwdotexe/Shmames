package com.hadenwatne.shmames.commands;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hadenwatne.shmames.enums.BotSettingName;
import com.hadenwatne.shmames.enums.Langs;
import com.hadenwatne.shmames.models.BotSetting;
import com.hadenwatne.shmames.models.Brain;
import com.hadenwatne.shmames.models.Lang;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.Shmames;
import com.hadenwatne.shmames.Utils;

import javax.annotation.Nullable;

public class Modify implements ICommand {
	private Brain brain;
	private Lang lang;

	@Override
	public String getDescription() {
		return "The Administrator's command to customize bot settings and behavior.";
	}
	
	@Override
	public String getUsage() {
		return "modify [<setting> [new value]]";
	}

	@Override
	public String getExamples() {
		return "`modify`\n" +
				"`modify ALLOW_MODIFY ServerPolice`\n" +
				"`modify SERVER_LANG`";
	}

	@Override
	public String run(String args, User author, Message message) {
		BotSetting canModify = brain.getSettingFor(BotSettingName.ALLOW_MODIFY);

		if(Utils.checkUserPermission(canModify, message.getMember())) {
			Matcher m = Pattern.compile("^([\\w]+)( [\\w\\-]+)?$").matcher(args);
			
			if(m.find()) {
				if(BotSettingName.contains(m.group(1))) {
					BotSettingName settingName = BotSettingName.valueOf(m.group(1).toUpperCase());
					BotSetting setting = brain.getSettingFor(settingName);

					if(m.group(2) != null) {
						String value = m.group(2).trim();

						// Ensure that this setting is only changed by an Administrator.
						if (settingName == BotSettingName.ALLOW_MODIFY) {
							if (!message.getMember().hasPermission(Permission.ADMINISTRATOR) && !Shmames.isDebug) {
								return lang.getError(Errors.NO_PERMISSION_USER, true);
							}
						}

						// Ensure that this setting is only changed to an existing Lang.
						if (settingName == BotSettingName.SERVER_LANG) {
							boolean found = false;

							for(Lang l : Shmames.getLangs().getAllLangs()) {
								if(l.getLangName().equalsIgnoreCase(value)){
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

						if (setting.setValue(value, brain)) {
							EmbedBuilder eBuilder = new EmbedBuilder();

							eBuilder.setColor(Color.ORANGE);
							flexValueType(eBuilder, setting, message.getGuild());
							eBuilder.addField("Status", lang.getMsg(Langs.SETTING_UPDATED_SUCCESS), false);
							message.getChannel().sendMessage(eBuilder.build()).queue();

							return "";
						} else {
							// Not successful
							return lang.wrongUsage("`modify " + setting.getName().toString() + " <" + setting.getType().toString() + ">`");
						}
					}else{
						EmbedBuilder eBuilder = new EmbedBuilder();

						eBuilder.setColor(Color.ORANGE);
						flexValueType(eBuilder, setting, message.getGuild());
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

						message.getChannel().sendMessage(eBuilder.build()).queue();

						return "";
					}
				}else {
					return lang.getError(Errors.SETTING_NOT_FOUND, true);
				}
			} else {
				EmbedBuilder eBuilder = new EmbedBuilder();
				
		        eBuilder.setColor(Color.ORANGE);
		        eBuilder.setTitle(lang.getMsg(Langs.SETTING_LIST_TITLE));
		        eBuilder.setFooter("Do not include \"#\" or \":\" symbols. // Use \""+Shmames.getBotName()+" modify <setting>\" for info.");
		        
		        for(BotSetting v : brain.getSettings()) {
		        	flexValueType(eBuilder, v, message.getGuild());
		        }
		        
		        message.getChannel().sendMessage(eBuilder.build()).queue();
		        
		        return "";
			}
		}else {
			return lang.getError(Errors.NO_PERMISSION_USER, true);
		}
	}

	private void flexValueType(EmbedBuilder eBuilder, BotSetting setting, Guild g){
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
	public String[] getAliases() {
		return new String[] {"modify"};
	}

	@Override
	public void setRunContext(Lang lang, @Nullable Brain brain) {
		this.brain = brain;
		this.lang = lang;
	}
	
	@Override
	public boolean requiresGuild() {
		return true;
	}
}
