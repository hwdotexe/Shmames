package com.hadenwatne.discordbot.commands;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hadenwatne.discordbot.Utils;
import com.hadenwatne.discordbot.storage.*;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import com.hadenwatne.discordbot.Errors;
import com.hadenwatne.discordbot.GuildOcarina;
import com.hadenwatne.discordbot.Shmames;

import javax.annotation.Nullable;

public class Music implements ICommand {
	@Override
	public String getDescription() {
		return "Play music, create playlists, and chill out.";
	}
	
	@Override
	public String getUsage() {
		return "music";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"music", "bops"};
	}

	@Override
	public String sanitize(String i) {
		return i;
	}

	@Override
	public boolean requiresGuild() {
		return true;
	}

	@Override
	public String run(String args, User author, Message message) {
		Matcher m = Pattern.compile("^([a-z]+)\\s?(.+)?$", Pattern.CASE_INSENSITIVE).matcher(args);

		if(m.find()){
			String mainCmd = m.group(1);
			GuildOcarina ocarina = Shmames.getMusicManager().getOcarina(message.getGuild().getId());
			Brain b = Shmames.getBrains().getBrain(message.getGuild().getId());

			switch(mainCmd) {
				case "play":
					if (canUse(b, message.getMember())) {
						return cmdPlay(b, message.getMember(), message.getTextChannel(), ocarina, m.group(2));
					} else {
						return Errors.NO_PERMISSION_USER;
					}
				case "pause":
					ocarina.togglePause(true);
					break;
				case "resume":
					ocarina.togglePause(false);
					break;
				case "skip":
					if(ocarina.getNowPlaying() != null) {
						if (canUse(b, message.getMember())) {
							ocarina.skip();
						} else {
							return Errors.NO_PERMISSION_USER;
						}
					}else{
						return Errors.TRACK_NOT_PLAYING;
					}
					break;
				case "shuffle":
					if(ocarina.getNowPlaying() != null) {
						if (canUse(b, message.getMember())) {
							ocarina.shuffleQueue();

							return "Shuffled the music queue!";
						} else {
							return Errors.NO_PERMISSION_USER;
						}
					}else{
						return Errors.TRACK_NOT_PLAYING;
					}
				case "stop":
					if(ocarina.getNowPlaying() != null) {
						if (canUse(b, message.getMember())) {
							ocarina.stop();
						} else {
							return Errors.NO_PERMISSION_USER;
						}
					}else{
						return Errors.TRACK_NOT_PLAYING;
					}
					break;
				case "loop":
					if (canUse(b, message.getMember())) {
						boolean isLoop = ocarina.toggleLoop();
						return "Music looping is now **" + (isLoop ? "ON" : "OFF") + "**";
					}else{
						return Errors.NO_PERMISSION_USER;
					}
				case "np":
				case "playing":
					AudioTrack track = ocarina.getNowPlaying();

					if(track != null){
						showTrackData(track, message.getChannel(), ocarina);
					}else{
						return Errors.TRACK_NOT_PLAYING;
					}

					break;
				case "pl":
				case "playlist":
					if (canUse(b, message.getMember())) {
						return cmdPlaylist(b, message.getChannel(), m.group(2));
					}else{
						return Errors.NO_PERMISSION_USER;
					}
				case "q":
				case "queue":
					if (canUse(b, message.getMember())) {
						return cmdQueue(b, message.getMember(), ocarina, message.getChannel(), m.group(2));
					} else {
						return Errors.NO_PERMISSION_USER;
					}
				case "convert":
					if (canUse(b, message.getMember())) {
						return cmdConvert(b, message.getMember(), ocarina, m.group(2));
					} else {
						return Errors.NO_PERMISSION_USER;
					}
				default:
					return Errors.formatUsage(Errors.WRONG_USAGE, getUsage());
			}
		}else{
			sendMusicCmdHelp(message.getChannel());

			return "";
		}

		return "";
	}

	private String cmdPlay(Brain b, Member m, TextChannel c, GuildOcarina ocarina, @Nullable String args) {
		if (args != null) {
			if (!ocarina.isInVoiceChannel()) {
				if (m.getVoiceState() != null && m.getVoiceState().inVoiceChannel()) {
					VoiceChannel vchannel = m.getVoiceState().getChannel();

					ocarina.connect(vchannel, c);
				} else {
					return "Please join a voice channel and run this command again.";
				}
			}

			if (isUrl(args)) {
				ocarina.loadTrack(args, false);

				return "Playing!";
			} else {
				Playlist pl = findPlaylist(args, b);

				if(pl != null) {
					ocarina.loadCustomPlaylist(pl.getTracks(), false);

					return "Playing the `" + pl.getName() + "` playlist!";
				} else {
					return "No playlists were found with that name.";
				}
			}
		} else {
			if (ocarina.isPaused()) {
				ocarina.togglePause(false);

				return "";
			} else {
				return "Please enter a media URL or playlist name!";
			}
		}
	}

	private String cmdQueue(Brain b, Member m, GuildOcarina ocarina, MessageChannel c, @Nullable String args) {
		if (ocarina.isInVoiceChannel()) {
			if (args != null) {
				if (isUrl(args)) {
					ocarina.loadTrack(args, true);

					return "Added to queue!";
				} else if (args.equalsIgnoreCase("clear")) {
					ocarina.getQueue().clear();

					return "Cleared the queue!";
				} else if (isInt(args)) {
					showQueue(ocarina.getQueue(), c, Integer.parseInt(args));

					return "";
				} else {
					Playlist pl = findPlaylist(args, b);

					if(pl != null) {
						ocarina.loadCustomPlaylist(pl.getTracks(), true);

						return "Queued the `" + pl.getName() + "` playlist!";
					} else {
						return "No playlists were found with that name.";
					}
				}
			} else {
				showQueue(ocarina.getQueue(), c, 1);

				return "";
			}
		} else {
			return "I have to be connected to a voice channel in order to do that!";
		}
	}

	private String cmdConvert(Brain b, Member m, GuildOcarina ocarina, @Nullable String args) {
		if (ocarina.getNowPlaying() != null) {
			if (args != null) {
				Matcher conv = Pattern.compile("^([a-z0-9]+)$", Pattern.CASE_INSENSITIVE).matcher(args);

				if (conv.find()) {
					String name = conv.group(1).toLowerCase();

					if (getPlaylist(name, b) == null) {
						Playlist p = new Playlist(name);

						p.addTrack(ocarina.getNowPlaying().getInfo().uri, ocarina.getNowPlaying().getInfo().title);

						for (AudioTrack t : ocarina.getQueue()) {
							if (p.getTracks().size() < 50) {
								p.addTrack(t.getInfo().uri, t.getInfo().title);
							}
						}

						b.getPlaylists().add(p);

						return "Created a new playlist `" + name + "` with `" + p.getTracks().size() + "` tracks!";
					} else {
						return "A playlist with that name already exists on this server!";
					}
				} else {
					return "Playlist names must be alphanumeric!";
				}
			} else {
				return "Please enter a name for the new playlist.";
			}
		} else {
			return Errors.TRACK_NOT_PLAYING;
		}
	}

	private String cmdPlaylist(Brain b, MessageChannel c, @Nullable String args) {
		if(args != null) {
			Matcher m = Pattern.compile("^([a-z]+)\\s?(.+)?$", Pattern.CASE_INSENSITIVE).matcher(args);

			if (m.find()) {
				String subCmd = m.group(1).toLowerCase();
				String subArgs = m.group(2) != null ? m.group(2) : "";

				switch (subCmd) {
					case "c":
					case "create":
						Matcher create = Pattern.compile("^([a-z0-9_]+)\\s?(https?://[./\\w\\d-_&?=*%]+)?\\s?(.+)?$", Pattern.CASE_INSENSITIVE).matcher(subArgs);

						if (create.find()) {
							return cmdPlaylistCreate(b, create.group(1), create.group(2), create.group(3));
						} else {
							return Errors.WRONG_USAGE;
						}
					case "a":
					case "add":
						Matcher add = Pattern.compile("^([a-z0-9_]+)\\s(https?://[./\\w\\d-_&?=*%]+)\\s?(.+)?$", Pattern.CASE_INSENSITIVE).matcher(subArgs);

						if (add.find()) {
							return cmdPlaylistAdd(b, add.group(1), add.group(2), add.group(3));
						} else {
							return Errors.WRONG_USAGE;
						}
					case "l":
					case "list":
						Matcher list = Pattern.compile("^([a-z0-9_]+)?\\s?(\\d{1,3})?$", Pattern.CASE_INSENSITIVE).matcher(subArgs);

						if (list.find()) {
							return cmdPlaylistList(b, list.group(1), c, list.group(2));
						} else {
							return Errors.WRONG_USAGE;
						}
					case "r":
					case "remove":
						Matcher remove = Pattern.compile("^([a-z0-9_]+)\\s(\\d{1,2})$", Pattern.CASE_INSENSITIVE).matcher(subArgs);

						if (remove.find()) {
							String listName = remove.group(1);
							int position = Integer.parseInt(remove.group(2));
							Playlist pRemove = getPlaylist(listName, b);

							if (pRemove != null) {
								if (pRemove.removeTrack(position - 1)) {
									return "Track removed!";
								} else {
									return "That item doesn't exist!";
								}
							} else {
								return "That playlist doesn't exist!";
							}
						} else {
							return Errors.WRONG_USAGE;
						}
					case "d":
					case "delete":
						Matcher delete = Pattern.compile("^([a-z0-9_]+)$", Pattern.CASE_INSENSITIVE).matcher(subArgs);

						if (delete.find()) {
							String listName = delete.group(1);
							Playlist pDelete = getPlaylist(listName, b);

							if (pDelete != null) {
								b.getPlaylists().remove(pDelete);
								return "Playlist deleted!";
							} else {
								return "That playlist doesn't exist!";
							}
						} else {
							return Errors.WRONG_USAGE;
						}
					default:
						return Errors.COMMAND_NOT_FOUND;
				}
			} else {
				return Errors.WRONG_USAGE;
			}
		} else {
			sendPlaylistCmdHelp(c);

			return "";
		}
	}

	private String cmdPlaylistCreate(Brain b, String listName, @Nullable String url, @Nullable String memo) {
		if(getPlaylist(listName, b) == null) {
			Playlist newList = new Playlist(listName);

			if(url != null) {
				newList.addTrack(url, memo);
			}

			b.getPlaylists().add(newList);

			return "Playlist `"+listName+"` created!";
		} else {
			return "A playlist with that name already exists on this server!";
		}
	}

	private String cmdPlaylistAdd(Brain b, String listName, String url, @Nullable String memo) {
		Playlist p = getPlaylist(listName, b);

		if (p != null) {
			if(p.getTracks().size() < 50) {
				p.addTrack(url, memo);

				return "Added track to playlist!";
			}else{
				return "Playlists currently support a max of 50 tracks!";
			}
		} else {
			return Errors.NOT_FOUND;
		}
	}

	private String cmdPlaylistList(Brain b, @Nullable String listName, MessageChannel c, @Nullable String p) {
		if(listName != null) {
			if(listName.equalsIgnoreCase("all")) {
				// List families' playlists
				StringBuilder sb = new StringBuilder();
				EmbedBuilder eBuilder = buildBasicEmbed();

				// Add this server's to the embed
				for (Playlist pl : b.getPlaylists()) {
					if (sb.length() > 0)
						sb.append(", ");

					sb.append("`");
					sb.append(pl.getName());
					sb.append("`");
				}

				if (sb.length() == 0) {
					sb.append("There aren't any playlists yet.");
				}

				eBuilder.addField(Shmames.getJDA().getGuildById(b.getGuildID()).getName(), sb.toString(), false);

				// Then add each server in our family's playlists
				for(String fid : b.getFamilies()) {
					Family f = Shmames.getBrains().getMotherBrain().getFamilyByID(fid);

					if(f != null){
						for(long gid : f.getMemberGuilds()){
							if(!Long.toString(gid).equals(b.getGuildID())){
								Brain ob = Shmames.getBrains().getBrain(Long.toString(gid));
								StringBuilder obsb = new StringBuilder();

								for (Playlist pl : ob.getPlaylists()) {
									if (obsb.length() > 0)
										obsb.append(", ");

									obsb.append("`");
									obsb.append(pl.getName());
									obsb.append("`");
								}

								if (obsb.length() == 0) {
									obsb.append("There aren't any playlists yet.");
								}

								eBuilder.addField(Shmames.getJDA().getGuildById(ob.getGuildID()).getName(), obsb.toString(), false);
							}
						}
					}
				}

				c.sendMessage(eBuilder.build()).queue();

				return "";
			}else{
				Playlist pList = getPlaylist(listName, b);

				if (pList != null) {
					int page = p != null ? Math.max(Integer.parseInt(p), 1) : 1;

					showList(pList, c, page);

					return "";
				} else {
					return Errors.NOT_FOUND;
				}
			}
		}

		// Just list out this server's playlists
		StringBuilder sb = new StringBuilder();
		EmbedBuilder eBuilder = buildBasicEmbed();

		for (Playlist pl : b.getPlaylists()) {
			if (sb.length() > 0)
				sb.append(", ");

			sb.append("`");
			sb.append(pl.getName());
			sb.append("`");
		}

		if (sb.length() == 0) {
			sb.append("There aren't any playlists yet.");
		}

		eBuilder.addField("Playlists", sb.toString(), false);
		c.sendMessage(eBuilder.build()).queue();

		return "";
	}

	private Playlist getPlaylist(String name, Brain b) {
		if(name.length() > 0) {
			for (Playlist p : b.getPlaylists()) {
				if (p.getName().equalsIgnoreCase(name)) {
					return p;
				}
			}
		}

		return null;
	}

	private void showTrackData(AudioTrack t, MessageChannel c, GuildOcarina o) {
		EmbedBuilder eBuilder = buildBasicEmbed();
		String videoID = extractVideoID(t.getInfo().uri);

		if(videoID != null) {
			eBuilder.setThumbnail("http://img.youtube.com/vi/"+videoID+"/1.jpg");
		}

		eBuilder.setTitle(t.getInfo().title, t.getInfo().uri);
		eBuilder.addField("Looping", o.isLooping() ? "Yes" : "No", true);
		eBuilder.addField("Position", getHumanTimeCode(t.getPosition()) + " / " + getHumanTimeCode(t.getDuration()), true);

		c.sendMessage(eBuilder.build()).queue();
	}

	private void showQueue(List<AudioTrack> queue, MessageChannel c, int page) {
		// Build the page.
		StringBuilder sb = new StringBuilder();
		page = Math.max(page, 1)-1;
		int totalPages = (int)Math.ceil(queue.size()/10d);

		for(int i=(page*10); i<(page*10)+10; i++) {
			if(queue.size() > i) {
				if(sb.length() > 0) {
					sb.append("\n");
				}

				sb.append(i + 1);
				sb.append(": ");
				sb.append("`");
				sb.append(queue.get(i).getInfo().title);
				sb.append("`");
			}
		}

		EmbedBuilder eBuilder = buildBasicEmbed();

		if(sb.length() == 0) {
			if(page == 0) {
				sb.append("There are no tracks in the queue.");
			} else {
				sb.append("There are no tracks in the queue on this page.");
			}
		}

		if ((page+1) > totalPages) {
			eBuilder.setTitle("Music Queue");
		} else {
			eBuilder.setTitle("Music Queue (page "+(page+1)+" of "+totalPages+")");
		}

		eBuilder.addField("Up Next", sb.toString(), false);

		c.sendMessage(eBuilder.build()).queue();
	}

	private void showList(Playlist playlist, MessageChannel c, int page) {
		// Build the page.
		StringBuilder sb = new StringBuilder();
		page = Math.max(page, 1)-1;
		int perPage = 7;
		int totalPages = (int)Math.ceil(playlist.getTracks().size()/(double)perPage);

		for(int i=(page*perPage); i<(page*perPage)+perPage; i++) {
			if(playlist.getTracks().size() > i) {
				if(sb.length() > 0) {
					sb.append("\n");
				}

				sb.append(i + 1);
				sb.append(": ");

				String url = playlist.getTracks().get(i);
				String memo = playlist.getMemo(url);

				if (memo != null && memo.length() > 0) {
					sb.append(memo);
					sb.append(" - ");
				}

				sb.append("`");
				sb.append(url);
				sb.append("`");
			}
		}

		EmbedBuilder eBuilder = buildBasicEmbed();

		if(sb.length() == 0) {
			if(page == 0) {
				sb.append("There are no tracks in that playlist.");
			} else {
				sb.append("There are no tracks in that playlist on this page.");
			}
		}

		if ((page+1) > totalPages) {
			eBuilder.setTitle("Playlist \""+playlist.getName()+"\"");
		} else {
			eBuilder.setTitle("Playlist \""+playlist.getName()+"\" (page "+(page+1)+" of "+totalPages+")");
		}

		eBuilder.addField("Tracks", sb.toString(), false);

		c.sendMessage(eBuilder.build()).queue();
	}

	private void sendMusicCmdHelp(MessageChannel c) {
		StringBuilder sb = new StringBuilder();
		EmbedBuilder eBuilder = buildBasicEmbed();

		sb.append("`play <url|playlist>` - Begin playing a track or playlist.\n");
		sb.append("`pause` - Toggle pause.\n");
		sb.append("`shuffle` - Shuffles tracks in the queue.\n");
		sb.append("`skip` - Skip the current track.\n");
		sb.append("`stop` - Stop playing and disconnect from the channel.\n");
		sb.append("`loop` - Toggle track looping.\n");
		sb.append("`playing|np` - See details about the current track.\n");
		sb.append("`(q)ueue [url|playlist|clear|page]` - Show the queue, add items, or clear it.\n");
		sb.append("`convert <name>` - Create a new playlist from the tracks in the queue.\n");
		sb.append("`(pl)aylist` - Manage a playlist.");

		eBuilder.addField("Commands", sb.toString(), false);

		c.sendMessage(eBuilder.build()).queue();
	}

	private void sendPlaylistCmdHelp(MessageChannel c) {
		StringBuilder sb = new StringBuilder();
		EmbedBuilder eBuilder = buildBasicEmbed();

		sb.append("`(c)reate <name> [track url] [track memo]` - Create a new playlist with an optional track and memo.\n");
		sb.append("`(a)dd <playlist> <track url> [memo]` - Add a new track to a playlist with an optional memo.\n");
		sb.append("`(l)ist [playlist|all] [page]` - List all playlists or all tracks in a playlist.\n");
		sb.append("`(r)emove <playlist> <track number>` - Remove a track from a playlist.\n");
		sb.append("`(d)elete <playlist>` - Delete a playlist and all tracks it contains.\n");

		eBuilder.addField("Commands", sb.toString(), false);

		c.sendMessage(eBuilder.build()).queue();
	}

	private EmbedBuilder buildBasicEmbed() {
		EmbedBuilder eBuilder = new EmbedBuilder();

		eBuilder.setColor(new Color(43, 164, 188));
		eBuilder.setAuthor(Shmames.getBotName(), null, Shmames.getJDA().getSelfUser().getAvatarUrl());
		eBuilder.setFooter("Music is in Beta. Some features may not work as intended.");

		return eBuilder;
	}

	private String getHumanTimeCode(long timeInMS) {
		int minutes = (int)Math.floor((timeInMS/1000d)/60d);
		int seconds = (int)((timeInMS/1000) - (minutes*60));

		return minutes+":"+(seconds < 10 ? "0" + seconds : seconds);
	}

	private String extractVideoID(String url) {
		Matcher m = Pattern.compile(".+v=([a-z0-9\\-]+)(&.+)?", Pattern.CASE_INSENSITIVE).matcher(url);

		if(m.find()){
			return m.group(1);
		}else{
			return null;
		}
	}

	private boolean isUrl(String test) {
		Matcher m = Pattern.compile("^https?://.+$", Pattern.CASE_INSENSITIVE).matcher(test);

		return m.find();
	}

	private boolean isInt(String test) {
		try {
			Integer.parseInt(test);
			return true;
		} catch (Exception ignored) {}

		return false;
	}

	private boolean canUse(Brain b, Member m) {
		return Utils.CheckUserPermission(b.getSettingFor(BotSettingName.RESET_EMOTE_STATS), m);
	}

	private Playlist findPlaylist(String name, Brain b) {
		// Check local server.
		for(Playlist pl : b.getPlaylists()) {
			if(pl.getName().equalsIgnoreCase(name)) {
				return pl;
			}
		}

		// Check other Family servers.
		for(String fid : b.getFamilies()){
			Family f = Shmames.getBrains().getMotherBrain().getFamilyByID(fid);

			if(f != null){
				for(long gid : f.getMemberGuilds()){
					if(!Long.toString(gid).equals(b.getGuildID())){
						Brain ob = Shmames.getBrains().getBrain(Long.toString(gid));

						for(Playlist pl : ob.getPlaylists()) {
							if(pl.getName().equalsIgnoreCase(name)) {
								return pl;
							}
						}
					}
				}
			}
		}

		return null;
	}
}
