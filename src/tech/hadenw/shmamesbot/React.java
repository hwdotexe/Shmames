package tech.hadenw.shmamesbot;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.core.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import tech.hadenw.shmamesbot.commands.ICommand;

public class React extends ListenerAdapter {
	private List<Long> strikes;
	
	public React() {
		strikes = new ArrayList<Long>();
	}
	
	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent e) {
		ReactionEmote emo = e.getReaction().getReactionEmote();
		
		if(emo.isEmote() && e.getGuild().getEmotes().contains(emo.getEmote())) {
			if(emo.getEmote().getName().equalsIgnoreCase("roygun")) {
				
				strikeMessage(e.getMessageIdLong(), e);
			}
		}
	}
	
	@Override
	public void onMessageReactionRemove(MessageReactionRemoveEvent e) {
		ReactionEmote emo = e.getReaction().getReactionEmote();
		
		if(emo.isEmote() && e.getGuild().getEmotes().contains(emo.getEmote())) {
			if(emo.getEmote().getName().equalsIgnoreCase("roygun")) {
				if(strikes.contains(e.getMessageIdLong())) {
					strikes.remove(e.getMessageIdLong());
				}
			}
		}
	}
	
	private void strikeMessage(long id, MessageReactionAddEvent e) {
		if(strikes.contains(id)) {
			String name = e.getChannel().getMessageById(id).complete().getAuthor().getName();
			String toTally = "";
			
			// Change tally
			if(name.equalsIgnoreCase(Shmames.getJDA().getSelfUser().getName())){
				// It was James
				toTally = "badbot";
			}else {
				// It was not
				toTally = "bad"+name.toLowerCase();
			}
			
			e.getChannel().deleteMessageById(e.getMessageId()).queue();
			strikes.remove(id);
			
			for(ICommand c : CommandHandler.getLoadedCommands()) {
				for(String a : c.getAliases()) {
					if(a.equalsIgnoreCase("addtally")) {
						String response = c.run(toTally, Shmames.getJDA().getSelfUser(), e.getGuild());
						e.getChannel().sendMessage(response).queue();
						return;
					}
				}
			}
		}else {
			strikes.add(id);
		}
	}
}
