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
					if(message.getMember().getVoiceState() != null){
						if(!ocarina.isInVoiceChannel()) {
							VoiceChannel vchannel = message.getMember().getVoiceState().getChannel();

							ocarina.connect(vchannel);
						}

						ocarina.loadTrack(m.group(2), false);
					}else{
						return "Please join a voice channel and run this command again.";
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
					break;
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
					// TODO check group 2 null

					if(m.group(2).equalsIgnoreCase("show")){
						// TODO check if connected
						// TODO default answer if empty queue
						StringBuilder sb = new StringBuilder();

						for(AudioTrack t : ocarina.getQueue()){
							if(sb.length() > 0) {
								sb.append("\n");
							}

							sb.append(ocarina.getQueue().indexOf(t)+1);
							sb.append(": ");
							sb.append("`");
							sb.append(t.getInfo().title);
							sb.append("`");
						}

						if(sb.length() == 0){
							sb.append("There are no tracks in the queue.");
						}

						showQueue(sb.toString(), message.getChannel());
						return "";
					}

					// Adds the selected item to the queue
					if(message.getMember().getVoiceState() != null){
						if(ocarina.isInVoiceChannel()) {
							// TODO is James Playlist or other?
							ocarina.loadTrack(m.group(2), true);
						}else{
							return "A track needs to be playing before you can add songs to the queue.";
						}
					}else{
						return "Please join a voice channel and run this command again.";
					}
					break;
				default:
					return Errors.formatUsage(Errors.WRONG_USAGE, getUsage());
			}
		}else{
			// TODO send command help
		}

		// TODO: check if playing on this server already, user is in a voice channel, etc.

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

		eBuilder.setColor(Color.blue);
		eBuilder.setAuthor(Shmames.getBotName()+" Music", null, Shmames.getJDA().getSelfUser().getAvatarUrl());
		eBuilder.setTitle(t.getInfo().title, t.getInfo().uri);
		eBuilder.setThumbnail(t.getInfo().uri);
		eBuilder.addField("Looping", o.isLooping() ? "Yes" : "No", true);
		eBuilder.addField("Position", getHumanTimeCode(t.getPosition()) + " / " + getHumanTimeCode(t.getDuration()), true);

		c.sendMessage(eBuilder.build()).queue();
	}

	private void showQueue(String queue, MessageChannel c) {
		EmbedBuilder eBuilder = new EmbedBuilder();

		eBuilder.setColor(Color.blue);
		eBuilder.setAuthor(Shmames.getBotName()+" Music Queue", null, Shmames.getJDA().getSelfUser().getAvatarUrl());
		eBuilder.addField("Up Next", queue, false);

		c.sendMessage(eBuilder.build()).queue();
	}

	private String getHumanTimeCode(long timeInMS) {
		int minutes = (int)Math.floor((timeInMS/1000d)/60d);
		int seconds = (int)((timeInMS/1000) - (minutes*60));

		return minutes+":"+(seconds < 10 ? "0" + seconds : seconds);
	}
}
