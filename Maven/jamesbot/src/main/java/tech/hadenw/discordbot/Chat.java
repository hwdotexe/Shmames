package tech.hadenw.discordbot;

import java.util.List;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tech.hadenw.discordbot.brain.Brain;
import tech.hadenw.discordbot.brain.Response;

public class Chat extends ListenerAdapter {
	private CommandHandler cmd;
	
	public Chat() {
		cmd = new CommandHandler();
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		if (!e.getAuthor().isBot()) {
			String message = e.getMessage().getContentRaw();
			
			if(e.getChannelType() == ChannelType.TEXT) {
				Brain brain = Shmames.getBrains().getBrain(e.getGuild().getId());
				
				// Jinping reactions
				if(brain.getJinping())
					e.getMessage().addReaction("\uD83C\uDFD3").queue();
				
				if(!brain.getTimeout()) {
					// Commands
					for (String trigger : brain.getTriggers(TriggerType.COMMAND)) {
						if (message.toLowerCase().startsWith(trigger.toLowerCase())) {
							String command = message.substring(trigger.length());
							
							if(command.contains(" "))
								command = command.substring(command.indexOf(" ")+1).trim();
							
							// TODO replace this with real logging
							System.out.println("[COMMAND/"+e.getAuthor().getName()+"]: "+command);
							cmd.PerformCommand(command, e.getMessage(), e.getAuthor(), e.getGuild());
							
							return;
						}
					}
					
					// Gather emoji stats
					for(Emote emo : e.getMessage().getEmotes()) {
						if(e.getGuild().getEmotes().contains(emo)) {
							String name = emo.getName();
							Brain b = Shmames.getBrains().getBrain(e.getGuild().getId());
							
							if(b.getEmoteStats().containsKey(name)) {
								b.getEmoteStats().put(name, b.getEmoteStats().get(name)+1);
							}else {
								b.getEmoteStats().put(name, 1);
							}
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
									}
									
									return;
								}
							}
						}
					}
		
					// Bot gives its two cents
					if (Utils.getRandom(130) == 0) {
						sendRandom(e.getChannel(), e.getGuild(), TriggerType.RANDOM, e.getAuthor());
					}
				}
			}else {
				if (message.toLowerCase().startsWith(Shmames.getBotName().toLowerCase())) {
					String command = message.substring(Shmames.getBotName().length()).trim();
					
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
		List<Response> r = Shmames.getBrains().getBrain(g.getId()).getResponsesFor(t); 
		String response = r.get(Utils.getRandom(r.size())).getResponse().replaceAll("%NAME%", author.getName());

		if (response.startsWith("[gif]"))
			response = Utils.getGIF(response.split("\\[gif\\]",2)[1]);
		
		c.sendMessage(response).queue();

		return;
	}
}
