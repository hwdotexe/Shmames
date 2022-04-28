package com.hadenwatne.shmames.tasks;

import com.hadenwatne.shmames.App;
import com.hadenwatne.shmames.enums.BotSettingName;
import com.hadenwatne.shmames.models.PollModel;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import com.hadenwatne.shmames.services.TextFormatService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.TimerTask;

public class PollTask extends TimerTask {
	private final PollModel pollModel;
	private Message message;
	private Lang lang;
	private TextChannel channel;

	public PollTask(PollModel pollModel) {
		this.pollModel = pollModel;

		this.channel = App.Shmames.getJDA().getTextChannelById(pollModel.getChannelID());

		if (this.channel != null) {
			this.channel.retrieveMessageById(this.pollModel.getMessageID()).queue(success -> {
				this.message = success;
				this.lang = App.Shmames.getLanguageService().getLangFor(this.channel.getGuild());

				if (!this.pollModel.hasStarted()) {
					this.tryPinPollMessage(this.message);
					this.populatePollVoteOptions(this.message);
					this.pollModel.setHasStarted(true);
				}

				EmbedBuilder embedBuilder = this.pollModel.buildMessageEmbed(this.lang, this.channel.getName(), this.pollModel.getExpiration(), false);

				this.message.editMessageEmbeds(embedBuilder.build()).queue();
			});
		}
	}

	/**
	 * Called when the poll has expired.
	 */
	public void run() {
		if (pollModel.isActive()) {
			pollModel.setActive(false);

			/*
			TODO: This is clearing out the results section, is there a way to preserve it without regenerating?
			EmbedBuilder embedBuilder = this.pollModel.buildMessageEmbed(this.lang, this.channel.getName(), this.pollModel.getExpiration(), true);

			this.message.editMessageEmbeds(embedBuilder.build()).queue();
			*/

			this.message.clearReactions().queue();

			Brain brain = App.Shmames.getStorageService().getBrain(message.getGuild().getId());
			brain.getActivePolls().remove(pollModel);
		}

		this.cancel();
	}

	private void populatePollVoteOptions(Message message) {
		for (int i = 0; i < pollModel.getOptions().size(); i++) {
			String emoji = TextFormatService.NumberToLetter(i + 1);

			message.addReaction(emoji).queue();
		}
	}

	private void tryPinPollMessage(Message message) {
		Brain b = App.Shmames.getStorageService().getBrain(message.getGuild().getId());

		if (b.getSettingFor(BotSettingName.PIN_POLLS).getAsBoolean()) {
			try {
				message.pin().queue();
			} catch (Exception ignored) {

			}
		}
	}
}
