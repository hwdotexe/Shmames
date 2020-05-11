package tech.hadenw.discordbot.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tech.hadenw.discordbot.CommandHandler;
import tech.hadenw.discordbot.Shmames;
import tech.hadenw.discordbot.TriggerType;
import tech.hadenw.discordbot.Utils;
import tech.hadenw.discordbot.storage.Brain;
import tech.hadenw.discordbot.storage.Family;
import tech.hadenw.discordbot.storage.Response;

public class ChatListener extends ListenerAdapter {
	private CommandHandler cmd;
	
	public ChatListener() {
		cmd = new CommandHandler();
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		if (!e.getAuthor().isBot()) {
			String message = e.getMessage().getContentRaw();

			// Process the message based on the channel type and message text.
			if (e.getChannelType() == ChannelType.TEXT) {
				Brain brain = Shmames.getBrains().getBrain(e.getGuild().getId());

				// Jinping reactions.
				if (brain.getJinping())
					e.getMessage().addReaction("\uD83C\uDFD3").queue();

				if (!brain.getTimeout()) {
					// Check for command triggers.
					for (String trigger : brain.getTriggers(TriggerType.COMMAND)) {
						Matcher m = Pattern.compile("^(" + trigger + ")(.+)?$", Pattern.CASE_INSENSITIVE).matcher(message);

						if (m.matches()) {
							if(m.group(2) != null){
								String command = m.group(2).trim();

								// TODO replace this with real logging
								System.out.println("[COMMAND/" + e.getGuild().getName() + "/" + e.getAuthor().getName() + "]: " + command);

								// Send to the command handler for further processing.
								cmd.PerformCommand(command, e.getMessage(), e.getAuthor(), e.getGuild());
							}else{
								sendRandom(e.getTextChannel(), e.getGuild(), TriggerType.HELLO, e.getAuthor());
							}

							return;
						}
					}

					// Gather emoji stats.
					for (Emote emo : e.getMessage().getEmotes()) {
						if (e.getGuild().getEmotes().contains(emo)) {
							String id = Long.toString(emo.getIdLong());
							Brain b = Shmames.getBrains().getBrain(e.getGuild().getId());

							Utils.IncrementEmoteTally(b, id);
						}
					}

					// Process other triggers.
					for (TriggerType type : TriggerType.values()) {
						for (String trigger : brain.getTriggers(type)) {
							if (message.toLowerCase().contains(trigger)) {
								if (type != TriggerType.COMMAND) {
									if (type != TriggerType.REACT) {
										sendRandom(e.getTextChannel(), e.getGuild(), type, e.getAuthor());
									} else {
										List<Emote> em = new ArrayList<Emote>(e.getGuild().getEmotes());

										for(String s : brain.getFamilies()){
											Family f = Shmames.getBrains().getMotherBrain().getFamilyByID(s);

											if(f != null){
												for(long g : f.getMemberGuilds()){
													if(e.getGuild().getIdLong() == g)
														continue;

													Guild guild = Shmames.getJDA().getGuildById(g);

													if(guild == null)
														continue;

													em.addAll(guild.getEmotes());
												}
											}
										}

										e.getMessage().addReaction(em.get(Utils.getRandom(em.size()))).queue();
									}

									return;
								}
							}
						}
					}

					// Bot gives its two cents.
					if (Utils.getRandom(130) == 0) {
						sendRandom(e.getTextChannel(), e.getGuild(), TriggerType.RANDOM, e.getAuthor());
					}
				}
			} else if (e.getChannelType() == ChannelType.PRIVATE || e.getChannelType() == ChannelType.GROUP) {
				if (message.toLowerCase().startsWith(Shmames.getBotName().toLowerCase())) {
					String command = message.substring(Shmames.getBotName().length()).trim();

					System.out.println("[COMMAND/" + e.getAuthor().getName() + "]: " + command);
					cmd.PerformCommand(command, e.getMessage(), e.getAuthor(), null);
				}
			}
		}
	}

	/**
	 * Chooses a random response from the server's list for a given response trigger.
	 * @param c The channel to reply to.
	 * @param g The server to reply in.
	 * @param t The trigger type being called.
	 * @param author The user who triggered this message.
	 */
	private void sendRandom(TextChannel c, Guild g, TriggerType t, User author) {
		List<Response> r = Shmames.getBrains().getBrain(g.getId()).getResponsesFor(t); 
		String response = r.get(Utils.getRandom(r.size())).getResponse().replaceAll("%NAME%", author.getName());

		if (response.startsWith("[gif]"))
			response = Utils.getGIF(response.split("\\[gif\\]",2)[1], c.isNSFW()?"low":"medium");
		
		c.sendMessage(response).queue();

		return;
	}
}
