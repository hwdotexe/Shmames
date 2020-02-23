package tech.hadenw.discordbot;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tech.hadenw.discordbot.commands.ICommand;
import tech.hadenw.discordbot.storage.BotSettingName;
import tech.hadenw.discordbot.storage.Brain;

public class ReactListener extends ListenerAdapter {
	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent e) {
		if (e.getUser() != Shmames.getJDA().getSelfUser()) {
			ReactionEmote emo = e.getReaction().getReactionEmote();
			Brain b = Shmames.getBrains().getBrain(e.getGuild().getId());
			
			// Tally up the emote
			if(emo.isEmote()) {
				if(e.getGuild().getEmotes().contains(emo.getEmote())) {
					long id = emo.getIdLong();
					
					if(b.getEmoteStats().containsKey(id)) {
						b.getEmoteStats().put(Long.toString(id), b.getEmoteStats().get(id)+1);
					}else {
						b.getEmoteStats().put(Long.toString(id), 1);
					}
				}
			}
			
			// Removal emotes
			String removalEmote = Shmames.getBrains().getBrain(e.getGuild().getId()).getSettingFor(BotSettingName.REMOVAL_EMOTE).getValue();

			if (emo.getName().equalsIgnoreCase(removalEmote)) {
				badTallyMessage(removalEmote, e.getChannel().retrieveMessageById(e.getMessageIdLong()).complete());
				return;
			}
			
			// Approval emotes
			String approvalEmote = Shmames.getBrains().getBrain(e.getGuild().getId()).getSettingFor(BotSettingName.APPROVAL_EMOTE).getValue();

			if (emo.getName().equalsIgnoreCase(approvalEmote)) {
				goodTallyMessage(approvalEmote, e.getChannel().retrieveMessageById(e.getMessageIdLong()).complete());
				return;
			}
		}
	}

	/**
	 * When a message receives the "badtally" emote, check to see if it hit the threshold. If so,
	 * delete the message and increment the tally.
	 * @param removalEmote The name of the emote used to remove messages.
	 * @param m The Message this reaction occurred on.
	 */
	private void badTallyMessage(String removalEmote, Message m) {
		int threshold = Integer.parseInt(Shmames.getBrains().getBrain(m.getGuild().getId()).getSettingFor(BotSettingName.REMOVAL_THRESHOLD).getValue());
		int votes = 0;

		for(MessageReaction r : m.getReactions()) {
			if(r.getReactionEmote().getName().equalsIgnoreCase(removalEmote)){
				votes = r.getCount();

				break;
			}
		}

		if(votes > threshold){
			String name = m.getAuthor().getName();
			name = name.replaceAll("\\s", "_").replaceAll("[\\W]", "").toLowerCase();
			String toTally = name.equalsIgnoreCase(Shmames.getJDA().getSelfUser().getName()) ? "badbot" : "bad"+name;

			// Remove the message & process
			try {
				m.getChannel().deleteMessageById(m.getIdLong()).queue();

				for(ICommand c : CommandHandler.getLoadedCommands()) {
					for(String a : c.getAliases()) {
						if(a.equalsIgnoreCase("addtally")) {
							String response = c.run(toTally, Shmames.getJDA().getSelfUser(), m);
							m.getChannel().sendMessage(response).queue();
							return;
						}
					}
				}
			}catch(Exception ex) {
				m.getChannel().sendMessage(Errors.NO_PERMISSION_BOT).queue();
			}
		}
	}

	/**
	 * When a message receives the "goodtally" emote, check to see if it hit the threshold. If so,
	 * give the user a good tally.
	 * @param approvalEmote The name of the emote used to remove messages.
	 * @param m The Message this reaction occurred on.
	 */
	private void goodTallyMessage(String approvalEmote, Message m) {
		int threshold = Integer.parseInt(Shmames.getBrains().getBrain(m.getGuild().getId()).getSettingFor(BotSettingName.APPROVAL_THRESHOLD).getValue());
		int votes = 0;

		for(MessageReaction r : m.getReactions()) {
			if(r.getReactionEmote().getName().equalsIgnoreCase(approvalEmote)){
				votes = r.getCount();

				break;
			}
		}

		if(votes > threshold){
			String name = m.getAuthor().getName();
			name = name.replaceAll("\\s", "_").replaceAll("[\\W]", "").toLowerCase();
			String toTally = name.equalsIgnoreCase(Shmames.getJDA().getSelfUser().getName()) ? "goodbot" : "good"+name;

			// Remove the message & process
			try {
				m.getChannel().deleteMessageById(m.getIdLong()).queue();

				for(ICommand c : CommandHandler.getLoadedCommands()) {
					for(String a : c.getAliases()) {
						if(a.equalsIgnoreCase("addtally")) {
							String response = c.run(toTally, Shmames.getJDA().getSelfUser(), m);
							m.getChannel().sendMessage(response).queue();
							return;
						}
					}
				}
			}catch(Exception ex) {
				m.getChannel().sendMessage(Errors.NO_PERMISSION_BOT).queue();
			}
		}
	}
}
