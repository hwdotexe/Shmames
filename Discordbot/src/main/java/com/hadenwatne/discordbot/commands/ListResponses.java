package com.hadenwatne.discordbot.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hadenwatne.discordbot.storage.Brain;
import com.hadenwatne.discordbot.storage.Lang;
import com.hadenwatne.discordbot.storage.Langs;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.discordbot.Errors;
import com.hadenwatne.discordbot.TriggerType;
import com.hadenwatne.discordbot.Utils;
import com.hadenwatne.discordbot.storage.Response;

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

				String list = Utils.GenerateList(rsText, -1, true);

				if(list.length() == 0)
					sb.append(Errors.ITEMS_NOT_FOUND);
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
			return Errors.formatUsage(Errors.WRONG_USAGE, getUsage());
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
