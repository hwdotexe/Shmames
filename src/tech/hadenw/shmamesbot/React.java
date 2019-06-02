package tech.hadenw.shmamesbot;

import java.util.HashMap;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import tech.hadenw.shmamesbot.brain.BotSettings;
import tech.hadenw.shmamesbot.brain.Brain;
import tech.hadenw.shmamesbot.commands.ICommand;

public class React extends ListenerAdapter {
	private HashMap<Long, Integer> strikes;
	private HashMap<Long, Integer> votes;
	
	public React() {
		strikes = new HashMap<Long, Integer>();
		votes = new HashMap<Long, Integer>();
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
			
			if (emo.getName().equalsIgnoreCase(Shmames.getBrains().getBrain(e.getGuild().getId()).getSettings().get(BotSettings.REMOVAL_EMOTE))) {
				strikeMessage(e.getMessageIdLong(), e);
				return;
			}
			
			if (emo.getName().equalsIgnoreCase(Shmames.getBrains().getBrain(e.getGuild().getId()).getSettings().get(BotSettings.APPROVAL_EMOTE))) {
				voteMessage(e.getMessageIdLong(), e);
				return;
			}
			
			// Tally up the emote
			if(emo.isEmote()) {
				if(e.getGuild().getEmotes().contains(emo.getEmote())) {
					Brain b = Shmames.getBrains().getBrain(e.getGuild().getId());
					String name = emo.getName();
					
					if(b.getEmoteStats().containsKey(name)) {
						b.getEmoteStats().put(name, b.getEmoteStats().get(name)+1);
					}else {
						b.getEmoteStats().put(name, 1);
					}
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
			
			if (emo.getName().equalsIgnoreCase(Shmames.getBrains().getBrain(e.getGuild().getId()).getSettings().get(BotSettings.REMOVAL_EMOTE))) {
				long id = e.getMessageIdLong();
				
				if(strikes.containsKey(id)) {
					strikes.put(id, strikes.get(id) > 1 ? strikes.get(id) - 1 : 0);
					
					if(strikes.get(id) <= 0){
						strikes.remove(id);
					}
				}
			}
		}
	}
	
	private void strikeMessage(long id, MessageReactionAddEvent e) {
		// Increment strike on this message
		strikes.put(id, strikes.containsKey(id) ? strikes.get(id) + 1 : 1);
		
		int t = 3;
		try {
			t = Integer.parseInt(Shmames.getBrains().getBrain(e.getGuild().getId()).getSettings().get(BotSettings.REMOVAL_THRESHOLD));
		}catch(Exception ex) {}
		
		if(strikes.get(id) >= t) {
			Message m = e.getChannel().getMessageById(id).complete();
			String name = m.getAuthor().getName();
			name = name.replaceAll("\\s", "_").replaceAll("[\\W]", "").toLowerCase();
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
				e.getChannel().sendMessage(Errors.NO_PERMISSION_BOT).queue();
			}
		}
	}
	
	private void voteMessage(long id, MessageReactionAddEvent e) {
		// Increment vote on this message
		votes.put(id, votes.containsKey(id) ? votes.get(id) + 1 : 1);
		
		int t = 3;
		try {
			t = Integer.parseInt(Shmames.getBrains().getBrain(e.getGuild().getId()).getSettings().get(BotSettings.APPROVAL_THRESHOLD));
		}catch(Exception ex) {}
		
		if(votes.get(id) == t) {
			Message m = e.getChannel().getMessageById(id).complete();
			String name = m.getAuthor().getName();
			name = name.replaceAll("\\s", "_").replaceAll("[\\W]", "").toLowerCase();
			String toTally = name.equalsIgnoreCase(Shmames.getJDA().getSelfUser().getName()) ? "goodbot" : "good"+name;
			
			// Process
			for(ICommand c : CommandHandler.getLoadedCommands()) {
				for(String a : c.getAliases()) {
					if(a.equalsIgnoreCase("addtally")) {
						String response = c.run(toTally, Shmames.getJDA().getSelfUser(), m);
						e.getChannel().sendMessage(response).queue();
						return;
					}
				}
			}
		}
	}
}
