package com.hadenwatne.discordbot.commands;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hadenwatne.discordbot.storage.Lang;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import com.hadenwatne.discordbot.Shmames;
import com.hadenwatne.discordbot.Utils;
import com.hadenwatne.discordbot.storage.BotSettingName;
import com.hadenwatne.discordbot.storage.Brain;
import com.hadenwatne.discordbot.storage.MotherBrain;

import javax.annotation.Nullable;

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
				Matcher m = Pattern.compile("^([a-z]+)\\s?(.+)?$", Pattern.CASE_INSENSITIVE).matcher(args);

				if(m.find()) {
					switch(m.group(1).toLowerCase()){
						case "addstatus":
							return addStatus(m.group(2)) ? "Status change successful!" : "Invalid syntax or bot error.";
						case "getguilds":
							return getGuilds();
						case "createapikey":
							return createAPIKey();
						case "reload":
							reload(m.group(2));
							return "Finished reloading the brain!";
						case "inviteme":
							return inviteMe(m.group(2));
						case "getcommandstats":
							return getCommandStats();
						case "clearcommandstats":
							clearCommandStats();
							return "Command statistics cleared!";
						case "announce":
							return announce(m.group(2), message.getAuthor());
						case "leave":
							return leave(m.group(2)) ? "Successfully left the server!" : "Could not leave that server.";
						case "getreports":
							getReports(message.getChannel());
							return "";
						case "savebrains":
							saveBrains();
							return "All brains were saved!";
						default:
							return "That command wasn't recognized!";
					}
				} else {
					return "**Developer Commands**\n"
							+ "addStatus <type> <status>\n"
							+ "getGuilds\n"
							+ "createapikey\n"
							+ "reload <guildID>\n"
							+ "inviteme <guildID>\n"
							+ "getCommandStats\n"
							+ "clearCommandStats\n"
							+ "announce <message>\n"
							+ "leave <guildID>\n"
							+ "getReports\n"
							+ "saveBrains\n";
				}
			} else {
				return "You cannot use the Developer command! This is used for bot maintenance tasks, and is restricted" +
						"to the bot developer.";
			}
		}
		
		return null;
	}

	@Override
	public String[] getAliases() {
		return new String[] {"developer"};
	}

	@Override
	public void setRunContext(Lang lang, @Nullable Brain brain) {

	}
	
	@Override
	public boolean requiresGuild() {
		return false;
	}

	private boolean addStatus(String args) {
		Matcher m = Pattern.compile("^([a-z]+)\\s(.+)$", Pattern.CASE_INSENSITIVE).matcher(args);

		if(m.find()){
			try {
				MotherBrain b = Shmames.getBrains().getMotherBrain();
				ActivityType type = ActivityType.valueOf(m.group(1).toUpperCase());
				String msg = m.group(2);

				b.getStatuses().put(msg, type);
				Shmames.getJDA().getPresence().setActivity(Activity.of(type, msg));
				Shmames.getBrains().saveMotherBrain();

				return true;
			}catch (Exception e){
				return false;
			}
		}else{
			return false;
		}
	}

	private String getGuilds() {
		StringBuilder sb = new StringBuilder();

		for(Guild g : Shmames.getJDA().getGuilds()) {
			if(sb.length() > 0)
				sb.append("\n");

			sb.append("> ");
			sb.append(g.getName());
			sb.append(" (");
			sb.append(g.getId());
			sb.append(" )");
		}

		sb.insert(0, "**Guilds the bot runs on**\n");

		return sb.toString();
	}

	private String createAPIKey() {
		StringBuilder key = new StringBuilder();

		for(int i=0; i<3; i++){
			key.append(Utils.createID());
		}

		String newKey = key.toString();

		Shmames.getBrains().getMotherBrain().getShmamesAPIKeys().add(newKey);

		return newKey;
	}

	private void reload(String g) {
		Shmames.getBrains().reloadBrain(g);
	}

	private String inviteMe(String g) {
		Guild guild = Shmames.getJDA().getGuildById(g);

		if(guild != null) {
			List<TextChannel> tc = guild.getTextChannels();

			for (TextChannel c : tc) {
				try {
					String code = c.createInvite().complete().getCode();

					return "Here you go! " + code;
				} catch (Exception ignored) {}
			}

			return "Guess I'm not allowed to do that...";
		}else{
			return "Guild not found!";
		}
	}

	private String getCommandStats() {
		StringBuilder answer = new StringBuilder("**Command Usage Statistics**");

		// Sort
		LinkedHashMap<String, Integer> cmdStats = Utils.sortHashMap(Shmames.getBrains().getMotherBrain().getCommandStats());

		for(String c : cmdStats.keySet()) {
			answer.append("\n");
			answer.append("`").append(c).append("`: ").append(cmdStats.get(c));
		}

		return answer.toString();
	}

	private void clearCommandStats() {
		Shmames.getBrains().getMotherBrain().getCommandStats().clear();
	}

	private String announce(String msg, User a) {
		EmbedBuilder eBuilder = new EmbedBuilder();
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());

		eBuilder.setAuthor(a.getName(), null, a.getEffectiveAvatarUrl());
		eBuilder.setColor(Color.RED);
		eBuilder.setTitle("\u2699 Developer Note");
		eBuilder.appendDescription(msg);
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
					g.getTextChannelById(channel).sendMessage(embed).complete();
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
	}

	private boolean leave(String gid) {
		for(Guild g : Shmames.getJDA().getGuilds()) {
			if(g.getId().equals(gid)) {
				g.leave().queue();

				return true;
			}
		}

		return false;
	}

	private void getReports(MessageChannel c) {
		StringBuilder reports = new StringBuilder("== User Reports ==");

		// Build list of reports.
		for(Guild g : Shmames.getJDA().getGuilds()) {
			Brain b = Shmames.getBrains().getBrain(g.getId());

			if(b.getFeedback().size()>0) {
				reports.append("\n");
				reports.append("== ").append(g.getName()).append(" ==");

				for(String r : b.getFeedback()) {
					reports.append("\n");
					reports.append(r);
				}
			}
		}

		// Save to file.
		File dir = new File("reports");
		File f = new File("reports/"+System.currentTimeMillis()+".txt");
		dir.mkdirs();

		try {
			f.createNewFile();

			FileOutputStream fo = new FileOutputStream(f);
			fo.write(reports.toString().getBytes());
			fo.flush();
			fo.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Send to me.
		c.sendFile(f).complete();

		// Delete report on disk.
		f.delete();

		// Clear out guild feedback.
		for(Guild g : Shmames.getJDA().getGuilds()) {
			Brain b = Shmames.getBrains().getBrain(g.getId());

			b.getFeedback().clear();
		}
	}

	private void saveBrains() {
		for(Brain b : Shmames.getBrains().getBrains()) {
			Shmames.getBrains().saveBrain(b);
		}

		Shmames.getBrains().saveMotherBrain();
	}
}