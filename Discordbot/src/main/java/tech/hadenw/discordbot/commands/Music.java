package tech.hadenw.discordbot.commands;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import tech.hadenw.discordbot.Errors;
import tech.hadenw.discordbot.GuildOcarina;
import tech.hadenw.discordbot.Shmames;
import tech.hadenw.discordbot.storage.Brain;
import tech.hadenw.discordbot.storage.Playlist;

public class Music implements ICommand {
	@Override
	public String getDescription() {
		return "";
	}
	
	@Override
	public String getUsage() {
		return "TBD";
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
					if(!ocarina.isInVoiceChannel()) {
						if(message.getMember().getVoiceState() != null){
							VoiceChannel vchannel = message.getMember().getVoiceState().getChannel();

							ocarina.connect(vchannel);
						} else {
							return "Please join a voice channel and run this command again.";
						}
					}

					if(isUrl(m.group(2))) {
						ocarina.loadTrack(m.group(2), false);
					} else {
						for(Playlist p : b.getPlaylists()) {
							if(p.getName().equalsIgnoreCase(m.group(2))){
								for(String url : p.getTracks()) {
									ocarina.loadTrack(url, true);
								}

								return "Playing the `"+p.getName()+"` playlist!";
							}
						}

						return "Please provide a media URL or Playlist name!";
					}

					break;
				case "pause":
				case "resume":
					ocarina.togglePause();
					break;
				case "skip":
					ocarina.skip();
					break;
				case "shuffle":
					ocarina.shuffleQueue();

					return "Shuffled the music queue!";
				case "stop":
					ocarina.stop();
					break;
				case "loop":
					boolean isLoop = ocarina.toggleLoop();
					return "Music looping is now **"+(isLoop ? "ON" : "OFF")+"**";
				case "playing":
					AudioTrack track = ocarina.getNowPlaying();

					if(track != null){
						showTrackData(track, message.getChannel(), ocarina);
					}else{
						return "Nothing is currently playing.";
					}

					break;
				case "playlist":
					if (m.group(2) != null) {
						return playlist(m.group(2), b, message.getChannel());
					}else{
						return Errors.WRONG_USAGE;
					}
				case "queue":
					if(ocarina.isInVoiceChannel()) {
						if (m.group(2) != null) {
							if (m.group(2).equalsIgnoreCase("list")) {
								StringBuilder sb = new StringBuilder();

								for (AudioTrack t : ocarina.getQueue()) {
									if (sb.length() > 0) {
										sb.append("\n");
									}

									sb.append(ocarina.getQueue().indexOf(t) + 1);
									sb.append(": ");
									sb.append("`");
									sb.append(t.getInfo().title);
									sb.append("`");
								}

								if (sb.length() == 0) {
									sb.append("There are no tracks in the queue.");
								}

								showQueue(sb.toString(), message.getChannel());
								return "";
							} else if (isUrl(m.group(2))) {
								ocarina.loadTrack(m.group(2), true);
								break;
							} else {
								for(Playlist p : b.getPlaylists()) {
									if(p.getName().equalsIgnoreCase(m.group(2))){
										for(String url : p.getTracks()) {
											ocarina.loadTrack(url, true);
										}

										return "Queued the `"+p.getName()+"` playlist!";
									}
								}

								return Errors.WRONG_USAGE;
							}
						} else {
							return Errors.WRONG_USAGE;
						}
					}else{
						return "I have to be connected to a voice channel in order to do that!";
					}
				default:
					return Errors.formatUsage(Errors.WRONG_USAGE, getUsage());
			}
		}else{
			sendCommandHelp(message.getChannel());

			return "";
		}

		return "";
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

	private String playlist(String args, Brain b, MessageChannel c) {
		Matcher m = Pattern.compile("^([a-z]+)\\s?([a-z0-9]+)?\\s?(https?:\\/\\/.+)?\\s?(\\d{1,3})?$", Pattern.CASE_INSENSITIVE).matcher(args);

		if(m.find()){
			String cmd = m.group(1).toLowerCase();
			String listName = m.group(2) != null ? m.group(2).toLowerCase() : "";

			switch(cmd) {
				case "create":

					if(getPlaylist(listName, b) == null) {
						b.getPlaylists().add(new Playlist(listName));
						return "Playlist `"+listName+"` created!";
					} else {
						return "A playlist with that name already exists on this server!";
					}
				case "add":
					if(m.group(3) != null) {
						Playlist p = getPlaylist(listName, b);

						if (p != null) {
							if(p.getTracks().size() < 50) {
								p.addTrack(m.group(3));
								return "Added track to playlist!";
							}else{
								return "Playlists currently support a max of 50 tracks!";
							}
						} else {
							return "That playlist doesn't exist!";
						}
					}else{
						return Errors.WRONG_USAGE;
					}
				case "list":
					if(listName.length() > 0) {
						Playlist pList = getPlaylist(listName, b);

						if (pList != null) {
							StringBuilder sb = new StringBuilder();
							EmbedBuilder eBuilder = buildBasicEmbed();

							for (String url : pList.getTracks()) {
								if (sb.length() > 0) {
									sb.append("\n");
								}

								sb.append(pList.getTracks().indexOf(url) + 1);
								sb.append(": `");
								sb.append(url);
								sb.append("`");
							}

							if (sb.length() == 0) {
								sb.append("There aren't any tracks in this playlist yet.");
							}

							eBuilder.addField("Playlist Tracks", sb.toString(), false);
							c.sendMessage(eBuilder.build()).queue();

							return "";
						} else {
							return "That playlist doesn't exist!";
						}
					}else{
						StringBuilder sb = new StringBuilder();
						EmbedBuilder eBuilder = buildBasicEmbed();

						for(Playlist p : b.getPlaylists()) {
							if(sb.length() > 0)
								sb.append(", ");

							sb.append("`");
							sb.append(p.getName());
							sb.append("`");
						}

						eBuilder.addField("Playlists", sb.toString(), false);
						c.sendMessage(eBuilder.build()).queue();

						return "";
					}
				case "remove":
					if(m.group(4) != null){
						Playlist pRemove = getPlaylist(listName, b);

						if (pRemove != null) {
							int pos = Integer.parseInt(m.group(4))-1;

							if(pRemove.removeTrack(pos)){
								return "Track removed!";
							}else{
								return "That item doesn't exist!";
							}
						} else {
							return "That playlist doesn't exist!";
						}
					}else{
						return Errors.WRONG_USAGE;
					}
				case "delete":
					Playlist pDelete = getPlaylist(listName, b);

					if (pDelete != null) {
						b.getPlaylists().remove(pDelete);
						return "Playlist deleted!";
					}else{
						return "That playlist doesn't exist!";
					}
				default:
					return Errors.COMMAND_NOT_FOUND;
			}
		} else {
			return Errors.WRONG_USAGE;
		}
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

	private void showQueue(String queue, MessageChannel c) {
		EmbedBuilder eBuilder = buildBasicEmbed();

		eBuilder.setTitle("Music Queue");
		eBuilder.addField("Up Next", queue, false);

		c.sendMessage(eBuilder.build()).queue();
	}

	private void sendCommandHelp(MessageChannel c) {
		StringBuilder sb = new StringBuilder();
		EmbedBuilder eBuilder = buildBasicEmbed();

		sb.append("`play <url|playlist>` - Begin playing a track or playlist.\n");
		sb.append("`pause` - Toggle pause.\n");
		sb.append("`shuffle` - Shuffles tracks in the queue.\n");
		sb.append("`skip` - Skip the current track.\n");
		sb.append("`stop` - Stop playing and disconnect from the channel.\n");
		sb.append("`loop` - Toggle track looping.\n");
		sb.append("`playing` - See details about the current track.\n");
		sb.append("`queue <list|url|playlist>` - Show the queue, or add an item to the queue.\n");
		sb.append("`playlist <create|add|list|remove|delete> <name> [url]` - Manage a playlist.");

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
		Matcher m = Pattern.compile("^https?:\\/\\/.+$", Pattern.CASE_INSENSITIVE).matcher(test);

		return m.find();
	}
}
