package com.hadenwatne.shmames.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hadenwatne.shmames.models.Brain;
import com.hadenwatne.shmames.models.Lang;
import com.hadenwatne.shmames.enums.Langs;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.enums.TriggerType;
import com.hadenwatne.shmames.Utils;
import com.hadenwatne.shmames.models.Response;

import javax.annotation.Nullable;

public class ListResponses implements ICommand {
	private Lang lang;
	private Brain brain;

	@Override
	public String getDescription() {
		return "Displays the list of random responses for the specified trigger type.";
	}
	
	@Override
	public String getUsage() {
		return "listResponses <responseType>";
	}

	@Override
	public String getExamples() {
		return "`listresponses RANDOM`";
	}

	@Override
	public String run(String args, User author, Message message) {
		Matcher m = Pattern.compile("^([a-zA-Z]{4,7})$").matcher(args);
		
		if(m.find()) {
			if(TriggerType.byName(m.group(1)) != null) {
				StringBuilder sb = new StringBuilder();

				sb.append("**");
				sb.append(args.toUpperCase());
				sb.append(" Responses:**\n");
		
				List<Response> rs = brain.getResponsesFor(TriggerType.byName(args));

				List<String> rsText = new ArrayList<String>();

				for(Response r : rs){
					rsText.add(r.getResponse());
				}

				String list = Utils.generateList(rsText, -1, true, true);

				if(list.length() == 0)
					sb.append(lang.getError(Errors.ITEMS_NOT_FOUND, true));
				else
					sb.append(list);
				
				return sb.toString();
			} else {
				StringBuilder types = new StringBuilder();

				for (TriggerType t : TriggerType.values()) {
					if(types.length() > 0)
						types.append(", ");
					
					types.append("`");
					types.append(t.name());
					types.append("`");
				}

				return lang.getMsg(Langs.INVALID_TRIGGER_TYPE, new String[] { types.toString() });
			}
		} else {
			return lang.wrongUsage(getUsage());
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"listresponses", "list responses"};
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
