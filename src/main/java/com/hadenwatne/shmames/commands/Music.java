package com.hadenwatne.shmames.commands;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hadenwatne.shmames.Utils;
import com.hadenwatne.shmames.commandbuilder.*;
import com.hadenwatne.shmames.enums.BotSettingName;
import com.hadenwatne.shmames.enums.Langs;
import com.hadenwatne.shmames.models.Brain;
import com.hadenwatne.shmames.models.Family;
import com.hadenwatne.shmames.models.Lang;
import com.hadenwatne.shmames.models.Playlist;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.music.GuildOcarina;
import com.hadenwatne.shmames.Shmames;

import javax.annotation.Nullable;

public class Music implements ICommand {
	private final CommandStructure commandStructure;

	public Music() {
		this.commandStructure = CommandBuilder.Create("music")
				.addSubCommands(
						CommandBuilder.Create("play")
								.addAlias("p")
								.addParameters(
										new CommandParameter("playPlaylist", "The playlist to play.", ParameterType.STRING, false)
												.setPattern("[\\w\\d-_]+"),
										new CommandParameter("playURL", "The URL of the song to play.", ParameterType.STRING, false)
												.setPattern("https?:\\/\\/[\\w\\d:/.\\-?&=%#@]+")
								)
								.build(),
						CommandBuilder.Create("pause")
								.build(),
						CommandBuilder.Create("resume")
								.addAlias("r")
								.build(),
						CommandBuilder.Create("skip")
								.addParameters(
										new CommandParameter("number", "How many tracks to skip.", ParameterType.INTEGER, false)
								)
								.build(),
						CommandBuilder.Create("stop")
								.build(),
						CommandBuilder.Create("loop")
								.addParameters(
										new CommandParameter("loopQueue", "Whether to loop the queue.", ParameterType.BOOLEAN)
								)
								.build(),
						CommandBuilder.Create("playing")
								.addAlias("np")
								.build(),
						CommandBuilder.Create("convert")
								.addParameters(
										new CommandParameter("newPlaylistName", "The name to use for the new playlist.", ParameterType.STRING)
												.setPattern("[\\w\\d-_]+")
								)
								.build()
				)
				.addSubCommandGroups(
						new SubCommandGroup("playlist")
								.addAlias("pl")
								.addSubCommands(
										CommandBuilder.Create("create")
												.build(),
										CommandBuilder.Create("add")
												.build(),
										CommandBuilder.Create("list")
												.build(),
										CommandBuilder.Create("remove")
												.build(),
										CommandBuilder.Create("delete")
												.build()
								),
						new SubCommandGroup("queue")
								.addAlias("q")
								.addSubCommands(
										CommandBuilder.Create("clear")
												.build(),
										CommandBuilder.Create("reverse")
												.build(),
										CommandBuilder.Create("shuffle")
												.build(),
										CommandBuilder.Create("append")
												.build()
								)
				)
				.build();
	}

	@Override
	public CommandStructure getCommandStructure() {
		return this.commandStructure;
	}

	@Override
	public String getDescription() {
		return "Play music, create playlists, and chill out.";
	}
	
	@Override
	public String getUsage() {
		return this.commandStructure.getUsage();
	}

	@Override
	public String getExamples() {
		return "`music play http://link.to.a.good.song`\n" +
				"`music pause`\n" +
				"`music queue myDopePlaylist`\n" +
				"`music playlist list`";
	}

	@Override
	public boolean requiresGuild() {
		return true;
	}

	@Override
	public String run (Lang lang, Brain brain, ShmamesCommandData data) {
		Matcher m = Pattern.compile("^([a-z]+)\\s?(.+)?$", Pattern.CASE_INSENSITIVE).matcher(args);

		if(m.find()){
			String mainCmd = m.group(1);
			GuildOcarina ocarina = Shmames.getMusicManager().getOcarina(message.getGuild().getId());
			Brain b = Shmames.getBrains().getBrain(message.getGuild().getId());

			switch(mainCmd) {
				case "p":
				case "play":
					if (canUse(b, message.getMember())) {
						return cmdPlay(message.getMember(), message.getTextChannel(), ocarina, m.group(2));
					} else {
						return lang.getError(Errors.NO_PERMISSION_USER, true);
					}
				case "pause":
					ocarina.togglePause(true);
					break;
				case "r":
				case "resume":
					ocarina.togglePause(false);
					break;
				case "skip":
					if(ocarina.getNowPlaying() != null) {
						if (canUse(b, message.getMember())) {
							if(m.group(2) != null && Utils.isInt(m.group(2))){
								ocarina.skipMany((Integer.parseInt( m.group(2))));
							}else{
								ocarina.skip();
							}
						} else {
							return lang.getError(Errors.NO_PERMISSION_USER, true);
						}
					}else{
						return lang.getError(Errors.TRACK_NOT_PLAYING, false);
					}
					break;
				case "stop":
					if(ocarina.getNowPlaying() != null) {
						if (canUse(b, message.getMember())) {
							ocarina.stop();
						} else {
							return lang.getError(Errors.NO_PERMISSION_USER, true);
						}
					}else{
						return lang.getError(Errors.TRACK_NOT_PLAYING, true);
					}
					break;
				case "loop":
					if (canUse(b, message.getMember())) {
						String g2 = m.group(2);

						if(g2 != null) {
							if(g2.equalsIgnoreCase("queue")) {
								boolean isLoopQueue = ocarina.toggleLoopQueue();
								return lang.getMsg(Langs.MUSIC_LOOPING_QUEUE_TOGGLED, new String[]{ isLoopQueue ? "ON" : "OFF" });
							}else{
								return lang.getError(Errors.COMMAND_NOT_FOUND, false);
							}
						}else {
							boolean isLoop = ocarina.toggleLoop();
							return lang.getMsg(Langs.MUSIC_LOOPING_TOGGLED, new String[]{isLoop ? "ON" : "OFF"});
						}
					}else{
						return lang.getError(Errors.NO_PERMISSION_USER, true);
					}
				case "np":
				case "playing":
					AudioTrack track = ocarina.getNowPlaying();

					if(track != null){
						showTrackData(track, message.getChannel(), ocarina);
					}else{
						return lang.getError(Errors.TRACK_NOT_PLAYING, false);
					}

					break;
				case "pl":
				case "playlist":
					if (canUse(b, message.getMember())) {
						return cmdPlaylist(b, message.getChannel(), m.group(2));
					}else{
						return lang.getError(Errors.NO_PERMISSION_USER, true);
					}
				case "q":
				case "queue":
					if (canUse(b, message.getMember())) {
						return cmdQueue(ocarina, message.getChannel(), m.group(2));
					} else {
						return lang.getError(Errors.NO_PERMISSION_USER, true);
					}
				case "convert":
					if (canUse(b, message.getMember())) {
						return cmdConvert(ocarina, m.group(2));
					} else {
						return lang.getError(Errors.NO_PERMISSION_USER, true);
					}
				default:
					return lang.wrongUsage(commandStructure.getUsage());
			}
		}else{
			sendMusicCmdHelp(message.getChannel());

			return "";
		}

		return "";
	}

	private String cmdPlay(Member m, TextChannel c, GuildOcarina ocarina, @Nullable String args) {
		if (args != null) {
			if (!ocarina.isInVoiceChannel()) {
				if (m.getVoiceState() != null && m.getVoiceState().inVoiceChannel()) {
					VoiceChannel vchannel = m.getVoiceState().getChannel();

					ocarina.connect(vchannel, c);
				} else {
					return lang.getError(Errors.MUSIC_NOT_IN_CHANNEL, false);
				}
			}

			if (isUrl(args)) {
				ocarina.loadTrack(args, false);

				return lang.getMsg(Langs.MUSIC_PLAYING);
			} else {
				Playlist pl = findPlaylistFamily(args, brain);

				if(pl != null) {
					ocarina.loadCustomPlaylist(pl.getTracks(), false);

					return lang.getMsg(Langs.MUSIC_PLAYING_PLAYLIST, new String[]{ pl.getName() });
				} else {
					return lang.getError(Errors.ITEMS_NOT_FOUND, false);
				}
			}
		} else {
			if (ocarina.isPaused()) {
				ocarina.togglePause(false);

				return "";
			} else {
				return lang.getError(Errors.MUSIC_WRONG_INPUT, false);
			}
		}
	}

	private String cmdQueue(GuildOcarina ocarina, MessageChannel c, @Nullable String args) {
		if (ocarina.isInVoiceChannel()) {
			if (args != null) {
				if (isUrl(args)) {
					ocarina.loadTrack(args, true);

					return lang.getMsg(Langs.MUSIC_ADDED_TO_QUEUE);
				} else if (args.equalsIgnoreCase("clear")) {
					ocarina.getQueue().clear();

					return lang.getMsg(Langs.MUSIC_QUEUE_CLEARED);
				}
				else if (args.equalsIgnoreCase("reverse")) {
					ocarina.reverseQueue();

					return lang.getMsg(Langs.MUSIC_QUEUE_REVERSED);
				}
				else if (args.equalsIgnoreCase("shuffle")) {
					ocarina.shuffleQueue();

					return lang.getMsg(Langs.MUSIC_QUEUE_SHUFFLED);
				}
				else if (Utils.isInt(args)) {
					showQueue(ocarina.getQueue(), c, Integer.parseInt(args));

					return "";
				} else {
					Playlist pl = findPlaylistFamily(args, brain);

					if(pl != null) {
						ocarina.loadCustomPlaylist(pl.getTracks(), true);

						return lang.getMsg(Langs.MUSIC_QUEUED_PLAYLIST, new String[]{ pl.getName() });
					} else {
						return lang.getError(Errors.ITEMS_NOT_FOUND, false);
					}
				}
			} else {
				showQueue(ocarina.getQueue(), c, 1);

				return "";
			}
		} else {
			return lang.getError(Errors.MUSIC_NOT_IN_CHANNEL, false);
		}
	}

	private String cmdConvert(GuildOcarina ocarina, @Nullable String args) {
		if (ocarina.getNowPlaying() != null) {
			if (args != null) {
				Matcher conv = Pattern.compile("^([a-z0-9]+)$", Pattern.CASE_INSENSITIVE).matcher(args);

				if (conv.find()) {
					String name = conv.group(1).toLowerCase();

					if (findPlaylistServer(name, brain) == null) {
						Playlist p = new Playlist(name);

						p.addTrack(ocarina.getNowPlaying().getInfo().uri, ocarina.getNowPlaying().getInfo().title);

						for (AudioTrack t : ocarina.getQueue()) {
							if (p.getTracks().size() < 50) {
								p.addTrack(t.getInfo().uri, t.getInfo().title);
							}
						}

						brain.getPlaylists().add(p);

						return lang.getMsg(Langs.MUSIC_PLAYLIST_CONVERTED, new String[]{ name, Integer.toString(p.getTracks().size()) });
					} else {
						return lang.getError(Errors.MUSIC_PLAYLIST_ALREADY_EXISTS, false);
					}
				} else {
					return lang.getError(Errors.MUSIC_PLAYLIST_NAME_INVALID, false);
				}
			} else {
				return lang.getError(Errors.MUSIC_PLAYLIST_NAME_MISSING, false);
			}
		} else {
			return lang.getError(Errors.TRACK_NOT_PLAYING, true);
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
						Matcher create = Pattern.compile("^([a-z0-9_]+)\\s?(https?://[./\\w\\d-_&?=*%:]+)?\\s?(.+)?$", Pattern.CASE_INSENSITIVE).matcher(subArgs);

						if (create.find()) {
							return cmdPlaylistCreate(b, create.group(1), create.group(2), create.group(3));
						} else {
							return lang.getError(Errors.WRONG_USAGE, true);
						}
					case "a":
					case "add":
						Matcher add = Pattern.compile("^([a-z0-9_]+)\\s(https?://[./\\w\\d-_&?=*%:]+)\\s?(.+)?$", Pattern.CASE_INSENSITIVE).matcher(subArgs);

						if (add.find()) {
							return cmdPlaylistAdd(b, add.group(1), add.group(2), add.group(3));
						} else {
							return lang.getError(Errors.WRONG_USAGE, true);
						}
					case "l":
					case "list":
						Matcher list = Pattern.compile("^([a-z0-9_]+)?\\s?(\\d{1,3})?$", Pattern.CASE_INSENSITIVE).matcher(subArgs);

						if (list.find()) {
							return cmdPlaylistList(b, list.group(1), c, list.group(2));
						} else {
							return lang.getError(Errors.WRONG_USAGE, true);
						}
					case "r":
					case "remove":
						Matcher remove = Pattern.compile("^([a-z0-9_]+)\\s(\\d{1,2})$", Pattern.CASE_INSENSITIVE).matcher(subArgs);

						if (remove.find()) {
							String listName = remove.group(1);
							int position = Integer.parseInt(remove.group(2));
							Playlist pRemove = findPlaylistServer(listName, b);

							if (pRemove != null) {
								if (pRemove.removeTrack(position - 1)) {
									return lang.getMsg(Langs.MUSIC_PLAYLIST_TRACK_REMOVED);
								} else {
									return lang.getError(Errors.NOT_FOUND, false);
								}
							} else {
								return lang.getError(Errors.MUSIC_PLAYLIST_DOESNT_EXIST, false);
							}
						} else {
							return lang.getError(Errors.WRONG_USAGE, true);
						}
					case "d":
					case "delete":
						Matcher delete = Pattern.compile("^([a-z0-9_]+)$", Pattern.CASE_INSENSITIVE).matcher(subArgs);

						if (delete.find()) {
							String listName = delete.group(1);
							Playlist pDelete = findPlaylistServer(listName, b);

							if (pDelete != null) {
								b.getPlaylists().remove(pDelete);

								return lang.getMsg(Langs.MUSIC_PLAYLIST_DELETED);
							} else {
								return lang.getError(Errors.MUSIC_PLAYLIST_DOESNT_EXIST, false);
							}
						} else {
							return lang.getError(Errors.WRONG_USAGE, true);
						}
					default:
						return lang.getError(Errors.COMMAND_NOT_FOUND, true);
				}
			} else {
				return lang.getError(Errors.WRONG_USAGE, true);
			}
		} else {
			sendPlaylistCmdHelp(c);

			return "";
		}
	}

	private String cmdPlaylistCreate(Brain b, String listName, @Nullable String url, @Nullable String memo) {
		if(findPlaylistServer(listName, b) == null) {
			if(!listName.equalsIgnoreCase("all")) {
				Playlist newList = new Playlist(listName);

				if (url != null) {
					newList.addTrack(url, memo);
				}

				b.getPlaylists().add(newList);

				return lang.getMsg(Langs.MUSIC_PLAYLIST_CREATED, new String[]{listName});
			}else{
				return lang.getError(Errors.RESERVED_WORD, false);
			}
		} else {
			return lang.getError(Errors.MUSIC_PLAYLIST_ALREADY_EXISTS, false);
		}
	}

	private String cmdPlaylistAdd(Brain b, String listName, String url, @Nullable String memo) {
		Playlist p = findPlaylistServer(listName, b);

		if (p != null) {
			if(p.getTracks().size() < 50) {
				p.addTrack(url, memo);

				return lang.getMsg(Langs.MUSIC_PLAYLIST_TRACK_ADDED);
			}else{
				return lang.getError(Errors.MUSIC_PLAYLIST_TRACK_MAXIMUM_REACHED, true);
			}
		} else {
			return lang.getError(Errors.NOT_FOUND, true);
		}
	}

	private String cmdPlaylistList(Brain b, @Nullable String listName, MessageChannel c, @Nullable String p) {
		if(listName != null) {
			if(listName.equalsIgnoreCase("all")) {
				// List families' playlists
				EmbedBuilder eBuilder = buildBasicEmbed();
				String playlistNames = createPlaylistNameList(b.getPlaylists());

				eBuilder.addField(Shmames.getJDA().getGuildById(b.getGuildID()).getName(), playlistNames, false);

				// Then add each server in our family's playlists
				for(String fid : b.getFamilies()) {
					Family f = Shmames.getBrains().getMotherBrain().getFamilyByID(fid);

					if(f != null){
						for(long gid : f.getMemberGuilds()){
							if(!Long.toString(gid).equals(b.getGuildID())){
								Brain ob = Shmames.getBrains().getBrain(Long.toString(gid));
								String playlistNamesOther = createPlaylistNameList(ob.getPlaylists());

								eBuilder.addField(Shmames.getJDA().getGuildById(ob.getGuildID()).getName(), playlistNamesOther, false);
							}
						}
					}
				}

				c.sendMessage(eBuilder.build()).queue();

				return "";
			}else{
				Playlist pList = findPlaylistServer(listName, b);

				if (pList != null) {
					int page = p != null ? Math.max(Integer.parseInt(p), 1) : 1;

					showList(pList, c, page);

					return "";
				} else {
					return lang.getError(Errors.NOT_FOUND, true);
				}
			}
		}

		// Just list out this server's playlists
		EmbedBuilder eBuilder = buildBasicEmbed();
		String playlistNames = createPlaylistNameList(b.getPlaylists());

		eBuilder.addField("Playlists", playlistNames, false);
		c.sendMessage(eBuilder.build()).queue();

		return "";
	}

	private void showTrackData(AudioTrack t, MessageChannel c, GuildOcarina o) {
		EmbedBuilder eBuilder = buildBasicEmbed();

		if (!t.getInfo().isStream) {
			String videoID = extractVideoID(t.getInfo().uri);

			if (videoID != null) {
				eBuilder.setThumbnail("http://img.youtube.com/vi/" + videoID + "/1.jpg");
			}

			eBuilder.setTitle(t.getInfo().title, t.getInfo().uri);
			eBuilder.addField("Looping", o.isLooping() ? "Yes" : "No", true);
			eBuilder.addField("Position", getHumanTimeCode(t.getPosition()) + " / " + getHumanTimeCode(t.getDuration()), true);
		}else{
			eBuilder.setThumbnail("https://www.screensaversplanet.com/img/screenshots/screensavers/large/the-matrix-1.png");
			eBuilder.setTitle("Livestream");
			eBuilder.addField("Info", Shmames.getBotName()+" is currently playing from an audio livestream.", false);
		}

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
				sb.append(lang.getError(Errors.MUSIC_QUEUE_EMPTY, false));
			} else {
				sb.append(lang.getError(Errors.MUSIC_QUEUE_PAGE_EMPTY, false));
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
				sb.append(lang.getError(Errors.MUSIC_PLAYLIST_EMPTY, false));
			} else {
				sb.append(lang.getError(Errors.MUSIC_PLAYLIST_PAGE_EMPTY, false));
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

		sb.append("`(p)lay <url|playlist>` - Begin playing a track or playlist.\n");
		sb.append("`pause` - Toggle pause.\n");
		sb.append("`skip [count]` - Skip tracks.\n");
		sb.append("`stop` - Stop playing and disconnect from the channel.\n");
		sb.append("`loop [queue]` - Toggle track or queue looping.\n");
		sb.append("`playing|np` - See details about the current track.\n");
		sb.append("`(q)ueue [url|playlist|clear|page|reverse|shuffle]` - Manage the music queue.\n");
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
//		eBuilder.setFooter("Music is in Beta. Some features may not work as intended.");

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

	private boolean canUse(Brain b, Member m) {
		return Utils.checkUserPermission(b.getSettingFor(BotSettingName.MANAGE_MUSIC), m);
	}

	private Playlist findPlaylistServer(String name, Brain b) {
		if(name.length() > 0) {
			for (Playlist p : b.getPlaylists()) {
				if (p.getName().equalsIgnoreCase(name)) {
					return p;
				}
			}
		}

		return null;
	}

	private Playlist findPlaylistFamily(String name, Brain b) {
		Playlist server = findPlaylistServer(name, b);

		if (server != null) {
			return server;
		}

		// Check other Family servers.
		for(String fid : b.getFamilies()){
			Family f = Shmames.getBrains().getMotherBrain().getFamilyByID(fid);

			if(f != null){
				for(long gid : f.getMemberGuilds()){
					if(!Long.toString(gid).equals(b.getGuildID())){
						Brain ob = Shmames.getBrains().getBrain(Long.toString(gid));
						Playlist otherServer = findPlaylistServer(name, ob);

						if (otherServer != null) {
							return otherServer;
						}
					}
				}
			}
		}

		return null;
	}

	private String createPlaylistNameList(List<Playlist> playlist) {
		List<String> playlistNames = new ArrayList<>();
		StringBuilder sb = new StringBuilder();

		for (Playlist pl : playlist) {
			playlistNames.add(pl.getName());
		}

		Collections.sort(playlistNames);

		for (String playlistName : playlistNames) {
			if (sb.length() > 0)
				sb.append(", ");

			sb.append("`");
			sb.append(playlistName);
			sb.append("`");
		}

		if (sb.length() == 0) {
			sb.append(lang.getError(Errors.MUSIC_PLAYLIST_LIST_EMPTY, false));
		}

		return sb.toString();
	}
}
