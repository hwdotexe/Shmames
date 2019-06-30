package tech.hadenw.shmamesbot;

import java.awt.Color;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.requests.restaction.MessageAction;
import tech.hadenw.shmamesbot.brain.BotSettingName;
import tech.hadenw.shmamesbot.brain.Brain;

public class Poll extends TimerTask{
	private Message m;
	private String question;
	private List<String> options;
	private HashMap<Integer, Integer> votes;
	private int pollID;
	private Calendar c;
	
	public Poll(MessageChannel ch, String q, List<String> o, int time, String interval) {
		question=q;
		options=o;
		pollID = Shmames.getPollID();
		votes = new HashMap<Integer, Integer>();
		
		c = Calendar.getInstance();
    	c.setTime(new Date());
    	
    	switch(interval) {
    	case "d":
    		c.add(Calendar.HOUR, 24*time);
    		break;
    	case "h":
    		c.add(Calendar.HOUR, time);
    		break;
    	case "m":
    		c.add(Calendar.MINUTE, time);
    		break;
    	case "s":
    		c.add(Calendar.SECOND, time);
    		break;
    	default:
    		c.add(Calendar.MINUTE, time);
    	}
		
		EmbedBuilder eBuilder = new EmbedBuilder();
		
		eBuilder.setAuthor("== POLL ==");
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
		t.schedule(this, c.getTime());
		
		// Pin the message
		Brain b = Shmames.getBrains().getBrain(m.getGuild().getId());
		if(b.getSettingFor(BotSettingName.DO_PIN).getValue().equalsIgnoreCase("true")) {
			try {
				m.pin().queue();
			}catch(Exception e) {
				m.getChannel().sendMessage(Errors.NO_PERMISSION_BOT).queue();
			}
		}
	}
	
	public void run() {
    	c.setTime(new Date());
		
		EmbedBuilder eBuilder = new EmbedBuilder();
		
		eBuilder.setAuthor("== POLL (results) ==");
        eBuilder.setColor(Color.GRAY);
        eBuilder.setTitle(question);
        eBuilder.setFooter("#" + m.getChannel().getName() + " - Expired "+Utils.getFriendlyDate(c), null);
        
        for(int i=0; i<options.size(); i++) {
        	eBuilder.appendDescription("**"+(i+1)+"**: "+options.get(i)+" **("+votes.get(i)+" votes)**"+"\n");
        }

        MessageEmbed embed = eBuilder.build();
        MessageAction ma = m.getChannel().sendMessage(embed);
        
        ma.complete();
		
		m.delete().queue();
		Shmames.getPolls().remove(this);
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
