package com.hadenwatne.shmames.models;

import com.hadenwatne.shmames.App;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.language.LanguageKey;
import com.hadenwatne.shmames.factories.EmbedFactory;
import com.hadenwatne.shmames.listeners.PollListener;
import com.hadenwatne.shmames.language.Language;
import com.hadenwatne.shmames.services.TextFormatService;
import com.hadenwatne.shmames.tasks.PollTask;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;

import java.util.*;

public class PollModel {
	private final String question;
	private String messageID;
	private final String channelID;
	private final String authorID;
	private final Calendar expires;
	private final List<String> options;
	private boolean isActive;
	private boolean hasStarted;

	private transient EmbedBuilder cachedEmbedBuilder;
	
	public PollModel(String channelID, String authorID, String messageID, String q, List<String> o, int seconds) {
		this.question = q;
		this.options = o;
		this.channelID = channelID;
		this.authorID = authorID;
		this.messageID = messageID;
		this.isActive = true;
		this.hasStarted = false;
		
		Calendar calendar = Calendar.getInstance();
    	calendar.setTime(new Date());
    	calendar.add(Calendar.SECOND, seconds);
		
    	this.expires = calendar;
	}

	public void startPollInstrumentation() {
		// Make sure this poll can expire over time.
		this.schedulePollExpirationTask(this.expires.getTime());

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

	public String getAuthorID() {
		return this.authorID;
	}

	public String getMessageID() {
		return messageID;
	}
	
	public String getChannelID() {
		return channelID;
	}
	
	public List<String> getOptions(){
		return options;
	}

	public void updateMessageEmbed(Language language, String channelName, Message message) {
		EmbedBuilder embedBuilder = buildMessageEmbed(language, channelName, message);

		message.editMessageEmbeds(embedBuilder.build()).queue();
	}

	private EmbedBuilder buildMessageEmbed(Language language, String channelName, Message message) {
		// Build the basic embed if we haven't built it before, or if the poll has expired.
		if(!this.isActive || this.cachedEmbedBuilder == null) {
			EmbedBuilder eBuilder = EmbedFactory.GetEmbed(this.isActive ? EmbedType.INFO : EmbedType.EXPIRED, "Poll");

			eBuilder.setTitle(this.isActive ? language.getMsg(LanguageKey.POLL_TITLE) : language.getMsg(LanguageKey.POLL_TITLE_RESULTS));
			eBuilder.addField("Topic", this.question, false);
			eBuilder.setFooter("#" + channelName + " - Expire" + (this.isActive ? "s on " + TextFormatService.GetFriendlyDateTime(this.expires) : "d"), null);

			if(this.isActive) {
				eBuilder.addField("Controls", "This poll will automatically close at the time below. React with "+TextFormatService.EMOJI_RED_X+" to close it early.", false);
			}

			eBuilder.addField(buildOptionsField());

			this.cachedEmbedBuilder = eBuilder;
		}

		// Clone an EmbedBuilder so we can modify it.
		EmbedBuilder response = new EmbedBuilder(cachedEmbedBuilder);

		// Perform the only expensive work we need to.
		HashMap<Integer, Integer> votes = getPollReactionVotes(message);

		// Only draw votes if the bot is finished reacting to itself.
		if(votes.size() == this.options.size() + 1) {
			response.addField(buildVoteField(votes));
		}

		// Clear any reactions on the message if this Poll has ended.
		if(!isActive) {
			message.clearReactions().queue();
		}

		return response;
	}

	private MessageEmbed.Field buildOptionsField() {
		StringBuilder pollOptions = new StringBuilder();

		for (int i = 0; i < this.options.size(); i++) {
			String emoji = TextFormatService.NumberToLetter(i + 1);

			if (pollOptions.length() > 0) {
				pollOptions.append(System.lineSeparator());
			}

			pollOptions.append(emoji)
					.append(" : ")
					.append(this.options.get(i));
		}

		return new MessageEmbed.Field("Options", pollOptions.toString(), false);
	}

	private MessageEmbed.Field buildVoteField(HashMap<Integer, Integer> votes) {
		StringBuilder voteReadout = new StringBuilder();
		int totalVotes = votes.get(0);

		for(int i=1; i<=this.options.size(); i++) {
			String emoji = TextFormatService.NumberToLetter(i);
			int optionVotes = votes.get(i);
			double percentageOfTotal = (double) optionVotes / (double) totalVotes;
			String percentage = Math.round(percentageOfTotal * 100) + "%";

			if (voteReadout.length() > 0) {
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

	public HashMap<Integer, Integer> getPollReactionVotes(Message message) {
		// Count the votes and update the embed.
		HashMap<Integer, Integer> votes = new HashMap<>();
		int totalVotes = 0;

		// Count the votes.
		for (MessageReaction r : message.getReactions()) {
			int voteOption = TextFormatService.LetterToNumber(r.getEmoji().getName());

			if (voteOption > 0) {
				// Remove 1 because the bot adds a default reaction.
				int optionVotes = Math.max(0, r.getCount() - 1);

				votes.put(voteOption, optionVotes);
				totalVotes += optionVotes;
			}
		}

		// Key 0 will be our placeholder for the total number of votes.
		votes.put(0, totalVotes);

		return votes;
	}

	private void schedulePollExpirationTask(Date expiration) {
		Timer t = new Timer();
		t.schedule(new PollTask(this), expiration);
	}
}
