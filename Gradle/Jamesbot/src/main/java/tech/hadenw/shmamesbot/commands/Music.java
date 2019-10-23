package tech.hadenw.shmamesbot.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import tech.hadenw.shmamesbot.Errors;
import tech.hadenw.shmamesbot.Shmames;
import tech.hadenw.shmamesbot.GuildOcarina;

public class Music implements ICommand {
	@Override
	public String getDescription() {
		return "Play music to your current voice channel.";
	}
	
	@Override
	public String getUsage() {
		return "music <play|pause|stop|queue> [link] [move <from> <to>|clear]";
	}

	@Override
	public String run(String args, User author, Message message) {
		Matcher m = Pattern.compile("^(play|pause|stop|queue)\\w?(.+)?$", Pattern.CASE_INSENSITIVE).matcher(args);
		
		if(m.find()) {
			// Get the music handler.
			GuildOcarina ocarina = Shmames.getOcarina(message.getGuild().getId());
			
			switch(m.group(1).toLowerCase()) {
			case "play":
				// Get the channel to play in.
				VoiceChannel vchannel = message.getMember().getVoiceState().getChannel();
				
				// Join the channel.
				ocarina.connect(vchannel);
				
				// Load and play the requested item.
				ocarina.queueTrack(m.group(2).trim());
				
				return "Playing!";
			case "pause":
				ocarina.togglePause();
				
				return "Toggled";
			case "stop":
				ocarina.stopPlaying();
				
				return "Goodbye";
			case "queue":
				return "Under construction";
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
}
