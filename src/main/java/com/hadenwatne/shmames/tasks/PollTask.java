package com.hadenwatne.shmames.tasks;

import java.awt.Color;
import java.util.*;

import com.hadenwatne.shmames.enums.BotSettingName;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.enums.Langs;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import com.hadenwatne.shmames.models.PollModel;
import com.hadenwatne.shmames.Shmames;
import com.hadenwatne.shmames.Utils;

public class PollTask extends TimerTask{
	private PollModel pollModel;
	private Message message;
	private Lang lang;
	
	public PollTask(PollModel pollModel) {
		this.pollModel = pollModel;

		TextChannel textChannel = Shmames.getJDA().getTextChannelById(pollModel.getChannelID());

		if(textChannel != null) {
			this.lang = Shmames.getLangFor(textChannel.getGuild());

			if(pollModel.getMessageID() == null) {
				this.message = this.sendMessageEmbed(textChannel, pollModel.getExpiration());
				this.populatePollVoteOptions(message);
				this.tryPinPollMessage(message);
			} else {
				this.message = textChannel.retrieveMessageById(pollModel.getMessageID()).complete();
			}
		}
	}
	
	public void run() {
		if(pollModel.isActive()) {
			pollModel.setActive(false);
			
			EmbedBuilder eBuilder = new EmbedBuilder();
			Calendar c = Calendar.getInstance();
	    	c.setTime(new Date());
			
	    	message = message.getChannel().retrieveMessageById(message.getId()).complete();
	    	HashMap<Integer, Integer> votes = new HashMap<Integer, Integer>();
	    	
	    	for(MessageReaction r : message.getReactions()) {
	    		votes.put(Utils.emojiToInt(r.getReactionEmote().getName()), r.getCount()-1);
	    	}
	    	
			eBuilder.setAuthor(lang.getMsg(Langs.POLL_TITLE_RESULTS));
	        eBuilder.setColor(Color.GRAY);
	        eBuilder.setTitle(pollModel.getQuestion());
	        eBuilder.setFooter("#" + message.getChannel().getName() + " - Expired "+Utils.getFriendlyDate(c), null);
	        
	        for(int i = 0; i< pollModel.getOptions().size(); i++) {
	        	eBuilder.appendDescription("**"+(i+1)+"**: "+ pollModel.getOptions().get(i)+" **("+votes.get(i+1)+" votes)**"+"\n");
	        }
	
	        MessageEmbed embed = eBuilder.build();
	        MessageAction ma = message.getChannel().sendMessage(embed);
	        
	        ma.complete();
			
			message.delete().queue();
			
			Brain b = Shmames.getBrains().getBrain(message.getGuild().getId());
			b.getActivePolls().remove(pollModel);
			Shmames.getBrains().saveBrain(b);
		}
		
		this.cancel();
	}

	private Message sendMessageEmbed(TextChannel channel, Date expiration) {
		EmbedBuilder eBuilder = new EmbedBuilder();
		Calendar calendar = Calendar.getInstance();

		calendar.setTime(expiration);

		eBuilder.setAuthor(this.lang.getMsg(Langs.POLL_TITLE));
		eBuilder.setColor(Color.GREEN);
		eBuilder.setTitle(this.pollModel.getQuestion());
		eBuilder.setFooter("#" + channel.getName() + " - Expires "+ Utils.getFriendlyDate(calendar)+" - #"+pollModel.getID(), null);

		for(int i=0; i<pollModel.getOptions().size(); i++) {
			eBuilder.appendDescription("**"+(i+1)+"**: "+pollModel.getOptions().get(i)+"\n");
		}

		MessageEmbed messageEmbed = eBuilder.build();
		MessageAction messageAction = channel.sendMessage(messageEmbed);
		Message message = messageAction.complete();
		pollModel.setMessageID(message.getId());

		return message;
	}

	private void populatePollVoteOptions(Message message) {
		for(int i=0; i<pollModel.getOptions().size(); i++) {
			message.addReaction(Utils.intToEmoji(i + 1)).queue();
		}
	}

	private void tryPinPollMessage(Message message) {
		Brain b = Shmames.getBrains().getBrain(message.getGuild().getId());

		if(b.getSettingFor(BotSettingName.PIN_POLLS).getValue().equalsIgnoreCase("true")) {
			try {
				message.pin().queue();
			}catch(Exception e) {
				message.getChannel().sendMessage(lang.getError(Errors.NO_PERMISSION_BOT, true)).queue();
			}
		}
	}
}
