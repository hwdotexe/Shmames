package tech.hadenw.discordbot.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import tech.hadenw.discordbot.Errors;
import tech.hadenw.discordbot.GuildOcarina;
import tech.hadenw.discordbot.Shmames;

public class Music implements ICommand {
	@Override
	public String getDescription() {
		return "This command is currently in BETA and is not guaranteed to work properly in every case.";
	}
	
	@Override
	public String getUsage() {
		return "music <add|pause|stop|queue|loop|skip> [link] [move <from> <to>|clear]";
	}

	@Override
	public String run(String args, User author, Message message) {
		Matcher m = Pattern.compile("^(add|pause|stop|queue|loop|skip)\\w?(.+)?$", Pattern.CASE_INSENSITIVE).matcher(args);
		
		if(m.find()) {
			// Get the music handler.
			GuildOcarina ocarina = Shmames.getMusicManager().getOcarina(message.getGuild().getId());
			
			switch(m.group(1).toLowerCase()) {
			case "add":
				// If we're already playing something, play this song with current settings.
				// If we're not, we can join channel and go
				
				if(ocarina.isConnected()) {
					// Load the requested item.
					ocarina.loadTrack(m.group(2).trim());
				}else {
					// Get the channel to play in.
					VoiceChannel vchannel = message.getMember().getVoiceState().getChannel();
					
					// Join the channel.
					ocarina.connect(vchannel);
					
					// Load requested item.
					ocarina.loadTrack(m.group(2).trim());
				}
				
				return "Playing!";
			case "pause":
				ocarina.togglePause();
				
				return "Toggled";
			case "stop":
				ocarina.disconnect();
				
				return "Goodbye";
			case "queue":
				String str3 = "";
				
				for(AudioTrack t : ocarina.getQueue()) {
					if(str3.length()>0)
						str3 += "\n";
					
					str3 += ocarina.getQueue().indexOf(t) + 1;
					str3 += ": "+t.getInfo().title;
				}
				
				return "**The Queue:**\n"+str3;
			case "loop":
				return "Under construction";
			case "skip":
				ocarina.playNext();
				
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
}
