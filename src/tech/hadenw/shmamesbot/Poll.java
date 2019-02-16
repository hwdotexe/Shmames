package tech.hadenw.shmamesbot;

import java.awt.Color;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.requests.restaction.MessageAction;

public class Poll {
	private Message m;
	private String question;
	private List<String> options;
	private HashMap<Integer, Integer> votes;
	private int pollID;
	
	public Poll(MessageChannel ch, String q, List<String> o, int minutes) {
		question=q;
		options=o;
		pollID = Shmames.getPollID();
		votes = new HashMap<Integer, Integer>();
		
		Calendar c = Calendar.getInstance();
    	c.setTime(new Date());
    	c.add(Calendar.MINUTE, minutes);
    	//c.add(Calendar.SECOND, 15);
		
		EmbedBuilder eBuilder = new EmbedBuilder();
		User a = Shmames.getJDA().getSelfUser();

        eBuilder.setAuthor(a.getName(), null, a.getEffectiveAvatarUrl());
        eBuilder.setColor(Color.GREEN);
        eBuilder.setTitle(question);
        eBuilder.setFooter("#" + ch.getName() + " - Expires "+Utils.getFriendlyDate(c), null);
        
        for(int i=0; i<options.size(); i++) {
        	eBuilder.appendDescription("**"+(i+1)+"**: "+options.get(i)+"\n");
        	votes.put(i, 0);
        }

        MessageEmbed embed = eBuilder.build();
        MessageAction ma = ch.sendMessage(embed);
        
        m = ma.complete();
        
        // Add reaction emotes
        for(int i=0; i<options.size(); i++) {
        	m.addReaction(intToEmoji(i+1)).queue();
        }
        
        // Schedule the task
        Timer t = new Timer();
		t.schedule(new PollFinal(pollID), c.getTime());
		
		// Pin the message
		try {
			m.pin().queue();
		}catch(Exception e) {
			// Do nothing; we don't have permission
		}
	}
	
	public HashMap<Integer, Integer> getVotes(){
		return votes;
	}
	
	public String getQuestion() {
		return question;
	}
	
	public List<String> getOptions(){
		return options;
	}
	
	public Message getMesssage() {
		return m;
	}
	
	public int getID() {
		return pollID;
	}
	
	private String intToEmoji(int i) {
		switch(i) {
		case 1:
			return "\u0031\u20E3";
		case 2:
			return "\u0032\u20E3";
		case 3:
			return "\u0033\u20E3";
		case 4:
			return "\u0034\u20E3";
		case 5:
			return "\u0035\u20E3";
		case 6:
			return "\u0036\u20E3";
		case 7:
			return "\u0037\u20E3";
		case 8:
			return "\u0038\u20E3";
		case 9:
			return "\u0039\u20E3";
		default:
			return "\u0030\u20E3";
		}
	}
}
