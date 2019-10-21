package tech.hadenw.shmamesbot;

import java.util.HashMap;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import tech.hadenw.shmamesbot.brain.BotSettingName;
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
		if (e.getUser() != Shmames.getJDA().getSelfUser()) {
			ReactionEmote emo = e.getReaction().getReactionEmote();
			Brain b = Shmames.getBrains().getBrain(e.getGuild().getId());
			
			// Tally up the emote
			if(emo.isEmote()) {
				if(e.getGuild().getEmotes().contains(emo.getEmote())) {
					String name = emo.getName();
					
					if(b.getEmoteStats().containsKey(name)) {
						b.getEmoteStats().put(name, b.getEmoteStats().get(name)+1);
					}else {
						b.getEmoteStats().put(name, 1);
					}
				}
			}
			
			// Removal emotes
			if (emo.getName().equalsIgnoreCase(Shmames.getBrains().getBrain(e.getGuild().getId()).getSettingFor(BotSettingName.REMOVAL_EMOTE).getValue())) {
				strikeMessage(e.getMessageIdLong(), e);
				return;
			}
			
			// Approval emotes
			if (emo.getName().equalsIgnoreCase(Shmames.getBrains().getBrain(e.getGuild().getId()).getSettingFor(BotSettingName.APPROVAL_EMOTE).getValue())) {
				voteMessage(e.getMessageIdLong(), e);
				return;
			}
		}
	}
	
	@Override
	public void onMessageReactionRemove(MessageReactionRemoveEvent e) {
		if (e.getUser() != Shmames.getJDA().getSelfUser()) {
			ReactionEmote emo = e.getReaction().getReactionEmote();
			
			if (emo.getName().equalsIgnoreCase(Shmames.getBrains().getBrain(e.getGuild().getId()).getSettingFor(BotSettingName.REMOVAL_EMOTE).getValue())) {
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
			t = Integer.parseInt(Shmames.getBrains().getBrain(e.getGuild().getId()).getSettingFor(BotSettingName.REMOVAL_THRESHOLD).getValue());
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
			t = Integer.parseInt(Shmames.getBrains().getBrain(e.getGuild().getId()).getSettingFor(BotSettingName.REMOVAL_THRESHOLD).getValue());
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
