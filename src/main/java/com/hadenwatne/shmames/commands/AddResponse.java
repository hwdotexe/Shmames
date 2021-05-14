package com.hadenwatne.shmames.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hadenwatne.shmames.models.Lang;
import com.hadenwatne.shmames.enums.Langs;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.shmames.enums.TriggerType;
import com.hadenwatne.shmames.models.Brain;
import com.hadenwatne.shmames.models.Response;

import javax.annotation.Nullable;

public class AddResponse implements ICommand {
	private Lang lang;
	private Brain brain;

	@Override
	public String getDescription() {
		return "Adds a new response to the list for the chosen response type. These are " +
				"chosen at random.";
	}
	
	@Override
	public String getUsage() {
		return "addresponse <responseType> <responseText>";
	}

	@Override
	public String getExamples() {
		return "`addresponse RANDOM Your mother was a hamster!`";
	}

	@Override
	public String run(String args, User author, Message message) {
		Matcher m = Pattern.compile("^([a-zA-Z]{4,7}) ([\\w\\W]{3,})$").matcher(args);
		
		if(m.find()) {
			String newresp = m.group(2);
			String nrtype = m.group(1);

			// Easter egg: "ronald" -> "hate"
			nrtype = nrtype.toLowerCase().equals("ronald") ? "HATE" : nrtype;
	
			if (TriggerType.byName(nrtype) != null) {
				brain.getTriggerResponses().add(new Response(TriggerType.byName(nrtype), newresp));

				return lang.getMsg(Langs.ITEM_ADDED);
			} else {
				StringBuilder types = new StringBuilder();

				for (TriggerType t : TriggerType.values()) {
					if(types.length() > 0)
						types.append(", ");
					
					types.append("`").append(t.name()).append("`");
				}

				return lang.getMsg(Langs.INVALID_TRIGGER_TYPE, new String[] { types.toString() });
			}
		} else {
			return lang.wrongUsage(getUsage());
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"addresponse", "add response"};
	}

	@Override
	public void setRunContext(Lang lang, @Nullable Brain brain) {
		this.lang = lang;
		this.brain = brain;
	}
	
	@Override
	public boolean requiresGuild() {
		return true;
	}
}
