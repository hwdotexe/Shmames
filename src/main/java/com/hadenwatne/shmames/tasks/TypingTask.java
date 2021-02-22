package com.hadenwatne.shmames.tasks;

import java.util.Timer;
import java.util.TimerTask;

import com.hadenwatne.shmames.Utils;
import com.hadenwatne.shmames.enums.Errors;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class TypingTask extends TimerTask{
	private String response;
	private Message trigger;
	
	public TypingTask(String response, Message trigger) {
		this.response = response;
		this.trigger = trigger;

		if(response != null && response.length() > 0) {
			trigger.getChannel().sendTyping().queue();

			Timer t = new Timer();
			t.schedule(this, 500);
		}
	}
	
	public void run() {
		for(String m : Utils.splitString(response, 2000)){
			if(m.length() > 0) {
				dispatchMessage(m);
			}
		}

		this.cancel();
	}

	private void dispatchMessage(String message) {
		trigger.reply(message).queue(success -> {}, error -> trigger.getChannel().sendMessage(message).queue());
	}
}
