package com.hadenwatne.shmames.tasks;

import java.util.Timer;
import java.util.TimerTask;

import com.hadenwatne.shmames.services.PaginationService;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class TypingTask extends TimerTask{
	private String response;
	private Message trigger;
	private MessageChannel channel;
	private boolean doReply;
	
	public TypingTask(String response, Message trigger, boolean doReply) {
		this.response = response;
		this.trigger = trigger;
		this.doReply = doReply;

		if(response != null && response.length() > 0) {
			trigger.getChannel().sendTyping().queue();

			Timer t = new Timer();
			t.schedule(this, 500);
		}
	}

	public TypingTask(String response, MessageChannel channel, boolean doReply) {
		this.response = response;
		this.channel = channel;
		this.doReply = doReply;

		if(response != null && response.length() > 0) {
			channel.sendTyping().queue();

			Timer t = new Timer();
			t.schedule(this, 500);
		}
	}

	public void run() {
		for(String m : PaginationService.SplitString(response, 2000)){
			if(m.length() > 0) {
				dispatchMessage(m);
			}
		}

		this.cancel();
	}

	private void dispatchMessage(String message) {
		if(doReply) {
			trigger.reply(message).queue(success -> {}, error -> trigger.getChannel().sendMessage(message).queue());
		} else {
			channel.sendMessage(message).queue();
		}
	}
}
