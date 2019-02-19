package tech.hadenw.shmamesbot;

import java.awt.Color;
import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.requests.restaction.MessageAction;

public class PollFinal extends TimerTask{
	private int pollID;
	
	public PollFinal(int id) {
		pollID = id;
	}
	
	public void run() {
		for(Poll p : Shmames.getPolls()) {
			if(p.getID() == pollID) {
				Calendar c = Calendar.getInstance();
		    	c.setTime(new Date());
				
				EmbedBuilder eBuilder = new EmbedBuilder();
				
				eBuilder.setAuthor("== POLL (results) ==");
		        eBuilder.setColor(Color.GRAY);
		        eBuilder.setTitle(p.getQuestion());
		        eBuilder.setFooter("#" + p.getMesssage().getChannel().getName() + " - Expired "+Utils.getFriendlyDate(c), null);
		        
		        for(int i=0; i<p.getOptions().size(); i++) {
		        	eBuilder.appendDescription("**"+(i+1)+"**: "+p.getOptions().get(i)+" **("+p.getVotes().get(i)+" votes)**"+"\n");
		        }

		        MessageEmbed embed = eBuilder.build();
		        MessageAction ma = p.getMesssage().getChannel().sendMessage(embed);
		        
		        ma.complete();
				
				p.getMesssage().delete().queue();
				Shmames.getPolls().remove(p);
				
				break;
			}
		}
	}
}