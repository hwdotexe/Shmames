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

					ocarina.loadTrack(m.group(2), false);

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
					// Create <name>, Add <name> <url>, Delete <name>, Remove <name> <item#>, List [name]
					return "This is still under construction";
				case "queue":
					if(ocarina.isInVoiceChannel()) {
						if (m.group(2) != null) {
							if (m.group(2).equalsIgnoreCase("show")) {
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
								// TODO is James Playlist or other?
								ocarina.loadTrack(m.group(2), true);
								break;
							} else {
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

	private void showTrackData(AudioTrack t, MessageChannel c, GuildOcarina o) {
		EmbedBuilder eBuilder = new EmbedBuilder();
		String videoID = extractVideoID(t.getInfo().uri);

		if(videoID != null) {
			eBuilder.setThumbnail("http://img.youtube.com/vi/"+videoID+"/2.jpg");
		}

		eBuilder.setColor(new Color(43, 164, 188));
		eBuilder.setAuthor(Shmames.getBotName()+" Music", null, Shmames.getJDA().getSelfUser().getAvatarUrl());
		eBuilder.setTitle(t.getInfo().title, t.getInfo().uri);
		eBuilder.addField("Looping", o.isLooping() ? "Yes" : "No", true);
		eBuilder.addField("Position", getHumanTimeCode(t.getPosition()) + " / " + getHumanTimeCode(t.getDuration()), true);

		c.sendMessage(eBuilder.build()).queue();
	}

	private void showQueue(String queue, MessageChannel c) {
		EmbedBuilder eBuilder = new EmbedBuilder();

		eBuilder.setColor(new Color(43, 164, 188));
		eBuilder.setAuthor(Shmames.getBotName()+" Music Queue", null, Shmames.getJDA().getSelfUser().getAvatarUrl());
		eBuilder.addField("Up Next", queue, false);

		c.sendMessage(eBuilder.build()).queue();
	}

	private void sendCommandHelp(MessageChannel c) {
		StringBuilder sb = new StringBuilder();
		EmbedBuilder eBuilder = new EmbedBuilder();

		sb.append("`play <url|playlist>` - Begin playing a track or playlist.");
		sb.append("`pause` - Toggle pause.");
		sb.append("`shuffle` - Shuffles tracks in the queue.");
		sb.append("`skip` - Skip the current track.");
		sb.append("`stop` - Stop playing and disconnect from the channel.");
		sb.append("`loop` - Toggle track looping.");
		sb.append("`playing` - See details about the current track.");
		sb.append("`queue <show|url|playlist>` - Show the queue, or add an item to the queue.");
		sb.append("`playlist <create|add|list|remove|delete> <name> [url]` - Manage a playlist.");

		eBuilder.setColor(new Color(43, 164, 188));
		eBuilder.setAuthor(Shmames.getBotName()+" Music", null, Shmames.getJDA().getSelfUser().getAvatarUrl());
		eBuilder.addField("Commands", sb.toString(), false);

		c.sendMessage(eBuilder.build()).queue();
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
