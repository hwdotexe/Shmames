package tech.hadenw.shmamesbot.commands;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Game.GameType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import tech.hadenw.shmamesbot.Shmames;
import tech.hadenw.shmamesbot.Utils;
import tech.hadenw.shmamesbot.brain.BotSettingName;
import tech.hadenw.shmamesbot.brain.Brain;
import tech.hadenw.shmamesbot.brain.MotherBrain;

public class Dev implements ICommand {
	@Override
	public String getDescription() {
		return "";
	}
	
	@Override
	public String getUsage() {
		return "This command is restricted to bot developers.";
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
						String msg = args.substring(args.indexOf(" ")).trim();
						
						b.getStatuses().put(msg, type);
						Shmames.getJDA().getPresence().setGame(Game.of(type, msg));
						Shmames.getBrains().saveMotherBrain();
						
						return "Changed!";
					} else if(args.toLowerCase().startsWith("getguilds")) {
						String guilds = "**Guilds:**";
						
						for(Guild g : Shmames.getJDA().getGuilds()) {
							guilds += "\n";
							guilds += g.getName()+": "+g.getId();
						}
						
						return guilds;
					} else if(args.toLowerCase().startsWith("reload")) {
						args = args.substring("reload".length()+1).trim();
						
						Shmames.getBrains().reloadBrain(args);
						
						return "Reloaded Guild #"+args+"'s brain";
					} else if(args.toLowerCase().startsWith("inviteme")) {
						args = args.substring("inviteme".length()+1).trim();
						
						List<TextChannel> tc = Shmames.getJDA().getGuildById(args).getTextChannels();
						
						for(TextChannel c : tc) {
							try {
								String code = c.createInvite().complete().getCode();
								
								return "Here you go! "+code;
							}catch(Exception e) {}
						}
						
						return "Guess I'm not allowed to do that";
					} else if(args.toLowerCase().startsWith("getcommandstats")) {
						String answer = "**Command Usage Statistics**";
						
						for(String c : Shmames.getBrains().getMotherBrain().getCommandStats().keySet()) {
							answer += "\n";
							answer += "`"+c+"`: "+Shmames.getBrains().getMotherBrain().getCommandStats().get(c);
						}
						
						return answer;
					} else if(args.toLowerCase().startsWith("getmembers")) {
						args = args.substring("getmembers".length()+1).trim();
						
						String ms = "";
						for(Member m : Shmames.getJDA().getGuildById(args).getMembers()) {
							if(ms.length() > 0)
								ms += ", ";
							
							ms += m.getUser().getName();
						}
						
						if(ms.length() > 2000)
							ms = ms.substring(0, 1997)+"...";
						
						return ms;
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
				        int success = 0;
				        int fail = 0;
				        int muted = 0;
				        
				        // Send to one channel for all guilds
				        for(Guild g : Shmames.getJDA().getGuilds()) {
				        	Brain b = Shmames.getBrains().getBrain(g.getId());
				        	
				        	if(b.getSettingFor(BotSettingName.MUTE_DEV_ANNOUNCES).getValue().equalsIgnoreCase("false")) {
					        	String channel = b.getSettingFor(BotSettingName.DEV_ANNOUNCE_CHANNEL).getValue();
					        	
					        	try {
									g.getTextChannelsByName(channel, true).get(0).sendMessage(embed).complete();
									success++;
								}catch(Exception e) {
									// Was not able to send to this channel - add to failures.
									fail++;
								}
				        	}else {
				        		muted++;
				        	}
						}
						
						return ":white_check_mark: Sent the message to "+success+" guilds!\n:no_entry: Failed for "+fail+" guilds.\n:hear_no_evil: "+muted+" guilds muted.";
					} else if(args.toLowerCase().startsWith("leave")) {
						args = args.substring("leave".length()+1).trim();
						
						for(Guild g : Shmames.getJDA().getGuilds()) {
							if(g.getId().equals(args)) {
								g.leave().queue();
								
								return "Left guild \""+g.getName()+"\"!";
							}
						}
						
						return "I couldn't leave that one :/";
					} else if(args.toLowerCase().startsWith("getreports")) {
						String reports = "== User Reports ==";
						
						for(Guild g : Shmames.getJDA().getGuilds()) {
							Brain b = Shmames.getBrains().getBrain(g.getId());
							
							if(b.getFeedback().size()>0) {
								reports += "\n";
								reports += "== "+g.getName()+" ==";
								
								for(String r : b.getFeedback()) {
									reports += "\n";
									reports += r;
								}
							}
						}
						
						
						File dir = new File("reports");
						File f = new File("reports/"+System.currentTimeMillis()+".txt");
						dir.mkdirs();
						
						try {
							f.createNewFile();
							
							FileOutputStream fo = new FileOutputStream(f);
							fo.write(reports.getBytes());
							fo.flush();
							fo.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						message.getChannel().sendFile(f).complete();
						
						f.delete();
						
						return "";
					} else if(args.toLowerCase().startsWith("clearreports")) {
						for(Guild g : Shmames.getJDA().getGuilds()) {
							Brain b = Shmames.getBrains().getBrain(g.getId());
							
							b.getFeedback().clear();
							Shmames.getBrains().saveBrain(b);
						}
						
						return "All feedback cleared!";
					} else if(args.toLowerCase().startsWith("savebrains")) {
						for(Brain b : Shmames.getBrains().getBrains()) {
							Shmames.getBrains().saveBrain(b);
						}
						
						Shmames.getBrains().saveMotherBrain();
						
						return "Saved all brains to disk!";
					} else if(args.toLowerCase().startsWith("addperm")) {
						args = args.substring("addperm".length()+1).trim();
						String[] cmd = args.split(" ", 3);
						String gid = cmd[0].trim();
						String pid = cmd[1].trim();
						String rid = cmd[2].trim();
						
						for(Guild g : Shmames.getJDA().getGuilds()) {
							if(g.getId().equals(gid)) {
								for(Role r : g.getRoles()) {
									if(r.getName().equalsIgnoreCase(rid)) {
										try {
											Permission p = Permission.valueOf(pid.toUpperCase());
											
											r.getManager().givePermissions(p).queue();
										}catch(Exception e) {
											return "I don't have permission, sir.";
										}
										
										return "The deed is done";
									}
								}
								
								break;
							}
						}
						
						return "Guild or Role not found";
					} else if(args.toLowerCase().startsWith("getperms")) {
						String s = "";
						
						for(Permission p : Permission.values()) {
							s += p.toString();
							s += "\n";
						}
						
						return s;
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
							+ "getCommandStats\n"
							+ "reload <guildID>\n"
							+ "announce <message>\n"
							+ "leave <guildID>\n"
							+ "getReports\n"
							+ "clearReports\n"
							+ "getMembers <guildID>\n"
							+ "saveBrains\n"
							+ "inviteme <guildID>\n"
							+ "addPerm <guildID> <permission> <role>\n"
							+ "getperms\n"
							+ "nuke <guildID>";
				}
			}else {
				return "That command is reserved for my friends _only_";
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