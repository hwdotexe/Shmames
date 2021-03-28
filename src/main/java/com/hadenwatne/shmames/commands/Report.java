package com.hadenwatne.shmames.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hadenwatne.shmames.models.Lang;
import com.hadenwatne.shmames.enums.Langs;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.shmames.Shmames;
import com.hadenwatne.shmames.models.Brain;
import com.hadenwatne.shmames.tasks.ReportCooldownTask;

import javax.annotation.Nullable;

public class Report implements ICommand {
	private Lang lang;
	private Brain brain;

	@Override
	public String getDescription() {
		return "Send feedback about "+Shmames.getBotName()+" to the developer. Your username, server's name, and message will be recorded.";
	}
	
	@Override
	public String getUsage() {
		return "report [bug|feature] <your message>";
	}

	@Override
	public String run(String args, User author, Message message) {
		Matcher m = Pattern.compile("^((bug)|(feature))?\\s?(.{10,})$", Pattern.CASE_INSENSITIVE).matcher(args);
		
		if(m.find()) {
			if(!brain.getReportCooldown()) {
				String type = m.group(1); // Could be empty!
				String msg = m.group(4);
				
				if(type == null)
					type = "GENERIC";

				brain.getFeedback().add(author.getName()+" ("+message.getGuild().getName()+"): ["+type.toUpperCase()+"] "+msg);
				
				// Start a cooldown
				new ReportCooldownTask(brain);

				return lang.getMsg(Langs.FEEDBACK_SENT);
			}else {
				return lang.getMsg(Langs.FEEDBACK_COOLDOWN);
			}
		}else {
			return lang.wrongUsage(getUsage());
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"report", "feedback", "suggestion"};
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