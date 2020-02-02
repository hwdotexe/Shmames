package tech.hadenw.discordbot;

import java.awt.Color;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import tech.hadenw.discordbot.storage.BotSettingName;
import tech.hadenw.discordbot.storage.Brain;
import tech.hadenw.discordbot.tasks.PollTask;

public class Poll {
	private String question;
	private List<String> options;
	private String pollID;
	private Date expires;
	private String messageID;
	private String channelID;
	private boolean isActive;
	
	public Poll(MessageChannel ch, String q, List<String> o, int time, String interval, String id) {
		question=q;
		options=o;
		pollID = id;
		messageID="";
		channelID=ch.getId();
		isActive=true;
		
		Calendar c = Calendar.getInstance();
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
		
    	expires = c.getTime();
    	
		EmbedBuilder eBuilder = new EmbedBuilder();
		
		eBuilder.setAuthor("== POLL ==");
        eBuilder.setColor(Color.GREEN);
        eBuilder.setTitle(question);
        eBuilder.setFooter("#" + ch.getName() + " - Expires "+Utils.getFriendlyDate(c)+" - #"+pollID, null);
        
        for(int i=0; i<options.size(); i++) {
        	eBuilder.appendDescription("**"+(i+1)+"**: "+options.get(i)+"\n");
        }

        MessageEmbed embed = eBuilder.build();
        MessageAction ma = ch.sendMessage(embed);
        
        Message m = ma.complete();
        messageID=m.getId();
        
        // Add reaction emotes
        for(int i=0; i<options.size(); i++) {
        	m.addReaction(intToEmoji(i+1)).queue();
        }
        
        // Schedule the task
        Timer t = new Timer();
		t.schedule(new PollTask(this, m), c.getTime());
		
		// Pin the message
		Brain b = Shmames.getBrains().getBrain(m.getGuild().getId());
		if(b.getSettingFor(BotSettingName.PIN_POLLS
				).getValue().equalsIgnoreCase("true")) {
			try {
				m.pin().queue();
			}catch(Exception e) {
				m.getChannel().sendMessage(Errors.NO_PERMISSION_BOT).queue();
			}
		}
	}
	
	public boolean isActive() {
		return isActive;
	}
	
	public void setActive(boolean active) {
		isActive = active;
	}
	
	public String getMessageID() {
		return messageID;
	}
	
	public String getChannelID() {
		return channelID;
	}
	
	public Date getExpiration() {
		return expires;
	}
	
	public String getQuestion() {
		return question;
	}
	
	public List<String> getOptions(){
		return options;
	}
	
	public String getID() {
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
