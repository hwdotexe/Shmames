package tech.hadenw.discordbot.tasks;

import java.util.Timer;
import java.util.TimerTask;

import net.dv8tion.jda.api.entities.MessageChannel;

public class TypingTask extends TimerTask{
	private MessageChannel ch;
	private String message;
	
	public TypingTask(MessageChannel c, String m) {
		ch=c;
		message=m;
		
		c.sendTyping().queue();
		Timer t = new Timer();
		t.schedule(this, 500);
	}
	
	public void run() {
		ch.sendMessage(message).queue();
		this.cancel();
	}
}
