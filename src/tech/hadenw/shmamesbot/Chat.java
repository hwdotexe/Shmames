package tech.hadenw.shmamesbot;

import java.util.List;

import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import tech.hadenw.shmamesbot.commands.CommandHandler;

public class Chat extends ListenerAdapter {
	private CommandHandler cmd;
	
	public Chat() {
		cmd = new CommandHandler();
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		if (!e.getAuthor().isBot()) {

			String message = e.getMessage().getContentDisplay();
			
			// Commands
			for (String trigger : Shmames.getBrain().getTriggers(TriggerType.COMMAND)) {
				if (message.toLowerCase().startsWith(trigger.toLowerCase())) {
					String command = message.substring(trigger.length()).trim();
					
					System.out.println("[COMMAND/"+e.getAuthor().getName()+"]: "+command);
					cmd.PerformCommand(command, e.getChannel(), e.getAuthor(), e.getGuild());
					
					return;
				}
			}
			
			// Triggers
			for (TriggerType type : TriggerType.values()) {
				for (String trigger : Shmames.getBrain().getTriggers(type)) {
					if (sanitize(message).contains(trigger)) {
						if (type != TriggerType.COMMAND) {
							if (type != TriggerType.REACT) {
								sendRandom(e.getChannel(), type, e.getAuthor());
							} else {
								List<Emote> em = Shmames.getJDA().getEmotes();
								e.getMessage().addReaction(em.get(Shmames.getRandom(em.size()))).queue();
								return;
							}
						}
					}
				}
			}
			
			//Nicolas Cage memes
			if (sanitize(message).contains("nicolas cage")) {
				e.getChannel().sendMessage(Shmames.getGIF("nicolas cage")).queue();
				return;
			}

			// James needs to give his two cents
			if (Shmames.getRandom(100) < 1) {
				sendRandom(e.getChannel(), TriggerType.RANDOM, e.getAuthor());
			}
		}
	}
	
	private String sanitize(String i) {
		return i.replaceAll("[^\\s\\w]", "").toLowerCase();
	}
	
	private void sendRandom(MessageChannel c, TriggerType t, User author) {
		List<String> r = Shmames.getBrain().getAllResponsesFor(t); 
		String response = r.get(Shmames.getRandom(r.size()));

		if (response.startsWith("[gif]"))
			c.sendMessage(Shmames.getGIF(response.split("\\[gif\\]",2)[1])).queue();
		else
			c.sendMessage(response.replaceAll("%NAME%", author.getAsMention())).queue();

		return;
	}
}
