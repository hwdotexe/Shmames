package com.hadenwatne.shmames.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hadenwatne.shmames.models.Brain;
import com.hadenwatne.shmames.models.Lang;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.shmames.enums.Errors;

import javax.annotation.Nullable;

public class React implements ICommand {
	private Lang lang;

	@Override
	public String getDescription() {
		return "Reacts to the specified message with emoji that spell out your word. Works " +
				"best with shorter words.";
	}
	
	@Override
	public String getUsage() {
		return "react <word> <^...>";
	}

	@Override
	public String getExamples() {
		return "`react dope ^^^`";
	}

	@Override
	public String run(String args, User author, Message message) {
		Matcher m1 = Pattern.compile("^(\\w{2,16})\\s([\\^]{1,15})$").matcher(args);
		Matcher m2 = Pattern.compile("^([\\^]{1,15})\\s(\\w{2,16})$").matcher(args);

		int messages = 0;
		String word = "";

		if(m1.find()) {
			messages = m1.group(2).length();
			word = m1.group(1);
		} else if(m2.find()) {
			messages = m2.group(1).length();
			word = m2.group(2);
		} else {
			return lang.wrongUsage(getUsage());
		}

		try {
			List<Message> msgs = message.getChannel().getHistoryBefore(message, messages).complete().getRetrievedHistory();
			Message toPin = msgs.get(msgs.size()-1);
			List<Character> chars = new ArrayList<Character>();

			for(char letter : word.toCharArray()) {
				if(chars.contains(letter)) {
					String l = dupLetterEmoji(letter);

					if(l != null)
						toPin.addReaction(l).queue();

					continue;
				}

				toPin.addReaction(letterToEmoji(letter)).queue();
				chars.add(letter);
			}

			// Remove the querying message
			try {
				message.delete().queue();
			}catch(Exception e) {}

			return "";
		}catch(Exception ex) {
			ex.printStackTrace();

			return lang.getError(Errors.NO_PERMISSION_BOT, true);
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"react"};
	}

	@Override
	public void setRunContext(Lang lang, @Nullable Brain brain) {
		this.lang = lang;
	}
	
	@Override
	public boolean requiresGuild() {
		return false;
	}
	
	// Provides duplicate/alternate letters, so we can use the same letter twice.
	private String dupLetterEmoji(char letter) {
		switch(letter) {
		case 'a':
			return "\uD83C\uDD70";
		case 'b':
			return "\uD83C\uDD71";
		case 'e':
			return "\u0033\u20E3";
		case 'i':
			return "\u2139";
		case 'l':
			return "\u0031\u20E3";
		case 'm':
			//return "\u303D";
			return "\u24C2";
		case 'o':
			return "\u0030\u20E3";
		case 'p':
			return "\uD83C\uDD7F";
		case 's':
			return "\u0035\u20E3";
		case 'x':
			return "\u2716";
		case 'z':
			return "\u0032\u20E3";
		default:
			return null;
		}
	}
	
	private String letterToEmoji(char letter) {
		switch(letter) {
		case 'a':
			return "\uD83C\uDDE6";
		case 'b':
			return "\uD83C\uDDE7";
		case 'c':
			return "\uD83C\uDDE8";
		case 'd':
			return "\uD83C\uDDE9";
		case 'e':
			return "\uD83C\uDDEA";
		case 'f':
			return "\uD83C\uDDEB";
		case 'g':
			return "\uD83C\uDDEC";
		case 'h':
			return "\uD83C\uDDED";
		case 'i':
			return "\uD83C\uDDEE";
		case 'j':
			return "\uD83C\uDDEF";
		case 'k':
			return "\uD83C\uDDF0";
		case 'l':
			return "\uD83C\uDDF1";
		case 'm':
			return "\uD83C\uDDF2";
		case 'n':
			return "\uD83C\uDDF3";
		case 'o':
			return "\uD83C\uDDF4";
		case 'p':
			return "\uD83C\uDDF5";
		case 'q':
			return "\uD83C\uDDF6";
		case 'r':
			return "\uD83C\uDDF7";
		case 's':
			return "\uD83C\uDDF8";
		case 't':
			return "\uD83C\uDDF9";
		case 'u':
			return "\uD83C\uDDFA";
		case 'v':
			return "\uD83C\uDDFB";
		case 'w':
			return "\uD83C\uDDFC";
		case 'x':
			return "\uD83C\uDDFD";
		case 'y':
			return "\uD83C\uDDFE";
		case 'z':
			return "\uD83C\uDDFF";
		case '-':
			return "\u2796";
		case '_':
			return "\u2796";
		case '$':
			return "\uD83D\uDCB2";
		default:
			return "\uD83D\uDD95";
		}
	}
}
