package com.hadenwatne.shmames.tasks;

import java.awt.Color;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimerTask;

import com.hadenwatne.shmames.models.Brain;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import com.hadenwatne.shmames.models.Poll;
import com.hadenwatne.shmames.Shmames;
import com.hadenwatne.shmames.Utils;

public class PollTask extends TimerTask{
	private Poll p;
	private Message m;
	
	public PollTask(Poll poll, Message msg) {
		p=poll;
		m=msg;
	}
	
	public void run() {
		if(p.isActive()) {
			p.setActive(false);
			
			EmbedBuilder eBuilder = new EmbedBuilder();
			Calendar c = Calendar.getInstance();
	    	c.setTime(new Date());
			
	    	m = m.getChannel().retrieveMessageById(m.getId()).complete();
	    	HashMap<Integer, Integer> votes = new HashMap<Integer, Integer>();
	    	
	    	for(MessageReaction r : m.getReactions()) {
	    		votes.put(this.emojiToInt(r.getReactionEmote().getName()), r.getCount()-1);
	    	}
	    	
			eBuilder.setAuthor(p.getTitleResults());
	        eBuilder.setColor(Color.GRAY);
	        eBuilder.setTitle(p.getQuestion());
	        eBuilder.setFooter("#" + m.getChannel().getName() + " - Expired "+Utils.getFriendlyDate(c), null);
	        
	        for(int i=0; i<p.getOptions().size(); i++) {
	        	eBuilder.appendDescription("**"+(i+1)+"**: "+p.getOptions().get(i)+" **("+votes.get(i+1)+" votes)**"+"\n");
	        }
	
	        MessageEmbed embed = eBuilder.build();
	        MessageAction ma = m.getChannel().sendMessage(embed);
	        
	        ma.complete();
			
			m.delete().queue();
			
			Brain b = Shmames.getBrains().getBrain(m.getGuild().getId());
			b.getActivePolls().remove(p);
			Shmames.getBrains().saveBrain(b);
		}
		
		this.cancel();
	}
	
	private int emojiToInt(String i) {
		switch(i) {
		case "\u0031\u20E3":
			return 1;
		case "\u0032\u20E3":
			return 2;
		case "\u0033\u20E3":
			return 3;
		case "\u0034\u20E3":
			return 4;
		case "\u0035\u20E3":
			return 5;
		case "\u0036\u20E3":
			return 6;
		case "\u0037\u20E3":
			return 7;
		case "\u0038\u20E3":
			return 8;
		case "\u0039\u20E3":
			return 9;
		default:
			return 0;
		}
	}
}
