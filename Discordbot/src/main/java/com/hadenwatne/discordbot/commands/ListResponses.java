package com.hadenwatne.discordbot.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.discordbot.Errors;
import com.hadenwatne.discordbot.Shmames;
import com.hadenwatne.discordbot.TriggerType;
import com.hadenwatne.discordbot.Utils;
import com.hadenwatne.discordbot.storage.Response;

public class ListResponses implements ICommand {
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
		
				List<Response> rs = Shmames.getBrains().getBrain(message.getGuild().getId())
						.getResponsesFor(TriggerType.byName(args));

				List<String> rsText = new ArrayList<String>();

				for(Response r : rs){
					rsText.add(r.getResponse());
				}

				String list = Utils.GenerateList(rsText, -1, true);

				if(list.length() == 0)
					sb.append("There aren't any responses saved for this trigger type.");
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

				return ":scream: Invalid trigger type! Your options are:" + types;
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
	public String sanitize(String i) {
		return i;
	}
	
	@Override
	public boolean requiresGuild() {
		return true;
	}
}
