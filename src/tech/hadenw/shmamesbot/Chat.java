package tech.hadenw.shmamesbot;

import java.util.List;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import tech.hadenw.shmamesbot.brain.Brain;

public class Chat extends ListenerAdapter {
	private CommandHandler cmd;
	
	public Chat() {
		cmd = new CommandHandler();
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		if (!e.getAuthor().isBot()) {
			
			String message = e.getMessage().getContentDisplay();
			
			if(e.getChannelType() == ChannelType.TEXT) {
				Brain brain = Shmames.getBrains().getBrain(e.getGuild().getId());
				
				// Commands
				for (String trigger : brain.getTriggers(TriggerType.COMMAND)) {
					if (message.toLowerCase().startsWith(trigger.toLowerCase())) {
						String command = message.substring(trigger.length()).trim();
						
						System.out.println("[COMMAND/"+e.getAuthor().getName()+"]: "+command);
						cmd.PerformCommand(command, e.getMessage(), e.getAuthor(), e.getGuild());
						
						return;
					}
				}
				
				// Triggers
				for (TriggerType type : TriggerType.values()) {
					for (String trigger : brain.getTriggers(type)) {
						if (sanitize(message).contains(trigger)) {
							if (type != TriggerType.COMMAND) {
								if (type != TriggerType.REACT) {
									sendRandom(e.getChannel(), e.getGuild(), type, e.getAuthor());
								} else {
									List<Emote> em = Shmames.getJDA().getEmotes();
									e.getMessage().addReaction(em.get(Utils.getRandom(em.size()))).queue();
									return;
								}
							}
						}
					}
				}
				
				//Nicolas Cage memes
				if (sanitize(message).contains("nicolas cage")) {
					e.getChannel().sendMessage(Utils.getGIF("nicolas cage")).queue();
					return;
				}
	
				// Bot gives its two cents
				if (Utils.getRandom(100) < 1) {
					sendRandom(e.getChannel(), e.getGuild(), TriggerType.RANDOM, e.getAuthor());
				}
			}else {
				if (message.toLowerCase().startsWith(Shmames.getJDA().getSelfUser().getName().toLowerCase())) {
					String command = message.substring(Shmames.getJDA().getSelfUser().getName().length()).trim();
					
					System.out.println("[COMMAND/"+e.getAuthor().getName()+"]: "+command);
					cmd.PerformCommand(command, e.getMessage(), e.getAuthor(), null);
					
					return;
				}
			}
		}
	}
	
	private String sanitize(String i) {
		return i.replaceAll("[^\\s\\w]", "").toLowerCase();
	}
	
	private void sendRandom(MessageChannel c, Guild g, TriggerType t, User author) {
		List<String> r = Shmames.getBrains().getBrain(g.getId()).getResponsesFor(t); 
		String response = r.get(Utils.getRandom(r.size()));

		if (response.startsWith("[gif]"))
			c.sendMessage(Utils.getGIF(response.split("\\[gif\\]",2)[1])).queue();
		else
			c.sendMessage(response.replaceAll("%NAME%", author.getAsMention())).queue();

		return;
	}
}
