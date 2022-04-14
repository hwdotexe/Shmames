package com.hadenwatne.shmames.commands;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hadenwatne.shmames.App;
import com.hadenwatne.shmames.models.PaginatedList;
import com.hadenwatne.shmames.services.PaginationService;
import com.hadenwatne.shmames.services.ShmamesService;
import com.hadenwatne.shmames.commandbuilder.*;
import com.hadenwatne.shmames.enums.BotSettingName;
import com.hadenwatne.shmames.enums.Langs;
import com.hadenwatne.shmames.enums.RegexPatterns;
import com.hadenwatne.shmames.models.Playlist;
import com.hadenwatne.shmames.models.command.ShmamesCommandArguments;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import com.hadenwatne.shmames.models.command.ShmamesCommandMessagingChannel;
import com.hadenwatne.shmames.models.command.ShmamesSubCommandData;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.music.GuildOcarina;
import com.hadenwatne.shmames.Shmames;

public class Music implements ICommand {
	private final CommandStructure commandStructure;

	public Music() {
		this.commandStructure = CommandBuilder.Create("music", "Play music, create playlists, and chill out.")
				.addAlias("bops")
				.addSubCommands(
						CommandBuilder.Create("play", "Play a track or playlist.")
								.addAlias("p")
								.addParameters(
										new CommandParameter("playlistName", "The playlist to play.", ParameterType.STRING, false)
												.setPattern(RegexPatterns.ALPHANUMERIC.getPattern()),
										new CommandParameter("URL", "The URL of the song to play.", ParameterType.STRING, false)
												.setPattern(RegexPatterns.URL.getPattern())
								)
								.setExample("music play https://link")
								.build(),
						CommandBuilder.Create("pause", "Pause any playing music.")
								.setExample("music pause")
								.build(),
						CommandBuilder.Create("resume", "Resume after pausing.")
								.addAlias("r")
								.setExample("music resume")
								.build(),
						CommandBuilder.Create("skip", "Skip the current track.")
								.addParameters(
										new CommandParameter("number", "How many tracks to skip.", ParameterType.INTEGER, false)
								)
								.setExample("music skip 3")
								.build(),
						CommandBuilder.Create("stop", "Stop playing music.")
								.setExample("music stop")
								.build(),
						CommandBuilder.Create("loop", "Toggle track looping.")
								.setExample("music loop")
								.build(),
						CommandBuilder.Create("loopqueue", "Toggle queue looping.")
								.setExample("music loopqueue")
								.build(),
						CommandBuilder.Create("playing", "See what's playing.")
								.addAlias("np")
								.setExample("music playing")
								.build(),
						CommandBuilder.Create("convert", "Convert the queue to a playlist.")
								.addParameters(
										new CommandParameter("playlistName", "The name to use for the new playlist.", ParameterType.STRING)
												.setPattern(RegexPatterns.ALPHANUMERIC.getPattern())
								)
								.setExample("music convert newPlaylist")
								.build()
				)
				.addSubCommandGroups(
						new SubCommandGroup("playlist", "Manage playlists.")
								.addAlias("pl")
								.addSubCommands(
										CommandBuilder.Create("create", "Create a new playlist.")
												.addAlias("c")
												.addParameters(
														new CommandParameter("playlistName", "The name of the new playlist.", ParameterType.STRING)
																.setPattern(RegexPatterns.ALPHANUMERIC.getPattern())
												)
												.setExample("music playlist create newPlaylist")
												.build(),
										CommandBuilder.Create("add", "Add a track to a playlist.")
												.addAlias("a")
												.addParameters(
														new CommandParameter("playlistName", "The name of the playlist to add to.", ParameterType.STRING)
																.setPattern(RegexPatterns.ALPHANUMERIC.getPattern()),
														new CommandParameter("URL", "The URL of the track to add.", ParameterType.STRING)
																.setPattern(RegexPatterns.URL.getPattern()),
														new CommandParameter("memo", "A memo about the track being added.", ParameterType.STRING, false)
												)
												.setExample("music playlist add newPlaylist https://link great song")
												.build(),
										CommandBuilder.Create("list", "Show available playlists.")
												.addAlias("l")
												.addParameters(
														new CommandParameter("playlistName", "The name of the playlist.", ParameterType.STRING, false)
																.setPattern(RegexPatterns.ALPHANUMERIC.getPattern()),
														new CommandParameter("page", "The page to view.", ParameterType.INTEGER, false)
												)
												.setExample("music playlist list newPlaylist 2")
												.build(),
										CommandBuilder.Create("remove", "Remove a track from a playlist.")
												.addAlias("r")
												.addParameters(
														new CommandParameter("playlistName", "The playlist to remove an item from.", ParameterType.STRING)
																.setPattern(RegexPatterns.ALPHANUMERIC.getPattern()),
														new CommandParameter("position", "The position of the item to remove.", ParameterType.INTEGER)
												)
												.setExample("music playlist remove newPlaylist 3")
												.build(),
										CommandBuilder.Create("delete", "Delete a playlist.")
												.addAlias("d")
												.addParameters(
														new CommandParameter("playlistName", "The name of the playlist to delete.", ParameterType.STRING)
																.setPattern(RegexPatterns.ALPHANUMERIC.getPattern())
												)
												.setExample("music playlist delete newPlaylist")
												.build()
								),
						new SubCommandGroup("queue", "Manage the queue.")
								.addAlias("q")
								.addSubCommands(
										CommandBuilder.Create("clear", "Clear the list of upcoming tracks.")
												.addAlias("c")
												.setExample("music queue clear")
												.build(),
										CommandBuilder.Create("reverse", "Reverse the order of the queue.")
												.addAlias("r")
												.setExample("music queue reverse")
												.build(),
										CommandBuilder.Create("shuffle", "Shuffle the upcoming tracks.")
												.addAlias("s")
												.setExample("music queue shuffle")
												.build(),
										CommandBuilder.Create("append", "Add more tracks or playlists to the queue.")
												.addAlias("a")
												.addParameters(
														new CommandParameter("playlistName", "The playlist to append.", ParameterType.STRING, false)
																.setPattern(RegexPatterns.ALPHANUMERIC.getPattern()),
														new CommandParameter("URL", "The URL of the song to append.", ParameterType.STRING, false)
																.setPattern(RegexPatterns.URL.getPattern())
												)
												.setExample("music queue append https://link")
												.build(),
										CommandBuilder.Create("view", "View the queue.")
												.addAlias("v")
												.addParameters(
														new CommandParameter("page", "The page of the queue to view.", ParameterType.INTEGER, false)
												)
												.setExample("music queue view 4")
												.build()
								)
				)
				.setExample("music")
				.build();
	}

	@Override
	public CommandStructure getCommandStructure() {
		return this.commandStructure;
	}

	@Override
	public boolean requiresGuild() {
		return true;
	}

	@Override
	public String run (Lang lang, Brain brain, ShmamesCommandData data) {
		ShmamesSubCommandData subCommand = data.getSubCommandData();
		GuildOcarina ocarina = App.Shmames.getMusicManager().getOcarina(data.getServer().getId());
		User author = data.getAuthor();
		Guild server = data.getServer();
		String nameOrGroup = subCommand.getNameOrGroup();

		switch (nameOrGroup) {
			case "play":
				if (canUse(server, brain, author)) {
					return cmdPlay(lang, brain, server, server.getMember(author), data.getMessagingChannel(), ocarina, subCommand.getArguments());
				} else {
					return lang.getError(Errors.NO_PERMISSION_USER, true);
				}
			case "pause":
				ocarina.togglePause(true);
				return "";
			case "resume":
				ocarina.togglePause(false);
				return "";
			case "skip":
				if (ocarina.getNowPlaying() != null) {
					if (canUse(server, brain, author)) {
						cmdSkip(ocarina, subCommand.getArguments());
						return "";
					} else {
						return lang.getError(Errors.NO_PERMISSION_USER, true);
					}
				} else {
					return lang.getError(Errors.TRACK_NOT_PLAYING, false);
				}
			case "stop":
				if (ocarina.isInVoiceChannel()) {
					if (canUse(server, brain, author)) {
						ocarina.stop();
						return "";
					} else {
						return lang.getError(Errors.NO_PERMISSION_USER, true);
					}
				} else {
					return lang.getError(Errors.TRACK_NOT_PLAYING, true);
				}
			case "loop":
				if (canUse(server, brain, author)) {
					return cmdLoop(lang, ocarina);
				} else {
					return lang.getError(Errors.NO_PERMISSION_USER, true);
				}
			case "loopqueue":
				if (canUse(server, brain, author)) {
					return cmdLoopQueue(lang, ocarina);
				} else {
					return lang.getError(Errors.NO_PERMISSION_USER, true);
				}
			case "playing":
				AudioTrack track = ocarina.getNowPlaying();

				if (track != null) {
					showTrackData(track, data.getMessagingChannel(), ocarina);
				} else {
					return lang.getError(Errors.TRACK_NOT_PLAYING, false);
				}

				break;
			case "playlist":
				if (canUse(server, brain, author)) {
					return cmdPlaylist(brain, lang, server, data.getMessagingChannel(), subCommand);
				} else {
					return lang.getError(Errors.NO_PERMISSION_USER, true);
				}
			case "queue":
				if (canUse(server, brain, author)) {
					return cmdQueue(brain, lang, ocarina, server, data.getMessagingChannel(), subCommand);
				} else {
					return lang.getError(Errors.NO_PERMISSION_USER, true);
				}
			case "convert":
				if (canUse(server, brain, author)) {
					return cmdConvert(brain, lang, ocarina, subCommand.getArguments());
				} else {
					return lang.getError(Errors.NO_PERMISSION_USER, true);
				}
			default:
				return lang.wrongUsage(commandStructure.getUsage());
		}

		return "";
	}

	private String cmdPlay(Lang lang, Brain brain, Guild server, Member member, ShmamesCommandMessagingChannel messagingChannel, GuildOcarina ocarina, ShmamesCommandArguments args) {
		String playlist = args.getAsString("playlistName");
		String url = args.getAsString("URL");

		if (playlist != null || url != null) {
			if (!ocarina.isInVoiceChannel()) {
				if (member.getVoiceState() != null && member.getVoiceState().inVoiceChannel()) {
					VoiceChannel vchannel = member.getVoiceState().getChannel();

					ocarina.connect(vchannel, messagingChannel.getChannel());
				} else {
					return lang.getError(Errors.MUSIC_NOT_IN_CHANNEL, false);
				}
			}

			if (url != null) {
				ocarina.loadTrack(url, false);

				return lang.getMsg(Langs.MUSIC_PLAYING);
			} else {
				Playlist pl = findPlaylistFamily(playlist, brain, server);

				if(pl != null) {
					ocarina.loadCustomPlaylist(pl.getTracks(), false);

					return lang.getMsg(Langs.MUSIC_PLAYING_PLAYLIST, new String[]{ pl.getName() });
				} else {
					return lang.getError(Errors.ITEMS_NOT_FOUND, false);
				}
			}
		} else {
			// No options provided; resume if paused, or send an error.
			if (ocarina.isPaused()) {
				ocarina.togglePause(false);

				return "";
			} else {
				return lang.getError(Errors.MUSIC_WRONG_INPUT, false);
			}
		}
	}

	private void cmdSkip(GuildOcarina ocarina, ShmamesCommandArguments args) {
		int times = args.getAsInteger("number");

		if (times > 0) {
			ocarina.skipMany(times);
		} else {
			ocarina.skip();
		}
	}

	private String cmdLoop(Lang lang, GuildOcarina ocarina) {
		boolean isLoop = ocarina.toggleLoop();
		return lang.getMsg(Langs.MUSIC_LOOPING_TOGGLED, new String[]{isLoop ? "ON" : "OFF"});
	}

	private String cmdLoopQueue(Lang lang, GuildOcarina ocarina) {
		boolean isLoopQueue = ocarina.toggleLoopQueue();
		return lang.getMsg(Langs.MUSIC_LOOPING_QUEUE_TOGGLED, new String[]{isLoopQueue ? "ON" : "OFF"});
	}

	private String cmdQueue(Brain brain, Lang lang, GuildOcarina ocarina, Guild server, ShmamesCommandMessagingChannel messagingChannel, ShmamesSubCommandData commandData) {
		ShmamesCommandArguments args = commandData.getArguments();
		String subCommand = commandData.getCommandName();

		if (ocarina.isInVoiceChannel()) {
			switch(subCommand.toLowerCase()) {
				case "append":
					String appendPlaylist = args.getAsString("playlistName");
					String appendURL = args.getAsString("URL");

					if(appendURL != null) {
						ocarina.loadTrack(appendURL, true);

						return lang.getMsg(Langs.MUSIC_ADDED_TO_QUEUE);
					} else if(appendPlaylist != null) {
						Playlist pl = findPlaylistFamily(appendPlaylist, brain, server);

						if(pl != null) {
							ocarina.loadCustomPlaylist(pl.getTracks(), true);

							return lang.getMsg(Langs.MUSIC_QUEUED_PLAYLIST, new String[]{ pl.getName() });
						} else {
							return lang.getError(Errors.ITEMS_NOT_FOUND, false);
						}
					} else {
						return lang.getError(Errors.MUSIC_WRONG_INPUT, false);
					}
				case "reverse":
					ocarina.reverseQueue();

					return lang.getMsg(Langs.MUSIC_QUEUE_REVERSED);
				case "shuffle":
					ocarina.shuffleQueue();

					return lang.getMsg(Langs.MUSIC_QUEUE_SHUFFLED);
				case "clear":
					ocarina.getQueue().clear();

					return lang.getMsg(Langs.MUSIC_QUEUE_CLEARED);
				default:
					int queuePage = args.getAsInteger("page");

					showQueue(lang, ocarina.getQueue(), messagingChannel, Math.max(queuePage, 1));

					return "";
			}
		} else {
			return lang.getError(Errors.MUSIC_NOT_IN_CHANNEL, false);
		}
	}

	private String cmdConvert(Brain brain, Lang lang, GuildOcarina ocarina, ShmamesCommandArguments args) {
		String newPlaylistName = args.getAsString("playlistName");

		if (ocarina.getNowPlaying() != null) {
			if (findPlaylistServer(newPlaylistName, brain) == null) {
				Playlist p = new Playlist(newPlaylistName);

				p.addTrack(ocarina.getNowPlaying().getInfo().uri, ocarina.getNowPlaying().getInfo().title);

				for (AudioTrack t : ocarina.getQueue()) {
					if (p.getTracks().size() < 50) {
						p.addTrack(t.getInfo().uri, t.getInfo().title);
					}
				}

				brain.getPlaylists().add(p);

				return lang.getMsg(Langs.MUSIC_PLAYLIST_CONVERTED, new String[]{newPlaylistName, Integer.toString(p.getTracks().size())});
			} else {
				return lang.getError(Errors.MUSIC_PLAYLIST_ALREADY_EXISTS, false);
			}
		} else {
			return lang.getError(Errors.TRACK_NOT_PLAYING, true);
		}
	}

	private String cmdPlaylist(Brain brain, Lang lang, Guild server, ShmamesCommandMessagingChannel messagingChannel, ShmamesSubCommandData commandData) {
		ShmamesCommandArguments args = commandData.getArguments();
		String subCommand = commandData.getCommandName();

		switch (subCommand) {
			case "create":
				String createPlaylistName = args.getAsString("playlistName");

				return cmdPlaylistCreate(lang, brain, createPlaylistName);
			case "add":
				String addPlaylistName = args.getAsString("playlistName");
				String addPlaylistURL = args.getAsString("URL");
				String addPlaylistMemo = args.getAsString("memo");

				return cmdPlaylistAdd(lang, brain, addPlaylistName, addPlaylistURL, addPlaylistMemo);
			case "list":
				String listPlaylistName = args.getAsString("playlistName");
				int listPlaylistPage = args.getAsInteger("page");

				return cmdPlaylistList(lang, brain, listPlaylistName, server, messagingChannel, listPlaylistPage);
			case "remove":
				String removePlaylist = args.getAsString("playlistName");
				int removePosition = args.getAsInteger("position");

				Playlist pRemove = findPlaylistServer(removePlaylist, brain);

				if (pRemove != null) {
					if (pRemove.removeTrack(removePosition - 1)) {
						return lang.getMsg(Langs.MUSIC_PLAYLIST_TRACK_REMOVED);
					} else {
						return lang.getError(Errors.NOT_FOUND, false);
					}
				} else {
					return lang.getError(Errors.MUSIC_PLAYLIST_DOESNT_EXIST, false);
				}
			case "delete":
				String deletePlaylist = args.getAsString("playlistName");
				Playlist pDelete = findPlaylistServer(deletePlaylist, brain);

				if (pDelete != null) {
					brain.getPlaylists().remove(pDelete);

					return lang.getMsg(Langs.MUSIC_PLAYLIST_DELETED);
				} else {
					return lang.getError(Errors.MUSIC_PLAYLIST_DOESNT_EXIST, false);
				}
			default:
				return lang.getError(Errors.COMMAND_NOT_FOUND, true);
		}
	}

	private String cmdPlaylistCreate(Lang lang, Brain brain, String listName) {
		if(findPlaylistServer(listName, brain) == null) {
			if(!listName.equalsIgnoreCase("all")) {
				Playlist newList = new Playlist(listName);

				brain.getPlaylists().add(newList);

				return lang.getMsg(Langs.MUSIC_PLAYLIST_CREATED, new String[]{listName});
			}else{
				return lang.getError(Errors.RESERVED_WORD, false);
			}
		} else {
			return lang.getError(Errors.MUSIC_PLAYLIST_ALREADY_EXISTS, false);
		}
	}

	private String cmdPlaylistAdd(Lang lang, Brain brain, String listName, String url, String memo) {
		Playlist p = findPlaylistServer(listName, brain);

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

	private String cmdPlaylistList(Lang lang, Brain brain, String listName, Guild server, ShmamesCommandMessagingChannel messagingChannel, int page) {
		if(listName != null) {
			if(listName.equalsIgnoreCase("all")) {
				// List families' playlists
				EmbedBuilder eBuilder = buildBasicEmbed();
				String playlistNames = createPlaylistNameList(lang, brain.getPlaylists());

				eBuilder.addField(server.getName(), playlistNames, false);

				// Then add each server in our family's playlists
				for(Guild family : ShmamesService.GetConnectedFamilyGuilds(brain, server)) {
					Brain otherBrain = App.Shmames.getStorageService().getBrain(family.getId());
					String playlistNamesOther = createPlaylistNameList(lang, otherBrain.getPlaylists());

					eBuilder.addField(family.getName(), playlistNamesOther, false);
				}

				messagingChannel.sendMessage(eBuilder);

				return "";
			}else{
				Playlist pList = findPlaylistServer(listName, brain);

				if (pList != null) {
					int viewPage = page > 0 ? page : 1;

					showList(lang, pList, messagingChannel, viewPage);

					return "";
				} else {
					return lang.getError(Errors.NOT_FOUND, true);
				}
			}
		}

		// Just list out this server's playlists
		EmbedBuilder eBuilder = buildBasicEmbed();
		String playlistNames = createPlaylistNameList(lang, brain.getPlaylists());

		eBuilder.addField("Playlists", playlistNames, false);

		messagingChannel.sendMessage(eBuilder);

		return "";
	}

	private void showTrackData(AudioTrack t, ShmamesCommandMessagingChannel messagingChannel, GuildOcarina o) {
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
			eBuilder.addField("Info", App.Shmames.getBotName()+" is currently playing from an audio livestream.", false);
		}

		messagingChannel.sendMessage(eBuilder);
	}

	private void showQueue(Lang lang, List<AudioTrack> queue, ShmamesCommandMessagingChannel messagingChannel, int page) {
		List<String> trackTitles = new ArrayList<>();

		for(AudioTrack track : queue) {
			trackTitles.add(track.getInfo().title);
		}

		PaginatedList paginatedList = PaginationService.GetPaginatedList(trackTitles, 10, -1, true);

		messagingChannel.sendMessage(PaginationService.DrawEmbedPage(paginatedList, Math.max(1, page), "Music Queue", new Color(43, 164, 188), lang));
	}

	private void showList(Lang lang, Playlist playlist, ShmamesCommandMessagingChannel messagingChannel, int page) {
		List<String> playlistTracks = new ArrayList<>();

		for(String trackURL : playlist.getTracks()) {
			String memo = playlist.getMemo(trackURL);
			String trackText = "";

			if (memo != null && memo.length() > 0) {
				trackText = memo + " - ";
			}

			trackText = trackText + "`" + trackURL + "`";

			playlistTracks.add(trackText);
		}

		PaginatedList paginatedList = PaginationService.GetPaginatedList(playlistTracks, 10, -1, true);

		messagingChannel.sendMessage(PaginationService.DrawEmbedPage(paginatedList, Math.max(1, page), "Playlist \""+playlist.getName()+"\"", new Color(43, 164, 188), lang));
	}

	private EmbedBuilder buildBasicEmbed() {
		EmbedBuilder eBuilder = new EmbedBuilder();

		eBuilder.setColor(new Color(43, 164, 188));
		eBuilder.setAuthor(App.Shmames.getBotName(), null, App.Shmames.getJDA().getSelfUser().getAvatarUrl());

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

	private boolean canUse(Guild server, Brain brain, User user) {
		return ShmamesService.CheckUserPermission(server, brain.getSettingFor(BotSettingName.MANAGE_MUSIC), user);
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

	private Playlist findPlaylistFamily(String name, Brain brain, Guild server) {
		Playlist serverPlaylist = findPlaylistServer(name, brain);

		if (serverPlaylist != null) {
			return serverPlaylist;
		}

		// Check other Family servers.
		for(Guild family : ShmamesService.GetConnectedFamilyGuilds(brain, server)) {
			Brain familyBrain = App.Shmames.getStorageService().getBrain(family.getId());
			Playlist otherServerPlaylist = findPlaylistServer(name, familyBrain);

			if (otherServerPlaylist != null) {
				return otherServerPlaylist;
			}
		}

		return null;
	}

	private String createPlaylistNameList(Lang lang, List<Playlist> playlist) {
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
