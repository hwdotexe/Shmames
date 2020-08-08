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
							// Get the channel to play in.
							VoiceChannel vchannel = message.getMember().getVoiceState().getChannel();

							// Join the channel.
							ocarina.connect(vchannel);
						}

						ocarina.queueItem(m.group(2).trim());
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
						showTrackData(track, message.getChannel());
					}else{
						return "Nothing is currently playing.";
					}

					break;
				case "playlist":
					break;
				case "queue":
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

	private void showTrackData(AudioTrack t, MessageChannel c) {
		EmbedBuilder eBuilder = new EmbedBuilder();

		eBuilder.setColor(Color.blue);
		eBuilder.setAuthor(Shmames.getBotName()+" Music", null, Shmames.getJDA().getSelfUser().getAvatarUrl());
		eBuilder.addField("Currently Playing", t.getInfo().title, false);
		eBuilder.addField("Position", getHumanTimeCode(t.getPosition()) + " / " + getHumanTimeCode(t.getDuration()), true);
		c.sendMessage(eBuilder.build()).queue();
	}

	private String getHumanTimeCode(long timeInMS) {
		int minutes = (int)Math.floor((timeInMS/1000d)/60d);
		int seconds = (int)((timeInMS/1000) - (minutes*60));

		return minutes+":"+seconds;
	}
}
