package tech.hadenw.shmamesbot;

import java.util.HashMap;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import tech.hadenw.shmamesbot.commands.ICommand;

public class React extends ListenerAdapter {
	private HashMap<Long, Integer> strikes;
	
	public React() {
		strikes = new HashMap<Long, Integer>();
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
				if (emo.getEmote().getName().equalsIgnoreCase(Shmames.getBrain().getRemovalEmoji())) {
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
				if(emo.getEmote().getName().equalsIgnoreCase(Shmames.getBrain().getRemovalEmoji())) {
					// TODO drop strike
				}
			}
		}
	}
	
	private void strikeMessage(long id, MessageReactionAddEvent e) {
		// Increment strike on this message
		strikes.put(id, strikes.containsKey(id) ? strikes.get(id) + 1 : 1);
		
		if(strikes.get(id) >= 3) {
			Message m = e.getChannel().getMessageById(id).complete();
			String name = m.getAuthor().getName();
			String toTally = name.equalsIgnoreCase(Shmames.getJDA().getSelfUser().getName()) ? "badbot" : "bad"+name;
			
			// Remove the message & process
			try {
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
			}catch(Exception ex) {
				e.getChannel().sendMessage("[No Permission] I wasn't able to delete that :/").queue();
			}
		}
	}
}
