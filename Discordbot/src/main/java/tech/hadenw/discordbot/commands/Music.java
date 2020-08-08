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
		Matcher m = Pattern.compile("^(.+)\\s?(.+)?$", Pattern.CASE_INSENSITIVE).matcher(args);

		if(m.find()){
			String mainCmd = m.group(1);
			GuildOcarina ocarina = Shmames.getMusicManager().getOcarina(message.getGuild().getId());

			switch(mainCmd) {
				case "play":
					if(ocarina.isInVoiceChannel()) {
						// Load the requested item.
						ocarina.queueItem(m.group(2).trim());
					}else {
						// Get the channel to play in.
						VoiceChannel vchannel = message.getMember().getVoiceState().getChannel();

						// Join the channel.
						ocarina.connect(vchannel);

						// Load requested item.
						ocarina.queueItem(m.group(2).trim());
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

		if(m.find()) {
			// Get the music handler.
			GuildOcarina ocarina = Shmames.getMusicManager().getOcarina(message.getGuild().getId());
			
			switch(m.group(1).toLowerCase()) {
			case "add":
				// If we're already playing something, play this song with current settings.
				// If we're not, we can join channel and go
				
				if(ocarina.isInVoiceChannel()) {
					// Load the requested item.
					ocarina.queueItem(m.group(2).trim());
				}else {
					// Get the channel to play in.
					VoiceChannel vchannel = message.getMember().getVoiceState().getChannel();
					
					// Join the channel.
					ocarina.connect(vchannel);
					
					// Load requested item.
					ocarina.queueItem(m.group(2).trim());
				}
				
				return "Playing!";
			case "pause":
				ocarina.togglePause();
				
				return "Toggled";
			case "stop":
				ocarina.stop();
				
				return "Goodbye";
			case "queue":
				StringBuilder qsb = new StringBuilder();
				
				for(AudioTrack t : ocarina.getQueue()) {
					if(qsb.length()>0)
						qsb.append("\n");
					
					qsb.append(ocarina.getQueue().indexOf(t) + 1);
					qsb.append(": ").append(t.getInfo().title);
				}
				
				return "**The Queue:**\n"+qsb;
			case "loop":
				return "Under construction";
			case "skip":
				ocarina.skip();
				
				return "Skipped!";
			}
		}else {
			return Errors.formatUsage(Errors.WRONG_USAGE, this.getUsage());
		}
		// TODO: check if playing on this server already, user is in a voice channel, etc.
		
		
		
		return "It is done";
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
		double minutes = Math.floor((timeInMS/1000d)/60d);
		double seconds = (timeInMS/1000d) - (minutes*60);

		return minutes+":"+seconds;
	}
}
