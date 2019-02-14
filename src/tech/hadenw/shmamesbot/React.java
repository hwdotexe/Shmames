package tech.hadenw.shmamesbot;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.core.entities.Message;
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

		if (e.getUser() != Shmames.getJDA().getSelfUser()) {
			for (Poll p : Shmames.getPolls()) {
				if (p.getMesssage().getId().equals(e.getMessageId())) {
					int vote = -1;

					try {
						vote = Integer.parseInt(emo.getName().substring(0, 1)) - 1;
					} catch (Exception ex) {
						return;
					}

					if (p.getVotes().containsKey(vote)) {
						p.getVotes().put(vote, p.getVotes().get(vote) + 1);
					}

					return;
				}
			}

			if (emo.isEmote() && e.getGuild().getEmotes().contains(emo.getEmote())) {
				if (emo.getEmote().getName().equalsIgnoreCase("roygun")) {
					strikeMessage(e.getMessageIdLong(), e);
				}
			}
		}
	}
	
	@Override
	public void onMessageReactionRemove(MessageReactionRemoveEvent e) {
		ReactionEmote emo = e.getReaction().getReactionEmote();
		
		if(e.getUser() != Shmames.getJDA().getSelfUser()) {
			for(Poll p : Shmames.getPolls()) {
				if(p.getMesssage().getIdLong() == e.getMessageIdLong()) {
					int vote = -1;
					
					try {
						vote = Integer.parseInt(emo.getName().substring(0, 1)) - 1;
					}catch(Exception ex) {
						break;
					}
					
					if(p.getVotes().containsKey(vote)) {
						p.getVotes().put(vote, p.getVotes().get(vote) - 1);
					}
					
					break;
				}
			}
			
			if(emo.isEmote() && e.getGuild().getEmotes().contains(emo.getEmote())) {
				if(emo.getEmote().getName().equalsIgnoreCase("roygun")) {
					if(strikes.contains(e.getMessageIdLong())) {
						strikes.remove(e.getMessageIdLong());
					}
				}
			}
		}
	}
	
	private void strikeMessage(long id, MessageReactionAddEvent e) {
		if(strikes.contains(id)) {
			Message m = e.getChannel().getMessageById(id).complete();
			String name = m.getAuthor().getName();
			String toTally = "";
			
			// Change tally
			if(name.equalsIgnoreCase(Shmames.getJDA().getSelfUser().getName())){
				// It was James
				toTally = "badbot";
			}else {
				// It was not
				toTally = "bad"+name.toLowerCase();
			}
			
			// Remove the message
			e.getChannel().deleteMessageById(e.getMessageId()).queue();
			strikes.remove(id);
			
			for(ICommand c : CommandHandler.getLoadedCommands()) {
				for(String a : c.getAliases()) {
					if(a.equalsIgnoreCase("addtally")) {
						String response = c.run(toTally, Shmames.getJDA().getSelfUser(), m);
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
