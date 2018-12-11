package tech.hadenw.shmamesbot;

import java.util.List;
import java.util.regex.Pattern;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Game.GameType;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Chat extends ListenerAdapter {
	private Shmames james;
	private boolean doNuke;
	
	public Chat(Shmames c) {
		doNuke = false;
		james = c;
	}

	private String sanitize(String i) {
		return i.replaceAll("[^\\s\\w]", "").toLowerCase();
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		if (!e.getAuthor().isBot()) {
			String author = "";

			if (e.getChannelType() == ChannelType.PRIVATE) {
				author = e.getAuthor().getName();
			} else if (e.getChannelType() == ChannelType.TEXT) {
				author = e.getMember().getNickname();
			}

			String message = e.getMessage().getContentDisplay();

			/* Commands */
			for (String trigger : james.getBrain().getTriggers(TriggerType.COMMAND)) {
				if (message.toLowerCase().startsWith(trigger.toLowerCase())) {
					String command = message.substring(trigger.length()).trim();

					// TODO remove eventually
					System.out.println("[COMMAND]: " + command);

					if (command.equalsIgnoreCase("help")) {
						e.getChannel().sendMessage("PM'd you the deets :punch:").queue();

						e.getAuthor().openPrivateChannel()
								.queue((c) -> c.sendMessage("**How to use Shmames in 42 easy steps:**"
										+ "\n`reload` - Reload my brain" + "\n`setgame <game>` - Set my game status"
										+ "\n`addgame <game>` - Add a game status to the random pool"
										+ "\n`add a tally to <tally>` - Increment a tally"
										+ "\n`remove a tally from <tally>` - Decrement a tally"
										+ "\n`show all tallies` - View a list of current tallies"
										+ "\n`gif <gif>` - Send a super :sunglasses: GIF"
										+ "\n`addtrigger <trigger> : <type>` - Add a message trigger"
										+ "\n`removetrigger <trigger>` - Delete a message trigger"
										+ "\n`addresponse <response> : <type>` - Add a message response"
										+ "\n`listtriggers` - List all triggers and types"
										+ "\n`listresponses <type>` - List all triggers and types"
										+ "\n`roll a <d20|d8|d4>` - Just roll some dice" + "\n\n"
										+ "\nProtip: `%NAME%` becomes a username"
										+ "\n`[gif]<search>` will send a gif instead of text").queue());
					} else if (command.toLowerCase().startsWith("reload")) {
						james.loadBrain();
						doNuke = false;
						e.getChannel().sendMessage("[Your File] => 10010101 => [My Brain]").queue();
					} else if (command.toLowerCase().startsWith("setgame")) {
						String game = command.substring("setgame".length()).trim();

						if (game.length() > 0) {
							james.getJDA().getPresence().setGame(Game.of(GameType.DEFAULT, game));
						} else {
							e.getChannel().sendMessage("Usage: `setgame <game title>`").queue();
						}
					} else if (command.toLowerCase().startsWith("addgame")) {
						String game = command.substring("setgame".length()).trim();

						if (game.length() > 0) {
							james.getJDA().getPresence().setGame(Game.of(GameType.DEFAULT, game));
							james.getBrain().getGames().add(game);
						} else {
							e.getChannel().sendMessage("Usage: `addgame <game title>`").queue();
						}
					} else if (command.toLowerCase().startsWith("add a tally to")) {
						String toTally = sanitize(command.toLowerCase().split("add a tally to")[1].trim()).replaceAll(" ", "_");

						if (james.getBrain().getTallies().containsKey(toTally)) {
							james.getBrain().getTallies().put(toTally, james.getBrain().getTallies().get(toTally) + 1);
						} else {
							james.getBrain().getTallies().put(toTally, 1);
						}

						e.getChannel().sendMessage("Current tally for `" + toTally + "`: `"
								+ james.getBrain().getTallies().get(toTally) + "`").queue();
						james.saveBrain();
					} else if (command.toLowerCase().startsWith("remove a tally from")) {
						String toTally = sanitize(command.toLowerCase().split("remove a tally from")[1].trim()).replaceAll(" ", "_");

						if (james.getBrain().getTallies().containsKey(toTally)) {
							int tallies = james.getBrain().getTallies().get(toTally);

							if (tallies - 1 < 1) {
								james.getBrain().getTallies().remove(toTally);
								e.getChannel().sendMessage("`" + toTally + "` hast been removed, sire").queue();
							} else {
								james.getBrain().getTallies().put(toTally, tallies - 1);
								e.getChannel().sendMessage("Current tallies for `" + toTally + "`: `"
										+ james.getBrain().getTallies().get(toTally) + "`").queue();
							}

							james.saveBrain();
						} else {
							e.getChannel().sendMessage("**I'm sorry " + author + ", I'm afraid I can't do that.**")
									.queue();
						}
					} else if (command.toLowerCase().equals("show all tallies") || command.toLowerCase().equals("show all the tallies")
							|| command.toLowerCase().equals("all tallies") || command.toLowerCase().equals("tallies") || command.toLowerCase().equals("all the tallier")) {
						String tallies = "The abacus hast recorded thusly:\n\n";

						for (String key : james.getBrain().getTallies().keySet()) {
							tallies += "`" + key + "`: `" + james.getBrain().getTallies().get(key) + "`\n";
						}

						e.getChannel().sendMessage(tallies).queue();
					} else if (command.toLowerCase().startsWith("gif")) {
						String gif = sanitize(command.toLowerCase().split("gif", 2)[1].trim());

						if (gif.length() > 0)
							e.getChannel().sendMessage(james.getGifURL(gif)).queue();
						else
							e.getChannel().sendMessage("Dost thou even syntax, brethren?").queue();
					} else if (command.toLowerCase().startsWith("addtrigger")) {
						String newtrigger = "";
						String nttype = "";

						try {
							newtrigger = command.toLowerCase().split(":", 2)[0].trim().split("addtrigger", 2)[1].trim();
							nttype = command.toLowerCase().split(":", 2)[1].trim();
						} catch (Exception exc) {
							e.getChannel().sendMessage(
									":angry: Wow, way to not pay attention to syntax. See the `help` menu for assistance ('cause you need it)")
									.queue();
							return;
						}

						if (!james.getBrain().getAllTriggers().contains(newtrigger)) {
							if (TriggerType.byName(nttype) != null) {
								james.getBrain().addTrigger(newtrigger, TriggerType.byName(nttype));
								james.saveBrain();
								e.getChannel().sendMessage("I will now send a `" + TriggerType.byName(nttype).toString()
										+ "` response when I hear `" + newtrigger + "`!").queue();
							} else {
								String types = "";

								for (TriggerType t : TriggerType.values()) {
									types += " ";
									types += "`" + t.name() + "`";
								}

								e.getChannel().sendMessage(":scream: Invalid trigger type! Your options are:" + types)
										.queue();
							}
						} else
							e.getChannel().sendMessage("Good idea, but that trigger already exists :sob:").queue();
					} else if (command.toLowerCase().startsWith("removetrigger")) {
						String oldtrigger = sanitize(command.toLowerCase().substring("removetrigger".length()).trim());

						if (!oldtrigger.equalsIgnoreCase("hey james")) {
							if (james.getBrain().getTriggers().containsKey(oldtrigger)) {
								james.getBrain().getTriggers().remove(oldtrigger);
								james.saveBrain();

								e.getChannel().sendMessage("I threw it on the **GROUND**!").queue();
							} else
								e.getChannel().sendMessage(
										"While I'd love to do that, it doesn't exist in my brain, so, I can't...")
										.queue();
						} else {
							e.getChannel().sendMessage(
									"Sorry, ya can't delete that trigger. It's stuck here like the gum on my shoe #ThanksCarly")
									.queue();
						}
					} else if (command.toLowerCase().startsWith("listtriggers")) {
						String msg = "";

						for (String trig : james.getBrain().getTriggers().keySet()) {
							msg += trig + " : " + james.getBrain().getTriggers().get(trig) + "\n";
						}

						e.getChannel().sendMessage(msg).queue();
					} else if (command.toLowerCase().startsWith("listresponses")) {
						String type = command.toLowerCase().split("listresponses", 2)[1].trim();
						String msg = "";

						for (String s : james.getBrain().getAllResponsesFor(TriggerType.byName(type))) {
							msg += "•" + s + "\n";
						}

						e.getChannel().sendMessage(msg).queue();
					} else if (command.toLowerCase().startsWith("addresponse")) {
						String newresp = "";
						String nrtype = "";

						try {
							newresp = message.split(":", 2)[0].trim().split("addresponse", 2)[1].trim();
							nrtype = command.toLowerCase().split(":", 2)[1].trim();
						} catch (Exception exc) {
							e.getChannel().sendMessage(
									":angry: Wow, way to not pay attention to syntax. See the `help` menu for assistance ('cause you need it)")
									.queue();
							return;
						}

						if (newresp.length() == 0 || nrtype.length() == 0) {
							e.getChannel().sendMessage(
									":angry: Wow, way to not pay attention to syntax. See the `help` menu for assistance ('cause you need it)")
									.queue();
							return;
						}

						if (!james.getBrain().getResponses().containsKey(newresp)) {
							if (TriggerType.byName(nrtype) != null) {
								james.getBrain().addResponse(newresp, TriggerType.byName(nrtype));
								james.saveBrain();

								e.getChannel().sendMessage("Added :+1:").queue();
							} else {
								String types = "";

								for (TriggerType t : TriggerType.values()) {
									types += " ";
									types += "`" + t.name() + "`";
								}

								e.getChannel().sendMessage(":scream: Invalid trigger type! Your options are:" + types)
										.queue();
							}
						} else
							e.getChannel().sendMessage("Good idea, but that response already exists :sob:").queue();
					} else if (command.toLowerCase().startsWith("roll a")) {
						String input = command.toLowerCase().split("roll a", 2)[1].replaceAll(" ", "");
						
						if(Pattern.compile("^d\\d{1,2}([\\+\\-]\\d{1})?$").matcher(input).matches()) {
							//matches "d20" and "d2+3"
							
							try {
								input = input.replaceAll("\\+", "p").replaceAll("\\-", "m");
								
								int base = 0;
								int mod = 0;
								boolean subtract = false;
								
								if(input.length()>3) {
									// Has a modifier
									int mindex = input.contains("p") ? input.indexOf("p") : input.indexOf("m");
									
									base = Integer.parseInt(input.substring(1, mindex));
									mod = Integer.parseInt(input.substring(mindex+1));
									subtract = input.charAt(mindex) == 'm' ? true : false;
								} else {
									// No modifier
									base = Integer.parseInt(input.substring(1));
								}
								
								int roll = james.getRandom().nextInt(base) + 1;
								int baseroll = roll;
								
								if(subtract) {
									roll -= mod;
								}else {
									roll += mod;
								}
								
								e.getChannel().sendMessage(":game_die: " + e.getAuthor().getAsMention() + " d"+base+"("+baseroll+")"+ (mod != 0 ? subtract == true ? "-"+mod : "+"+mod : "") +": " + (roll)).queue();
								
								// If rolling a d20, send a GIF if the player rolls an extreme
								if(base == 20) {
									if(baseroll <= 3) {
										e.getChannel().sendMessage(james.getGifURL("laugh"));
									} else if(baseroll >= 19) {
										e.getChannel().sendMessage(james.getGifURL("hype"));
									}
								}
								
							}catch(Exception ex) {
								e.getChannel().sendMessage("I sense a plot to destroy me.").queue();
								ex.printStackTrace();
							}
						}else{
							e.getChannel().sendMessage("Try `roll a d20`!").queue();
						}
					} else if (command.equals("InitializeArmageddon();")) {
						e.getChannel().sendMessage("Please confirm your actions.").queue();
						doNuke = true;
					} else if (command.equals("CONFIRM")) {
						if (doNuke) {
							for (TextChannel c : e.getGuild().getTextChannels()) {
								c.delete().queue();
							}

							for (VoiceChannel c : e.getGuild().getVoiceChannels()) {
								c.delete().queue();
							}

							doNuke = false;
							james.getJDA().getPresence().setGame(Game.of(GameType.WATCHING, "the world burn"));
						} else {
							// Fake error message to deter anyone from finding our secret nuke.
							e.getChannel().sendMessage("Hey bro! Try out `" + trigger + " help`").queue();
						}
					} else {
						e.getChannel().sendMessage("Hey bro! Try out `" + trigger + " help`").queue();
					}

					return;
				}
			}

			/* Send a message based on a trigger */
			for (TriggerType type : TriggerType.values()) {
				for (String trigger : james.getBrain().getTriggers(type)) {
					if (sanitize(message).contains(trigger)) {
						if (type != TriggerType.COMMAND) {
							if (type != TriggerType.REACT) {
								String response = getRandom(james.getBrain().getAllResponsesFor(type));

								if (response.startsWith("[gif]"))
									e.getChannel().sendMessage(james.getGifURL(response.substring(5).trim())).queue();
								else
									e.getChannel().sendMessage(response.replaceAll("%NAME%", author)).queue();

								return;
							} else {
								List<Emote> em = james.getJDA().getEmotes();
								e.getMessage().addReaction(em.get(james.getRandom().nextInt(em.size()))).queue();
								return;
							}
						}
					}
				}
			}

			/* Nicolas Cage memes */
			if (sanitize(message).contains("nick cage") || sanitize(message).contains("nicolas cage")) {
				e.getChannel().sendMessage(james.getGifURL("nicolas cage")).queue();
				return;
			}

			/* James needs to give his two cents */
			if (james.getRandom().nextInt(100) < 1) {
				String rmsg = getRandom(james.getBrain().getAllResponsesFor(TriggerType.RANDOM));

				if (rmsg.startsWith("[gif]"))
					e.getChannel().sendMessage(james.getGifURL(rmsg.substring(5).trim())).queue();
				else
					e.getChannel().sendMessage(rmsg.replaceAll("%NAME%", author)).queue();

				return;
			}
		}
	}

	private String getRandom(List<String> s) {
		if (s.size() > 0)
			return s.get(james.getRandom().nextInt(s.size()));
		else
			return "wat is this";
	}
}
