package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.Utils;
import com.hadenwatne.shmames.enums.Langs;
import com.hadenwatne.shmames.models.Brain;
import com.hadenwatne.shmames.models.Lang;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class When implements ICommand {
	private String[] answers;
	private Lang lang;

	public When() {
		answers = new String[] {"In %T years", "In %T minutes", "%T days ago", "When pigs fly", "Absolutely never", "Right now, but in a parallel universe",
				"Not sure, ask your mom", "%T years ago", "Once you stop procrastinating", "Once I get elected Chancellor", "After the heat death of the universe",
				"In precisely %T", "On the next full moon", "When the sand in me hourglass be empty", "Time is subjective", "Time is a tool you can put on the wall",
				"Probably within %T days", "I'd say in %T months", "In %T? %T? Maybe %T?", "Between %T and %T centuries", "Sooner shall %T days pass", "%T seconds", "%T hours, %T minutes, and %T seconds",
				"Eventually", "Not in your lifetime, kiddo", "In your dreams", "Right now"};
	}

	@Override
	public String getDescription() {
		return "I'll tell you when something will happen.";
	}
	
	@Override
	public String getUsage() {
		return "when [item]";
	}

	@Override
	public String run(String args, User author, Message message) {
		String msg = answers[Utils.getRandom(answers.length)];
		Matcher m = Pattern.compile("%T").matcher(msg);

		while(m.find()) {
			msg = msg.replaceFirst(m.group(), Integer.toString(Utils.getRandom(150)+1));
		}

		return msg;
	}

	@Override
	public String[] getAliases() {
		return new String[] {"when"};
	}

	@Override
	public void setRunContext(Lang lang, @Nullable Brain brain) {
		this.lang = lang;
	}
	
	@Override
	public boolean requiresGuild() {
		return false;
	}
}
