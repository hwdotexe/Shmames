package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.App;
import com.hadenwatne.shmames.commandbuilder.*;
import com.hadenwatne.shmames.enums.*;
import com.hadenwatne.shmames.models.PaginatedList;
import com.hadenwatne.shmames.models.Playlist;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import com.hadenwatne.shmames.models.command.ExecutingCommandArguments;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Language;
import com.hadenwatne.shmames.music.GuildOcarina;
import com.hadenwatne.shmames.services.PaginationService;
import com.hadenwatne.shmames.services.ShmamesService;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Music extends Command {
	private final int PLAYLIST_TRACK_MAXIMUM = 50;

	public Music() {
		super(true);
	}

	@Override
	protected Permission[] configureRequiredBotPermissions() {
		return new Permission[]{Permission.MESSAGE_SEND, Permission.MESSAGE_SEND_IN_THREADS, Permission.MESSAGE_EMBED_LINKS, Permission.VOICE_CONNECT, Permission.VOICE_SPEAK};
	}

	@Override
	protected CommandStructure buildCommandStructure() {
		return CommandBuilder.Create("music", "Play music, create playlists, and chill out.")
				.addAlias("bops")
				.addSubCommands(
						CommandBuilder.Create("play", "Play a track or playlist.")
								.addAlias("p")
								.addParameters(
										new CommandParameter("playlistName", "The playlist to play.", ParameterType.STRING, false)
												.setPattern(RegexPatterns.ALPHANUMERIC.getPattern()),
										new CommandParameter("URL", "The URL of the song to play.", ParameterType.STRING, false)
												.setPattern(RegexPatterns.URL.getPattern())
												.setExample("url/")
								)
								.build(),
						CommandBuilder.Create("pause", "Pause any playing music.")
								.build(),
						CommandBuilder.Create("resume", "Resume after pausing.")
								.addAlias("r")
								.build(),
						CommandBuilder.Create("skip", "Skip the current track.")
								.addParameters(
										new CommandParameter("number", "How many tracks to skip.", ParameterType.INTEGER, false)
												.setExample("3")
								)
								.build(),
						CommandBuilder.Create("stop", "Stop playing music.")
								.build(),
						CommandBuilder.Create("loop", "Toggle track looping.")
								.build(),
						CommandBuilder.Create("loopqueue", "Toggle queue looping.")
								.build(),
						CommandBuilder.Create("playing", "See what's playing.")
								.addAlias("np")
								.build(),
						CommandBuilder.Create("convert", "Convert the queue to a playlist.")
								.addParameters(
										new CommandParameter("playlistName", "The name to use for the new playlist.", ParameterType.STRING)
												.setPattern(RegexPatterns.ALPHANUMERIC.getPattern())
												.setExample("newPlaylist")
								)
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
																.setExample("newPlaylist")
												)
												.build(),
										CommandBuilder.Create("add", "Add a track to a playlist.")
												.addAlias("a")
												.addParameters(
														new CommandParameter("playlistName", "The name of the playlist to add to.", ParameterType.STRING)
																.setPattern(RegexPatterns.ALPHANUMERIC.getPattern())
																.setExample("newPlaylist"),
														new CommandParameter("URL", "The URL of the track to add.", ParameterType.STRING)
																.setPattern(RegexPatterns.URL.getPattern())
																.setExample("url/"),
														new CommandParameter("memo", "A memo about the track being added.", ParameterType.STRING, false)
																.setExample("great song")
												)
												.build(),
										CommandBuilder.Create("list", "Show available playlists.")
												.addAlias("l")
												.addParameters(
														new CommandParameter("playlistName", "The name of the playlist.", ParameterType.STRING, false)
																.setPattern(RegexPatterns.ALPHANUMERIC.getPattern())
																.setExample("newPlaylist"),
														new CommandParameter("page", "The page to view.", ParameterType.INTEGER, false)
																.setExample("2")
												)
												.build(),
										CommandBuilder.Create("remove", "Remove a track from a playlist.")
												.addAlias("r")
												.addParameters(
														new CommandParameter("playlistName", "The playlist to remove an item from.", ParameterType.STRING)
																.setPattern(RegexPatterns.ALPHANUMERIC.getPattern())
																.setExample("newPlaylist"),
														new CommandParameter("position", "The position of the item to remove.", ParameterType.INTEGER)
																.setExample("3")
												)
												.build(),
										CommandBuilder.Create("delete", "Delete a playlist.")
												.addAlias("d")
												.addParameters(
														new CommandParameter("playlistName", "The name of the playlist to delete.", ParameterType.STRING)
																.setPattern(RegexPatterns.ALPHANUMERIC.getPattern())
																.setExample("newPlaylist")
												)
												.build()
								),
						new SubCommandGroup("queue", "Manage the queue.")
								.addAlias("q")
								.addSubCommands(
										CommandBuilder.Create("clear", "Clear the list of upcoming tracks.")
												.addAlias("c")
												.build(),
										CommandBuilder.Create("reverse", "Reverse the order of the queue.")
												.addAlias("r")
												.build(),
										CommandBuilder.Create("shuffle", "Shuffle the upcoming tracks.")
												.addAlias("s")
												.build(),
										CommandBuilder.Create("append", "Add more tracks or playlists to the queue.")
												.addAlias("a")
												.addParameters(
														new CommandParameter("playlistName", "The playlist to append.", ParameterType.STRING, false)
																.setPattern(RegexPatterns.ALPHANUMERIC.getPattern()),
														new CommandParameter("URL", "The URL of the song to append.", ParameterType.STRING, false)
																.setPattern(RegexPatterns.URL.getPattern())
																.setExample("url/")
												)
												.build(),
										CommandBuilder.Create("view", "View the queue.")
												.addAlias("v")
												.addParameters(
														new CommandParameter("page", "The page of the queue to view.", ParameterType.INTEGER, false)
																.setExample("4")
												)
												.build()
								)
				)
				.build();
	}

	@Override
	public EmbedBuilder run(ExecutingCommand executingCommand) {
		String subCommand = executingCommand.getSubCommand();
		String subCommandGroup = executingCommand.getSubCommandGroup();
		Language language = executingCommand.getLanguage();
		Brain brain = executingCommand.getBrain();
		Guild server = executingCommand.getServer();
		Member member = executingCommand.getAuthorMember();
		GuildOcarina ocarina = App.Shmames.getMusicManager().getOcarina(server.getId());

		switch (subCommandGroup) {
			case "playlist":
				if (canUse(server, brain, member)) {
					return cmdPlaylist(brain, language, executingCommand);
				} else {
					return response(EmbedType.ERROR, ErrorKeys.NO_PERMISSION_USER.name())
							.setDescription(language.getError(ErrorKeys.NO_PERMISSION_USER));
				}
			case "queue":
				if (canUse(server, brain, member)) {
					return cmdQueue(brain, language, ocarina, server, executingCommand);
				} else {
					return response(EmbedType.ERROR, ErrorKeys.NO_PERMISSION_USER.name())
							.setDescription(language.getError(ErrorKeys.NO_PERMISSION_USER));
				}
		}

		switch (subCommand) {
			case "play":
				if (canUse(server, brain, member)) {
					return cmdPlay(language, brain, ocarina, executingCommand);
				} else {
					return response(EmbedType.ERROR, ErrorKeys.NO_PERMISSION_USER.name())
							.setDescription(language.getError(ErrorKeys.NO_PERMISSION_USER));
				}
			case "pause":
				ocarina.togglePause(true);

				return response(EmbedType.SUCCESS)
						.setDescription(language.getMsg(LanguageKeys.GENERIC_SUCCESS));
			case "resume":
				ocarina.togglePause(false);

				return response(EmbedType.SUCCESS)
						.setDescription(language.getMsg(LanguageKeys.GENERIC_SUCCESS));
			case "skip":
				if (ocarina.getNowPlaying() != null) {
					if (canUse(server, brain, member)) {
						int times = executingCommand.getCommandArguments().getAsInteger("number");

						cmdSkip(ocarina, times);

						return response(EmbedType.SUCCESS)
								.setDescription(language.getMsg(LanguageKeys.GENERIC_SUCCESS));
					} else {
						return response(EmbedType.ERROR, ErrorKeys.NO_PERMISSION_USER.name())
								.setDescription(language.getError(ErrorKeys.NO_PERMISSION_USER));
					}
				} else {
					return response(EmbedType.ERROR, ErrorKeys.TRACK_NOT_PLAYING.name())
							.setDescription(language.getError(ErrorKeys.TRACK_NOT_PLAYING));
				}
			case "stop":
				if (ocarina.isInVoiceChannel()) {
					if (canUse(server, brain, member)) {
						ocarina.stop();

						return response(EmbedType.SUCCESS)
								.setDescription(language.getMsg(LanguageKeys.GENERIC_SUCCESS));
					} else {
						return response(EmbedType.ERROR, ErrorKeys.NO_PERMISSION_USER.name())
								.setDescription(language.getError(ErrorKeys.NO_PERMISSION_USER));
					}
				} else {
					return response(EmbedType.ERROR, ErrorKeys.TRACK_NOT_PLAYING.name())
							.setDescription(language.getError(ErrorKeys.TRACK_NOT_PLAYING));
				}
			case "loop":
				if (canUse(server, brain, member)) {
					return cmdLoop(language, ocarina);
				} else {
					return response(EmbedType.ERROR, ErrorKeys.NO_PERMISSION_USER.name())
							.setDescription(language.getError(ErrorKeys.NO_PERMISSION_USER));
				}
			case "loopqueue":
				if (canUse(server, brain, member)) {
					return cmdLoopQueue(language, ocarina);
				} else {
					return response(EmbedType.ERROR, ErrorKeys.NO_PERMISSION_USER.name())
							.setDescription(language.getError(ErrorKeys.NO_PERMISSION_USER));
				}
			case "playing":
				AudioTrack track = ocarina.getNowPlaying();

				if (track != null) {
					return showTrackData(track, ocarina);
				} else {
					return response(EmbedType.ERROR, ErrorKeys.TRACK_NOT_PLAYING.name())
							.setDescription(language.getError(ErrorKeys.TRACK_NOT_PLAYING));
				}
			case "convert":
				if (canUse(server, brain, member)) {
					return cmdConvert(brain, language, ocarina, executingCommand.getCommandArguments());
				} else {
					return response(EmbedType.ERROR, ErrorKeys.NO_PERMISSION_USER.name())
							.setDescription(language.getError(ErrorKeys.NO_PERMISSION_USER));
				}
		}

		return null;
	}

	private EmbedBuilder cmdPlay(Language language, Brain brain, GuildOcarina ocarina, ExecutingCommand executingCommand) {
		String playlist = executingCommand.getCommandArguments().getAsString("playlistName");
		String url = executingCommand.getCommandArguments().getAsString("URL");
		Member member = executingCommand.getAuthorMember();

		if (playlist != null || url != null) {
			// If the bot is not in a voice channel yet, add them to the user's channel.
			if (!ocarina.isInVoiceChannel()) {
				if (member.getVoiceState() != null && member.getVoiceState().inAudioChannel()) {
					AudioChannel channel = member.getVoiceState().getChannel();

					if(executingCommand.getServer().getSelfMember().hasPermission(channel, Permission.VOICE_CONNECT, Permission.VOICE_SPEAK)) {
						ocarina.connect(channel, executingCommand.getChannel());
					}else {
						return response(EmbedType.ERROR, ErrorKeys.PERMISSION_MISSING.name())
								.setDescription(language.getError(ErrorKeys.PERMISSION_MISSING, new String[]{App.Shmames.getBotName(), Permission.VOICE_SPEAK.getName() + System.lineSeparator() + Permission.VOICE_CONNECT.getName()}));
					}
				} else {
					return response(EmbedType.ERROR, ErrorKeys.MUSIC_NOT_IN_CHANNEL.name())
							.setDescription(language.getError(ErrorKeys.MUSIC_NOT_IN_CHANNEL));
				}
			}

			if (url != null) {
				ocarina.loadTrack(url, false);

				return response(EmbedType.SUCCESS)
						.setDescription(language.getMsg(LanguageKeys.MUSIC_PLAYING));
			} else {
				Playlist pl = findPlaylistFamily(playlist, brain, executingCommand.getServer());

				if (pl != null) {
					ocarina.loadCustomPlaylist(pl.getTracks(), false);

					return response(EmbedType.SUCCESS)
							.setDescription(language.getMsg(LanguageKeys.MUSIC_PLAYING_PLAYLIST, new String[]{pl.getName()}));
				} else {
					return response(EmbedType.ERROR, ErrorKeys.ITEMS_NOT_FOUND.name())
							.setDescription(language.getError(ErrorKeys.ITEMS_NOT_FOUND));
				}
			}
		} else {
			// No options provided; resume if paused, or send an error.
			if (ocarina.isPaused()) {
				ocarina.togglePause(false);

				return response(EmbedType.SUCCESS)
						.setDescription(language.getMsg(LanguageKeys.GENERIC_SUCCESS));
			} else {
				return response(EmbedType.ERROR, ErrorKeys.MUSIC_WRONG_INPUT.name())
						.setDescription(language.getError(ErrorKeys.MUSIC_WRONG_INPUT));
			}
		}
	}

	private void cmdSkip(GuildOcarina ocarina, int times) {
		if (times > 0) {
			ocarina.skipMany(times);
		} else {
			ocarina.skip();
		}
	}

	private EmbedBuilder cmdLoop(Language language, GuildOcarina ocarina) {
		boolean isLoop = ocarina.toggleLoop();

		return response(EmbedType.SUCCESS)
				.setDescription(language.getMsg(LanguageKeys.MUSIC_LOOPING_TOGGLED, new String[]{isLoop ? "ON" : "OFF"}));
	}

	private EmbedBuilder cmdLoopQueue(Language language, GuildOcarina ocarina) {
		boolean isLoopQueue = ocarina.toggleLoopQueue();

		return response(EmbedType.SUCCESS)
				.setDescription(language.getMsg(LanguageKeys.MUSIC_LOOPING_QUEUE_TOGGLED, new String[]{isLoopQueue ? "ON" : "OFF"}));
	}

	private EmbedBuilder cmdConvert(Brain brain, Language language, GuildOcarina ocarina, ExecutingCommandArguments args) {
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

				return response(EmbedType.SUCCESS)
						.setDescription(language.getMsg(LanguageKeys.MUSIC_PLAYLIST_CONVERTED, new String[]{newPlaylistName, Integer.toString(p.getTracks().size())}));
			} else {
				return response(EmbedType.ERROR, ErrorKeys.MUSIC_PLAYLIST_ALREADY_EXISTS.name())
						.setDescription(language.getError(ErrorKeys.MUSIC_PLAYLIST_ALREADY_EXISTS));
			}
		} else {
			return response(EmbedType.ERROR, ErrorKeys.TRACK_NOT_PLAYING.name())
					.setDescription(language.getError(ErrorKeys.TRACK_NOT_PLAYING));
		}
	}

	private EmbedBuilder cmdQueue(Brain brain, Language language, GuildOcarina ocarina, Guild server, ExecutingCommand executingCommand) {
		ExecutingCommandArguments args = executingCommand.getCommandArguments();
		String subCommand = executingCommand.getSubCommand();

		if (ocarina.isInVoiceChannel()) {
			switch (subCommand) {
				case "append":
					String appendPlaylist = args.getAsString("playlistName");
					String appendURL = args.getAsString("URL");

					return cmdQueueAppend(language, brain, server, ocarina, appendURL, appendPlaylist);
				case "reverse":
					ocarina.reverseQueue();

					return response(EmbedType.SUCCESS)
							.setDescription(language.getMsg(LanguageKeys.MUSIC_QUEUE_REVERSED));
				case "shuffle":
					ocarina.shuffleQueue();

					return response(EmbedType.SUCCESS)
							.setDescription(language.getMsg(LanguageKeys.MUSIC_QUEUE_SHUFFLED));
				case "clear":
					ocarina.getQueue().clear();

					return response(EmbedType.SUCCESS)
							.setDescription(language.getMsg(LanguageKeys.MUSIC_QUEUE_CLEARED));
				case "view":
					int queuePage = args.getAsInteger("page");

					return showQueue(language, ocarina.getQueue(), Math.max(queuePage, 1));
			}
		} else {
			return response(EmbedType.ERROR, ErrorKeys.MUSIC_NOT_IN_CHANNEL.name())
					.setDescription(language.getError(ErrorKeys.MUSIC_NOT_IN_CHANNEL));
		}

		return null;
	}

	private EmbedBuilder cmdQueueAppend(Language language, Brain brain, Guild server, GuildOcarina ocarina, String appendURL, String appendPlaylist) {
		if (appendURL != null) {
			ocarina.loadTrack(appendURL, true);

			return response(EmbedType.SUCCESS)
					.setDescription(language.getMsg(LanguageKeys.MUSIC_ADDED_TO_QUEUE));
		} else if (appendPlaylist != null) {
			Playlist pl = findPlaylistFamily(appendPlaylist, brain, server);

			if (pl != null) {
				ocarina.loadCustomPlaylist(pl.getTracks(), true);

				return response(EmbedType.SUCCESS)
						.setDescription(language.getMsg(LanguageKeys.MUSIC_QUEUED_PLAYLIST, new String[]{pl.getName()}));
			} else {
				return response(EmbedType.ERROR, ErrorKeys.ITEMS_NOT_FOUND.name())
						.setDescription(language.getError(ErrorKeys.ITEMS_NOT_FOUND));
			}
		} else {
			return response(EmbedType.ERROR, ErrorKeys.MUSIC_WRONG_INPUT.name())
					.setDescription(language.getError(ErrorKeys.MUSIC_WRONG_INPUT));
		}
	}

	private EmbedBuilder cmdPlaylist(Brain brain, Language language, ExecutingCommand executingCommand) {
		ExecutingCommandArguments args = executingCommand.getCommandArguments();
		String subCommand = executingCommand.getSubCommand();

		switch (subCommand) {
			case "create":
				String createPlaylistName = args.getAsString("playlistName");

				return cmdPlaylistCreate(language, brain, createPlaylistName);
			case "add":
				String addPlaylistName = args.getAsString("playlistName");
				String addPlaylistURL = args.getAsString("URL");
				String addPlaylistMemo = args.getAsString("memo");

				return cmdPlaylistAdd(language, brain, addPlaylistName, addPlaylistURL, addPlaylistMemo);
			case "list":
				String listPlaylistName = args.getAsString("playlistName");
				int listPlaylistPage = args.getAsInteger("page");

				return cmdPlaylistList(language, brain, listPlaylistName, listPlaylistPage);
			case "remove":
				String removePlaylist = args.getAsString("playlistName");
				int removePosition = args.getAsInteger("position");

				return cmdPlaylistRemove(language, brain, removePlaylist, removePosition);
			case "delete":
				String deletePlaylist = args.getAsString("playlistName");

				return cmdPlaylistDelete(language, brain, deletePlaylist);
		}

		return null;
	}

	private EmbedBuilder cmdPlaylistCreate(Language language, Brain brain, String listName) {
		if (findPlaylistServer(listName, brain) == null) {
			Playlist newList = new Playlist(listName);

			brain.getPlaylists().add(newList);

			return response(EmbedType.SUCCESS)
					.setDescription(language.getMsg(LanguageKeys.MUSIC_PLAYLIST_CREATED, new String[]{listName}));
		} else {
			return response(EmbedType.ERROR, ErrorKeys.MUSIC_PLAYLIST_ALREADY_EXISTS.name())
					.setDescription(language.getError(ErrorKeys.MUSIC_PLAYLIST_ALREADY_EXISTS));
		}
	}

	private EmbedBuilder cmdPlaylistAdd(Language language, Brain brain, String listName, String url, String memo) {
		Playlist p = findPlaylistServer(listName, brain);

		if (p != null) {
			if (p.getTracks().size() < PLAYLIST_TRACK_MAXIMUM) {
				p.addTrack(url, memo);

				return response(EmbedType.SUCCESS)
						.setDescription(language.getMsg(LanguageKeys.MUSIC_PLAYLIST_TRACK_ADDED));
			} else {
				return response(EmbedType.ERROR, ErrorKeys.MUSIC_PLAYLIST_TRACK_MAXIMUM_REACHED.name())
						.setDescription(language.getError(ErrorKeys.MUSIC_PLAYLIST_TRACK_MAXIMUM_REACHED));
			}
		} else {
			return response(EmbedType.ERROR, ErrorKeys.NOT_FOUND.name())
					.setDescription(language.getError(ErrorKeys.NOT_FOUND));
		}
	}

	private EmbedBuilder cmdPlaylistList(Language language, Brain brain, String listName, int page) {
		if (listName != null) {
			Playlist pList = findPlaylistServer(listName, brain);

			if (pList != null) {
				int viewPage = page > 0 ? page : 1;

				return showList(language, pList, viewPage);
			} else {
				return response(EmbedType.ERROR, ErrorKeys.NOT_FOUND.name())
						.setDescription(language.getError(ErrorKeys.NOT_FOUND));
			}
		}

		// Just list out this server's playlists
		EmbedBuilder eBuilder = response(EmbedType.INFO);
		String playlistNames = createPlaylistNameList(language, brain.getPlaylists());

		eBuilder.addField("Playlists", playlistNames, false);

		return eBuilder;
	}

	private EmbedBuilder cmdPlaylistRemove(Language language, Brain brain, String listName, int position) {
		Playlist pRemove = findPlaylistServer(listName, brain);

		if (pRemove != null) {
			if (pRemove.removeTrack(position - 1)) {
				return response(EmbedType.SUCCESS)
						.setDescription(language.getMsg(LanguageKeys.MUSIC_PLAYLIST_TRACK_REMOVED));
			} else {
				return response(EmbedType.ERROR, ErrorKeys.NOT_FOUND.name())
						.setDescription(language.getError(ErrorKeys.NOT_FOUND));
			}
		} else {
			return response(EmbedType.ERROR, ErrorKeys.MUSIC_PLAYLIST_DOESNT_EXIST.name())
					.setDescription(language.getError(ErrorKeys.MUSIC_PLAYLIST_DOESNT_EXIST));
		}
	}

	private EmbedBuilder cmdPlaylistDelete(Language language, Brain brain, String listName) {
		Playlist pDelete = findPlaylistServer(listName, brain);

		if (pDelete != null) {
			brain.getPlaylists().remove(pDelete);

			return response(EmbedType.SUCCESS)
					.setDescription(language.getMsg(LanguageKeys.MUSIC_PLAYLIST_DELETED));
		} else {
			return response(EmbedType.ERROR, ErrorKeys.MUSIC_PLAYLIST_DOESNT_EXIST.name())
					.setDescription(language.getError(ErrorKeys.MUSIC_PLAYLIST_DOESNT_EXIST));
		}
	}

	private EmbedBuilder showTrackData(AudioTrack t, GuildOcarina o) {
		EmbedBuilder eBuilder = response(EmbedType.INFO);

		if (!t.getInfo().isStream) {
			String videoID = extractVideoID(t.getInfo().uri);

			if (videoID != null) {
				eBuilder.setThumbnail("http://img.youtube.com/vi/" + videoID + "/1.jpg");
			}

			eBuilder.setTitle(t.getInfo().title, t.getInfo().uri);
			eBuilder.addField("Looping", o.isLooping() ? "Yes" : "No", true);
			eBuilder.addField("Position", getHumanTimeCode(t.getPosition()) + " / " + getHumanTimeCode(t.getDuration()), true);
		} else {
			eBuilder.setThumbnail("https://www.screensaversplanet.com/img/screenshots/screensavers/large/the-matrix-1.png");
			eBuilder.setTitle("Livestream");
			eBuilder.addField("Info", App.Shmames.getBotName() + " is currently playing from an audio livestream.", false);
		}

		return eBuilder;
	}

	private EmbedBuilder showQueue(Language language, List<AudioTrack> queue, int page) {
		List<String> trackTitles = new ArrayList<>();

		for (AudioTrack track : queue) {
			trackTitles.add(track.getInfo().title);
		}

		PaginatedList paginatedList = PaginationService.GetPaginatedList(trackTitles, 10, -1, true);

		return PaginationService.DrawEmbedPage(paginatedList, Math.max(1, page), "Music Queue", Color.ORANGE, language);
	}

	private EmbedBuilder showList(Language language, Playlist playlist, int page) {
		List<String> playlistTracks = new ArrayList<>();

		for (String trackURL : playlist.getTracks()) {
			String memo = playlist.getMemo(trackURL);
			String trackText = "";

			if (memo != null && memo.length() > 0) {
				trackText = memo + " - ";
			}

			trackText = trackText + "`" + trackURL + "`";

			playlistTracks.add(trackText);
		}

		PaginatedList paginatedList = PaginationService.GetPaginatedList(playlistTracks, 10, -1, true);

		return PaginationService.DrawEmbedPage(paginatedList, Math.max(1, page), "Playlist \"" + playlist.getName() + "\"", Color.ORANGE, language);
	}

	private String getHumanTimeCode(long timeInMS) {
		int minutes = (int) Math.floor((timeInMS / 1000d) / 60d);
		int seconds = (int) ((timeInMS / 1000) - (minutes * 60));

		return minutes + ":" + (seconds < 10 ? "0" + seconds : seconds);
	}

	private String extractVideoID(String url) {
		Matcher m = Pattern.compile(".+v=([a-z0-9\\-]+)(&.+)?", Pattern.CASE_INSENSITIVE).matcher(url);

		if (m.find()) {
			return m.group(1);
		} else {
			return null;
		}
	}

	private boolean canUse(Guild server, Brain brain, Member member) {
		return ShmamesService.CheckUserPermission(server, brain.getSettingFor(BotSettingName.MANAGE_MUSIC), member);
	}

	private Playlist findPlaylistServer(String name, Brain b) {
		if (name.length() > 0) {
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
		for (Guild family : ShmamesService.GetConnectedFamilyGuilds(brain, server)) {
			Brain familyBrain = App.Shmames.getStorageService().getBrain(family.getId());
			Playlist otherServerPlaylist = findPlaylistServer(name, familyBrain);

			if (otherServerPlaylist != null) {
				return otherServerPlaylist;
			}
		}

		return null;
	}

	private String createPlaylistNameList(Language language, List<Playlist> playlist) {
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
			sb.append(language.getError(ErrorKeys.MUSIC_PLAYLIST_LIST_EMPTY));
		}

		return sb.toString();
	}
}