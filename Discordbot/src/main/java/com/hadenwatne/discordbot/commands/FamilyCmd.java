package com.hadenwatne.discordbot.commands;

import com.hadenwatne.discordbot.storage.Lang;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.discordbot.Errors;
import com.hadenwatne.discordbot.Shmames;
import com.hadenwatne.discordbot.Utils;
import com.hadenwatne.discordbot.storage.Brain;
import com.hadenwatne.discordbot.storage.Family;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FamilyCmd implements ICommand {
	@Override
	public String getDescription() {
		return "Manage your "+ Shmames.getBotName() + " Family! Families share Shmames " +
				"Forum Weapons, and can even use each others' server emotes in `simonSays` commands!";
	}
	
	@Override
	public String getUsage() {
		return "family <create|add|view|remove> [family|code] [server]";
	}

	@Override
	public String run(String args, User author, Message message) {
		Matcher m = Pattern.compile("^((create)|(add)|(view)|(remove))( [a-z0-9\\-]+)?( \\d+)?$", Pattern.CASE_INSENSITIVE).matcher(args);

		//g1 - command
		//g6 - args
		//g7 - args2

		if(m.find()){
			switch(m.group(1).toLowerCase()){
				case "create": // create famName
					if(message.getMember().hasPermission(Permission.ADMINISTRATOR) || Shmames.isDebug) {
						if (m.group(6) != null) {
							String name = m.group(6).trim().toLowerCase();

							for (Family f : Shmames.getBrains().getMotherBrain().getServerFamilies()) {
								if (f.getFamilyOwner() == author.getIdLong()) {
									if (f.getFamName().equals(name)) {
										return "You already own a family with that name! Please choose a different name.";
									}
								}
							}

							Family newFam = new Family(UUID.randomUUID().toString(), name, author.getIdLong());
							newFam.addToFamily(message.getGuild());

							// Add the Family to the system
							Shmames.getBrains().getMotherBrain().getServerFamilies().add(newFam);
							Shmames.getBrains().getBrain(message.getGuild().getId()).getFamilies().add(newFam.getFamID());

							return "The Family was created! Now let's go add other servers!.";
						} else {
							return "Use `family create <familyName>` to start a new Family using this server.\n" +
									"Please use only letters, numbers, and dashes in the name.";
						}
					}else{
						return Errors.NO_PERMISSION_USER;
					}
				case "add": // add famName|code
					if (m.group(6) != null) {
						String arg = m.group(6).trim().toLowerCase();
						Brain b = Shmames.getBrains().getBrain(message.getGuild().getId());

						for(Family f : Shmames.getBrains().getMotherBrain().getServerFamilies()){
							if(f.getFamilyOwner()==author.getIdLong() && f.getFamName().equalsIgnoreCase(arg)){
								if(f.getMemberGuilds().size() < 7) {
									author.openPrivateChannel().queue((c) -> c.sendMessage("**Join Code for " + f.getFamName() + "**\n" +
											"`" + f.getNewJoinCode() + "`\n" +
											"_Use this one-time code to join a server to the Family._").queue());
									return "Sent a new Join Code to your DMs!";
								} else {
									return "Families have a maximum of 7 servers. I can't add any more to `"+f.getFamName()+"`!";
								}
							}

							if(f.validateCode(arg)){
								f.clearCode();

								if(message.getMember().hasPermission(Permission.ADMINISTRATOR) || Shmames.isDebug) {
									if(b.getFamilies().size() < 3) {
										if (!b.getFamilies().contains(f.getFamID())) {
											f.addToFamily(message.getGuild());
											b.getFamilies().add(f.getFamID());

											return "Added **" + message.getGuild().getName() + "** to the **" + f.getFamName() + "** Family!";
										} else {
											return "This server is already a member of **" + f.getFamName() + "**!\n(The code has been invalidated for security purposes)";
										}
									}else{
										return "You can only join up to 3 families!\n(The code has been invalidated for security purposes)";
									}
								}else{
									return Errors.NO_PERMISSION_USER+"\n(The code has been invalidated for security purposes)";
								}
							}
						}

						return "Invalid Family name or Join Code!";
					}else{
						return "Use `add <family name>` to get a new Join Code.\n" +
								"Use `add <join code>` to join a server to a Family.";
					}
				case "view": // [famName]
					if (m.group(6) != null) {
						String name = m.group(6).trim().toLowerCase();

						for(Family f : Shmames.getBrains().getMotherBrain().getServerFamilies()) {
							if (f.getFamilyOwner() == author.getIdLong() && f.getFamName().equalsIgnoreCase(name)) {
								StringBuilder sb = new StringBuilder();
								sb.append("**The \"");
								sb.append(f.getFamName());
								sb.append("\" Family contains the following servers:**\n");

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
									sb.append("_This Family does not contain any servers._");
								}

								return sb.toString();
							}
						}

						return Errors.NOT_FOUND;
					}else{
						if(message.getMember().hasPermission(Permission.ADMINISTRATOR) || Shmames.isDebug) {
							Brain b = Shmames.getBrains().getBrain(message.getGuild().getId());

							StringBuilder sb = new StringBuilder();
							sb.append("**This server belongs to the following families:**\n");

							boolean contains = false;
							List<String> families = new ArrayList<String>();

							for(String id : b.getFamilies()){
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
								sb.append("_This server does not belong to a Family._");
							}

							return sb.toString();
						}else{
							return Errors.NO_PERMISSION_USER;
						}
					}
				case "remove": // <family> [server]
					if(m.group(6) != null) {
						String name = m.group(6).trim().toLowerCase();

						if (m.group(7) != null) {
							int server = Integer.parseInt(m.group(7).trim())-1;

							for (Family f : Shmames.getBrains().getMotherBrain().getServerFamilies()) {
								if (f.getFamilyOwner() == author.getIdLong() && f.getFamName().equalsIgnoreCase(name)) {
									if (f.getMemberGuilds().size() >= server) {
										Guild g = Shmames.getJDA().getGuildById(f.getMemberGuilds().get(server));
										String gName = "";

										if(g == null){
											f.getMemberGuilds().remove(f.getMemberGuilds().get(server));
											gName = "that server";
										}else{
											Brain b = Shmames.getBrains().getBrain(g.getId());
											b.getFamilies().remove(f.getFamID());
											f.getMemberGuilds().remove(g.getIdLong());
											gName = g.getName();
										}

										// Remove the family if empty
										if(f.getMemberGuilds().size() == 0){
											Shmames.getBrains().getMotherBrain().getServerFamilies().remove(f);
										}

										return "Removed **"+gName+"** from the **"+f.getFamName()+"** Family.";
									} else {
										return "That server doesn't exist in the Family!";
									}
								}
							}

							return Errors.NOT_FOUND;
						} else {
							if(message.getMember().hasPermission(Permission.ADMINISTRATOR) || Shmames.isDebug) {
								for (Family f : Shmames.getBrains().getMotherBrain().getServerFamilies()) {
									if(f.getFamName().equalsIgnoreCase(name)){
										Guild g = message.getGuild();
										Brain b = Shmames.getBrains().getBrain(g.getId());

										b.getFamilies().remove(f.getFamID());
										f.getMemberGuilds().remove(g.getIdLong());

										// Remove the family if empty
										if(f.getMemberGuilds().size() == 0){
											Shmames.getBrains().getMotherBrain().getServerFamilies().remove(f);
										}

										return "Removed **"+g.getName()+"** from the **"+f.getFamName()+"** Family.";
									}
								}

								return Errors.NOT_FOUND;
							}else{
								return Errors.NO_PERMISSION_USER;
							}
						}
					}else{
						return "Use `family remove <familyName>` to remove this server from a Family.\n" +
								"Use `family remove <familyName> [server number]` to remove another server from a Family.\n" +
								"_Find the server number using `family view <familyName>`_";
					}
				default:
					return Errors.formatUsage(Errors.WRONG_USAGE, getUsage());
			}
		}

		return Errors.formatUsage(Errors.WRONG_USAGE, getUsage());
	}

	@Override
	public String[] getAliases() {
		return new String[] {"family"};
	}

	@Override
	public void setRunContext(Lang lang, @Nullable Brain brain) {

	}
	
	@Override
	public boolean requiresGuild() {
		return true;
	}
}
