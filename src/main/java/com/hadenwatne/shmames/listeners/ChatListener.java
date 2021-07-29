package com.hadenwatne.shmames.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hadenwatne.shmames.*;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.enums.TriggerType;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.Family;
import com.hadenwatne.shmames.models.data.Lang;
import com.hadenwatne.shmames.models.Response;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ChatListener extends ListenerAdapter {
	private final Lang defaultLang = Shmames.getDefaultLang();
	
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

								// Send to the command handler for further processing.
								Shmames.getCommandHandler().PerformCommand(command, e.getMessage(), e.getAuthor(), e.getGuild());
							}else{
								e.getTextChannel().sendMessage(defaultLang.getError(Errors.HEY_THERE, false, new String[] { Shmames.getBotName() })).queue();
							}

							return;
						}
					}

					// Gather emoji stats.
					for (Emote emo : e.getMessage().getEmotes()) {
						if (e.getGuild().getEmotes().contains(emo)) {
							String id = Long.toString(emo.getIdLong());
							Brain b = Shmames.getBrains().getBrain(e.getGuild().getId());

							Utils.incrementEmoteTally(b, id);
						}
					}

					// Process other triggers.
					for (TriggerType type : TriggerType.values()) {
						for (String trigger : brain.getTriggers(type)) {
							Matcher m = Pattern.compile("\\b"+trigger+"\\b", Pattern.CASE_INSENSITIVE).matcher(message);

							if (m.find()) {
								if (type != TriggerType.COMMAND) {
									if (type != TriggerType.REACT) {
										sendRandom(e.getTextChannel(), type, e.getMessage());
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
					if (Utils.getRandom(150) == 0) {
						sendRandom(e.getTextChannel(), TriggerType.RANDOM, e.getMessage());
					}
				}
			} else if (e.getChannelType() == ChannelType.PRIVATE || e.getChannelType() == ChannelType.GROUP) {
				if (message.toLowerCase().startsWith(Shmames.getBotName().toLowerCase())) {
					String command = message.substring(Shmames.getBotName().length()).trim();

					Shmames.getCommandHandler().PerformCommand(command, e.getMessage(), e.getAuthor(), null);
				}
			}
		}
	}

	/**
	 * Chooses a random response from the server's list for a given response trigger.
	 * @param c The channel to reply to.
	 * @param t The trigger type being called.
	 * @param message The message that triggered this event.
	 */
	private void sendRandom(TextChannel c, TriggerType t, Message message) {
		Guild g = message.getGuild();
		Member author = message.getMember();
		List<Response> r = Shmames.getBrains().getBrain(g.getId()).getResponsesFor(t);
		String name = author.getNickname() != null ? author.getNickname() : author.getEffectiveName();

		if(r.size() > 0) {
			String response = r.get(Utils.getRandom(r.size())).getResponse().replaceAll("%NAME%", name);

			if (response.startsWith("[gif]"))
				response = Utils.getGIF(response.split("\\[gif\\]", 2)[1], c.isNSFW() ? "low" : "high");

			if(t == TriggerType.LOVE || t == TriggerType.HATE) {
				message.reply(response).queue();
			} else {
				c.sendMessage(response).queue();
			}
		}else{
			if(t != TriggerType.RANDOM)
				c.sendMessage("There are no responses saved for the "+t.name()+" type!").queue();
		}
	}
}
