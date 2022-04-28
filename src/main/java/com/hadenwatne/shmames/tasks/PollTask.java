package com.hadenwatne.shmames.tasks;

import com.hadenwatne.shmames.App;
import com.hadenwatne.shmames.enums.BotSettingName;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.enums.Langs;
import com.hadenwatne.shmames.models.PollModel;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import com.hadenwatne.shmames.services.TextFormatService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import java.awt.*;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimerTask;

public class PollTask extends TimerTask {
	private PollModel pollModel;
	private Message message;
	private Lang lang;
	
	public PollTask(PollModel pollModel) {
		this.pollModel = pollModel;

		TextChannel textChannel = App.Shmames.getJDA().getTextChannelById(pollModel.getChannelID());

		if(textChannel != null) {
			this.lang = App.Shmames.getLanguageService().getLangFor(textChannel.getGuild());

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
	    		votes.put(TextFormatService.EmojiToInt(r.getReactionEmote().getName()), r.getCount()-1);
	    	}
	    	
			eBuilder.setAuthor(lang.getMsg(Langs.POLL_TITLE_RESULTS));
	        eBuilder.setColor(Color.GRAY);
	        eBuilder.setTitle(pollModel.getQuestion());
	        eBuilder.setFooter("#" + message.getChannel().getName() + " - Expired "+TextFormatService.GetFriendlyDateTime(c), null);
	        
	        for(int i = 0; i< pollModel.getOptions().size(); i++) {
	        	eBuilder.appendDescription("**"+(i+1)+"**: "+ pollModel.getOptions().get(i)+" **("+votes.get(i+1)+" votes)**"+"\n");
	        }
	
	        MessageEmbed embed = eBuilder.build();
	        MessageAction ma = message.getChannel().sendMessageEmbeds(embed);
	        
	        ma.complete();
			
			message.delete().queue();
			
			Brain b = App.Shmames.getStorageService().getBrain(message.getGuild().getId());
			b.getActivePolls().remove(pollModel);
			App.Shmames.getStorageService().getBrainController().saveBrain(b);
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
		eBuilder.setFooter("#" + channel.getName() + " - Expires "+ TextFormatService.GetFriendlyDateTime(calendar)+" - #"+pollModel.getID(), null);

		for(int i=0; i<pollModel.getOptions().size(); i++) {
			eBuilder.appendDescription("**"+(i+1)+"**: "+pollModel.getOptions().get(i)+"\n");
		}

		MessageEmbed messageEmbed = eBuilder.build();
		MessageAction messageAction = channel.sendMessageEmbeds(messageEmbed);
		Message message = messageAction.complete();
		pollModel.setMessageID(message.getId());

		return message;
	}

	private void populatePollVoteOptions(Message message) {
		for(int i=0; i<pollModel.getOptions().size(); i++) {
			message.addReaction(TextFormatService.IntToEmoji(i + 1)).queue();
		}
	}

	private void tryPinPollMessage(Message message) {
		Brain b = App.Shmames.getStorageService().getBrain(message.getGuild().getId());

		if(b.getSettingFor(BotSettingName.PIN_POLLS).getAsString().equalsIgnoreCase("true")) {
			try {
				message.pin().queue();
			}catch(Exception e) {
				message.getChannel().sendMessage(lang.getError(Errors.NO_PERMISSION_BOT)).queue();
			}
		}
	}
}
