package com.hadenwatne.shmames.models;

import com.hadenwatne.shmames.App;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.enums.Langs;
import com.hadenwatne.shmames.factories.EmbedFactory;
import com.hadenwatne.shmames.listeners.PollListener;
import com.hadenwatne.shmames.models.data.Lang;
import com.hadenwatne.shmames.services.TextFormatService;
import com.hadenwatne.shmames.tasks.PollTask;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.*;

public class PollModel {
	private final String question;
	private String messageID;
	private final String channelID;
	private final Date expires;
	private final List<String> options;
	private boolean isActive;
	private boolean hasStarted;
	
	public PollModel(String channelID, String messageID, String q, List<String> o, int seconds) {
		this.question = q;
		this.options = o;
		this.channelID = channelID;
		this.messageID = messageID;
		this.isActive = true;
		this.hasStarted = false;
		
		Calendar c = Calendar.getInstance();
    	c.setTime(new Date());
    	c.add(Calendar.SECOND, seconds);
		
    	this.expires = c.getTime();

    	// Make sure this poll can expire over time.
		this.schedulePollExpirationTask(this.expires);

		// Open a new listener unique to this poll.
		App.Shmames.getJDA().addEventListener(new PollListener(this));
	}

	public boolean isActive() {
		return isActive;
	}
	
	public void setActive(boolean active) {
		isActive = active;
	}

	public boolean hasStarted() {
		return this.hasStarted;
	}

	public void setHasStarted(boolean hasStarted) {
		this.hasStarted = hasStarted;
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

	/**
	 * Builds an Embed that displays the current poll options and votes.
	 *
	 * @param channelName The name of the channel to display in the footer.
	 * @param expiration  The time of expiration for this Poll.
	 * @return An EmbedBuilder representing this message.
	 */
	public EmbedBuilder buildMessageEmbed(Lang lang, String channelName, Date expiration, boolean expired) {
		EmbedBuilder eBuilder = EmbedFactory.GetEmbed(EmbedType.INFO, "Poll");
		Calendar calendar = Calendar.getInstance();
		StringBuilder pollOptions = new StringBuilder();

		calendar.setTime(expiration);

		eBuilder.setTitle(expired ? lang.getMsg(Langs.POLL_TITLE_RESULTS) : lang.getMsg(Langs.POLL_TITLE));
		eBuilder.addField("Topic", this.question, false);
		eBuilder.setFooter("#" + channelName + " - Expire" + (expired ? "d " : "s ") + TextFormatService.GetFriendlyDateTime(calendar), null);

		for (int i = 0; i < this.options.size(); i++) {
			String emoji = TextFormatService.NumberToLetter(i + 1);

			if (pollOptions.length() > 0) {
				pollOptions.append(System.lineSeparator());
			}

			pollOptions.append(emoji)
					.append(" : ")
					.append(this.options.get(i));
		}

		eBuilder.addField("Options", pollOptions.toString(), false);

		return eBuilder;
	}

	public MessageEmbed.Field buildVoteField(HashMap<Integer, Integer> votes, int totalVotes) {
		StringBuilder voteReadout = new StringBuilder();

		for(int i=0; i<this.options.size(); i++) {
			String emoji = TextFormatService.NumberToLetter(i + 1);
			int optionVotes = votes.get(i + 1);
			double percentageOfTotal = (double) optionVotes / (double) totalVotes;
			String percentage = Math.round(percentageOfTotal * 100) + "%";

			if(voteReadout.length() > 0) {
				voteReadout.append(System.lineSeparator());
			}

			voteReadout.append(emoji)
					.append(" : ")
					.append("**")
					.append(optionVotes)
					.append("**")
					.append(" • ")
					.append(percentage);
		}

		return new MessageEmbed.Field("Votes", voteReadout.toString(), false);
	}

	private void schedulePollExpirationTask(Date expiration) {
		Timer t = new Timer();
		t.schedule(new PollTask(this), expiration);
	}
}
