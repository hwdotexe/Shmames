package tech.hadenw.shmamesbot;

import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class BirthdaySpam extends TimerTask{
	private int day;
	private int month;
	private String message;
	private String userMention;
	private Calendar cal;
	private TextChannel channel;
	
	public BirthdaySpam(int day, int mth, String message, String userMention, TextChannel channel, JDA jda) {
		this.day = day;
		this.month = mth;
		this.message = message;
		this.channel = channel;
		
		Date date= new Date();
		cal = Calendar.getInstance();
		cal.setTime(date);
		
		for(User u : jda.getUsers()) {
			if(u.getName().equalsIgnoreCase(userMention)) {
				this.userMention = u.getAsMention();
				break;
			}
		}
	}
	
	public void run() {
		Date date= new Date();
		cal = Calendar.getInstance();
		cal.setTime(date);
		
		if(cal.get(Calendar.MONTH) == month) {
			if(cal.get(Calendar.DAY_OF_MONTH) == day) {
				channel.sendMessage("Yo, "+userMention+"! "+message).queue();
			}
		}
	}
}
