package com.hadenwatne.discordbot.commands;

import com.hadenwatne.discordbot.storage.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import com.hadenwatne.discordbot.Shmames;
import com.hadenwatne.discordbot.Utils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FamilyCmd implements ICommand {
	private Lang lang;
	private Brain brain;

	@Override
	public String getDescription() {
		return "Manage your server family membership.\n`family create [name]` - Create a new server family\n`family add [name|code]` - Create a join code, or redeem a code to join a family\n`family view [name|emotes]` - View information about the family or list family emotes";
	}
	
	@Override
	public String getUsage() {
		return "family <create|add|view|remove> [family|code|emotes] [server]";
	}

	@Override
	public String run(String args, User author, Message message) {
		Matcher m = Pattern.compile("^((create)|(add)|(view)|(remove))( [a-z0-9\\-]+)?( \\d+)?$", Pattern.CASE_INSENSITIVE).matcher(args);

		if(m.find()){
			String cmd = m.group(1);
			String arg1 = m.group(6);
			String arg2 = m.group(7);

			switch(cmd.toLowerCase()){
				case "create": // create famName
					if(message.getMember().hasPermission(Permission.ADMINISTRATOR) || Shmames.isDebug) {
						if (arg1 != null) {
							String name = arg1.trim().toLowerCase();

							for (Family f : Shmames.getBrains().getMotherBrain().getServerFamilies()) {
								if (f.getFamilyOwner() == author.getIdLong()) {
									if (f.getFamName().equals(name)) {
										return lang.getError(Errors.FAMILY_ALREADY_EXISTS, true);
									}
								}
							}

							Family newFam = new Family(UUID.randomUUID().toString(), name, author.getIdLong());
							newFam.addToFamily(message.getGuild());

							// Add the Family to the system
							Shmames.getBrains().getMotherBrain().getServerFamilies().add(newFam);
							brain.getFamilies().add(newFam.getFamID());

							return lang.getMsg(Langs.FAMILY_CREATED);
						} else {
							return "Use `family create <familyName>` to start a new Family using this server.\n" +
									"Please use only letters, numbers, and dashes in the name.";
						}
					}else{
						return lang.getError(Errors.NO_PERMISSION_USER, true);
					}
				case "add": // add famName|code
					if (arg1 != null) {
						String arg = arg1.trim().toLowerCase();

						for(Family f : Shmames.getBrains().getMotherBrain().getServerFamilies()){
							if(f.getFamilyOwner()==author.getIdLong() && f.getFamName().equalsIgnoreCase(arg)){
								if(f.getMemberGuilds().size() < 7) {
									author.openPrivateChannel().queue((c) -> c.sendMessage(lang.getMsg(Langs.FAMILY_JOIN_CODE, new String[] { f.getFamName(), f.getNewJoinCode() })).queue());

									return lang.getMsg(Langs.SENT_PRIVATE_MESSAGE);
								} else {
									return lang.getError(Errors.FAMILY_MEMBER_MAXIMUM_REACHED, true);
								}
							}

							if(f.validateCode(arg)){
								f.clearCode();

								if(message.getMember().hasPermission(Permission.ADMINISTRATOR) || Shmames.isDebug) {
									if(brain.getFamilies().size() < 3) {
										if (!brain.getFamilies().contains(f.getFamID())) {
											f.addToFamily(message.getGuild());
											brain.getFamilies().add(f.getFamID());

											return lang.getMsg(Langs.FAMILY_JOINED, new String[]{ message.getGuild().getName(), f.getFamName() });
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
					}else{
						return "Use `add <family name>` to get a new Join Code.\n" +
								"Use `add <join code>` to join a server to a Family.";
					}
				case "view": // [famName|emotes]
					if (arg1 != null) {
						String name = arg1.trim().toLowerCase();

						if(name.equalsIgnoreCase("emotes")){
							// Get all the emotes from all the servers in all the families
							EmbedBuilder embed = new EmbedBuilder();

							// This server first.
							addEmoteListField(embed, message.getGuild(), message.getTextChannel());

							for(String id : brain.getFamilies()){
								for(Family f : Shmames.getBrains().getMotherBrain().getServerFamilies()){
									if(f.getFamID().equals(id)) {
										for (long guildID : f.getMemberGuilds()) {
											if (message.getGuild().getIdLong() != guildID) { // Don't include this server
												Guild otherGuild = Shmames.getJDA().getGuildById(guildID);

												if(otherGuild != null) {
													addEmoteListField(embed, otherGuild, message.getTextChannel());
												}else{
													f.getMemberGuilds().remove(guildID);
												}
											}
										}

										break;
									}
								}
							}

							message.getChannel().sendMessage(embed.build()).queue();

							return "";
						}else{
							for(Family f : Shmames.getBrains().getMotherBrain().getServerFamilies()) {
								if ((f.getFamilyOwner() == author.getIdLong() || message.getMember().hasPermission(Permission.ADMINISTRATOR)) && f.getFamName().equalsIgnoreCase(name)) {
									StringBuilder sb = new StringBuilder();

									sb.append("**");
									sb.append(lang.getMsg(Langs.FAMILY_SERVER_LIST, new String[]{ f.getFamName() }));
									sb.append("**");
									sb.append("\n");

									boolean contains = false;
									List<String> memberGuilds = new ArrayList<String>();

									for(long g : new ArrayList<Long>(f.getMemberGuilds())){
										Guild guild = Shmames.getJDA().getGuildById(g);

										// Quick null check!
										if(guild == null){
											f.getMemberGuilds().remove(g);
											continue;
										}

										memberGuilds.add(guild.getName());
										contains = true;
									}

									if(contains) {
										sb.append(Utils.GenerateList(memberGuilds, -1, true));
									}else{
										sb.append("_");
										sb.append(Errors.FAMILY_SERVER_LIST_EMPTY);
										sb.append("_");
									}

									return sb.toString();
								}
							}

							return lang.getError(Errors.NOT_FOUND, true);
						}
					}else{
						if(message.getMember().hasPermission(Permission.ADMINISTRATOR) || Shmames.isDebug) {
							StringBuilder sb = new StringBuilder();
							sb.append("**");
							sb.append(lang.getMsg(Langs.SERVER_FAMILY_LIST));
							sb.append("**");
							sb.append("\n");

							boolean contains = false;
							List<String> families = new ArrayList<String>();

							for(String id : brain.getFamilies()){
								for(Family f : Shmames.getBrains().getMotherBrain().getServerFamilies()){
									if(f.getFamID().equals(id)){
										families.add(f.getFamName());
										contains = true;

										break;
									}
								}
							}

							if(contains) {
								sb.append(Utils.GenerateList(families, 3, false));
							}else{
								sb.append("_");
								sb.append(Errors.SERVER_FAMILY_LIST_EMPTY);
								sb.append("_");
							}

							return sb.toString();
						}else{
							return lang.getError(Errors.NO_PERMISSION_USER, true);
						}
					}
				case "remove": // <family> [server]
					if(arg1 != null) {
						String name = arg1.trim().toLowerCase();

						if (arg2 != null) {
							int server = Integer.parseInt(arg2.trim())-1;

							for (Family f : Shmames.getBrains().getMotherBrain().getServerFamilies()) {
								if (f.getFamilyOwner() == author.getIdLong() && f.getFamName().equalsIgnoreCase(name)) {
									if (f.getMemberGuilds().size() >= server) {
										Guild g = Shmames.getJDA().getGuildById(f.getMemberGuilds().get(server));
										String gName = "";

										if(g == null){
											f.getMemberGuilds().remove(f.getMemberGuilds().get(server));
											gName = "that server";
										}else{
											brain.getFamilies().remove(f.getFamID());
											f.getMemberGuilds().remove(g.getIdLong());
											gName = g.getName();
										}

										// Remove the family if empty
										if(f.getMemberGuilds().size() == 0){
											Shmames.getBrains().getMotherBrain().getServerFamilies().remove(f);
										}

										return lang.getMsg(Langs.FAMILY_REMOVED_SERVER, new String[]{ gName, f.getFamName() });
									} else {
										return lang.getError(Errors.FAMILY_NOT_JOINED, true);
									}
								}
							}

							return lang.getError(Errors.NOT_FOUND, true);
						} else {
							if(message.getMember().hasPermission(Permission.ADMINISTRATOR) || Shmames.isDebug) {
								for (Family f : Shmames.getBrains().getMotherBrain().getServerFamilies()) {
									if(f.getFamName().equalsIgnoreCase(name)){
										Guild g = message.getGuild();

										brain.getFamilies().remove(f.getFamID());
										f.getMemberGuilds().remove(g.getIdLong());

										// Remove the family if empty
										if(f.getMemberGuilds().size() == 0){
											Shmames.getBrains().getMotherBrain().getServerFamilies().remove(f);
										}

										return lang.getMsg(Langs.FAMILY_REMOVED_SERVER, new String[]{ g.getName(), f.getFamName() });
									}
								}

								return lang.getError(Errors.NOT_FOUND, true);
							}else{
								return lang.getError(Errors.NO_PERMISSION_USER, true);
							}
						}
					}else{
						return "Use `family remove <familyName>` to remove this server from a Family.\n" +
								"Use `family remove <familyName> [server number]` to remove another server from a Family.\n" +
								"_Find the server number using `family view <familyName>`_";
					}
				default:
					return lang.wrongUsage(getUsage());
			}
		}

		return lang.wrongUsage(getUsage());
	}

	@Override
	public String[] getAliases() {
		return new String[] {"family"};
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

	private void addEmoteListField(EmbedBuilder embed, Guild g, TextChannel channel) {
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

		String[] emoteLists = Utils.splitString(guildEmotes.toString(), MessageEmbed.VALUE_MAX_LENGTH);

		if((embed.length() + emoteLists[0].length()) > MessageEmbed.EMBED_MAX_LENGTH_BOT) {
			channel.sendMessage(embed.build()).complete();

			embed.clearFields();
		}

		embed.addField(g.getName(), emoteLists[0], false);

		if(emoteLists.length > 1) {
			for(int i=1; i<emoteLists.length; i++) {
				if((embed.length() + emoteLists[i].length()) > MessageEmbed.EMBED_MAX_LENGTH_BOT) {
					channel.sendMessage(embed.build()).complete();

					embed.clearFields();
				}

				embed.addField("", emoteLists[i], false);
			}
		}
	}
}
