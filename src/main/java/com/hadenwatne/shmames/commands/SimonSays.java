package com.hadenwatne.shmames.commands;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hadenwatne.shmames.models.Lang;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.shmames.Shmames;
import com.hadenwatne.shmames.Utils;
import com.hadenwatne.shmames.models.Brain;
import com.hadenwatne.shmames.models.Family;

import javax.annotation.Nullable;

public class SimonSays implements ICommand {
	private Lang lang;
	private @Nullable Brain brain;

	@Override
	public String getDescription() {
		return "I'll repeat after you! Send messages, links, or server emotes!";
	}
	
	@Override
	public String getUsage() {
		return "simonsays <message>";
	}

	@Override
	public String getExamples() {
		return "`simonsays Hey look at me, I'm a talkin' Shmames!`\n" +
				"`simonsays :emoteFromAnotherServer:`";
	}

	@Override
	public String run(String args, User author, Message message) {
		if(args.length() > 0) {
			try {
				message.delete().complete();
			} catch(Exception e) { }

			// :emotename:   =>   <a:emoteName:1234567890>
			Matcher m = Pattern.compile(":([\\w\\d]+):").matcher(args);
			
			// Add a space at the end so we can regex correctly
			args += " ";

			if(brain != null) {
				long thisGuild = message.getGuild().getIdLong();

				// Check servers for the emote; this one first, then others.
				while (m.find()) {
					String eName = m.group(1);

					// This guild
					Emote e = findEmote(message.getGuild(), eName);

					if (e != null) {
						args = args.replace(":" + eName + ": ", e.getAsMention());
					} else {
						boolean found = false;

						for (String fID : brain.getFamilies()) {
							Family f = Shmames.getBrains().getMotherBrain().getFamilyByID(fID);

							if (f != null) {
								for (long gid : f.getMemberGuilds()) {
									if (gid != thisGuild) {
										Guild g = Shmames.getJDA().getGuildById(gid);
										e = findEmote(g, eName);

										if (e != null) {
											args = args.replace(":" + eName + ": ", e.getAsMention());
											found = true;
											break;
										}
									}
								}

								if (found)
									break;
							}
						}
					}

					// Tally the emote
					if (e != null) {
						Brain b = Shmames.getBrains().getBrain(e.getGuild().getId());
						String eID = Long.toString(e.getIdLong());

						Utils.incrementEmoteTally(b, eID);
					}
				}
			}
			
			return args;
		}else {
			return lang.wrongUsage(getUsage());
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"simonsays","simon says", "repeat", "echo"};
	}

	@Override
	public void setRunContext(Lang lang, @Nullable Brain brain) {
		this.lang = lang;
		this.brain = brain;
	}
	
	@Override
	public boolean requiresGuild() {
		return false;
	}
	
	private Emote findEmote(Guild g, String n) {
		List<Emote> em = g.getEmotesByName(n, true);
		
		if(em.size() > 0)
			return em.get(0);
		
		return null;
	}
}
