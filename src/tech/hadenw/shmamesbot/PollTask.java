package tech.hadenw.shmamesbot;

import java.awt.Color;
import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.requests.restaction.MessageAction;
import tech.hadenw.shmamesbot.brain.Brain;

public class PollTask extends TimerTask{
	private Poll p;
	private Message m;
	
	public PollTask(Poll poll, Message msg) {
		p=poll;
		m=msg;
	}
	
	public void run() {
		EmbedBuilder eBuilder = new EmbedBuilder();
		Calendar c = Calendar.getInstance();
    	c.setTime(new Date());
		
    	m = m.getChannel().getMessageById(m.getId()).complete();
    	
    	for(MessageReaction r : m.getReactions()) {
    		System.out.println("Count: "+r.getCount());
    		System.out.println("Emote: "+r.getReactionEmote().getName());
    	}
    	
		eBuilder.setAuthor("== POLL (results) ==");
        eBuilder.setColor(Color.GRAY);
        eBuilder.setTitle(p.getQuestion());
        eBuilder.setFooter("#" + m.getChannel().getName() + " - Expired "+Utils.getFriendlyDate(c), null);
        
        for(int i=0; i<p.getOptions().size(); i++) {
        	eBuilder.appendDescription("**"+(i+1)+"**: "+p.getOptions().get(i)+" **("+p.getVotes().get(i)+" votes)**"+"\n");
        }

        MessageEmbed embed = eBuilder.build();
        MessageAction ma = m.getChannel().sendMessage(embed);
        
        ma.complete();
		
		m.delete().queue();
		
		Brain b = Shmames.getBrains().getBrain(m.getGuild().getId());
		b.getActivePolls().remove(p);
		Shmames.getBrains().saveBrain(b);
	}
}
