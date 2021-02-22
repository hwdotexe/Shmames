package com.hadenwatne.shmames.commands;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hadenwatne.shmames.models.Lang;
import com.hadenwatne.shmames.enums.Langs;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.enums.TriggerType;
import com.hadenwatne.shmames.models.Brain;
import com.hadenwatne.shmames.models.Response;

import javax.annotation.Nullable;

public class DropResponse implements ICommand {
	private Lang lang;
	private Brain brain;

	@Override
	public String getDescription() {
		return "Removes an existing response from the list for the specified type. Use the `listResponses` command " +
				"to view response numbers.";
	}
	
	@Override
	public String getUsage() {
		return "dropresponse <responseType> <responseNumber>";
	}

	@Override
	public String run(String args, User author, Message message) {
		Matcher m = Pattern.compile("^([a-zA-Z]{4,7}) (\\d{1,3})$").matcher(args);
		
		if(m.find()) {
			int rNum = Integer.parseInt(m.group(2));
			String nrtype = m.group(1);

			// Easter egg: "ronald" -> "hate"
			nrtype = nrtype.toLowerCase().equals("ronald") ? "HATE" : nrtype;
	
			if (TriggerType.byName(nrtype) != null) {
				List<Response> responses = brain.getResponsesFor(TriggerType.byName(nrtype));
				
				if(responses.size() >= rNum) {
					Response r = responses.get(rNum-1);
					brain.removeTriggerResponse(r);

					return lang.getMsg(Langs.ITEM_REMOVED, new String[]{ r.getResponse() });
				}else {
					return lang.getError(Errors.NOT_FOUND, true);
				}
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
		return new String[] {"dropresponse", "drop response", "removeresponse", "remove response"};
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
