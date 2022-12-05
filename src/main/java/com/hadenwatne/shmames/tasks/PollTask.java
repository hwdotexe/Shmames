package com.hadenwatne.shmames.tasks;

import com.hadenwatne.shmames.App;
import com.hadenwatne.shmames.enums.BotSettingName;
import com.hadenwatne.shmames.models.PollModel;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Language;
import com.hadenwatne.shmames.services.TextFormatService;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;

import java.util.TimerTask;

public class PollTask extends TimerTask {
	private final PollModel pollModel;
	private final TextChannel channel;
	private Message message;
	private Language language;

	public PollTask(PollModel pollModel) {
		this.pollModel = pollModel;
		this.channel = App.Shmames.getJDA().getTextChannelById(this.pollModel.getChannelID());

		if (this.channel != null) {
			this.language = App.Shmames.getLanguageService().getLangFor(this.channel.getGuild());

			if(this.pollModel.isActive()) {
				this.channel.retrieveMessageById(this.pollModel.getMessageID()).queue(success -> {
					this.message = success;

					// Perform first-time Poll tasks if this Poll hasn't started yet.
					if (!this.pollModel.hasStarted()) {
						this.tryPinPollMessage(this.message);
						this.populatePollVoteOptions(this.message);

						this.pollModel.updateMessageEmbed(language, this.channel.getName(), this.message);
						this.pollModel.setHasStarted(true);
					}
				});
			}
		}
	}

	/**
	 * Called when the poll has expired.
	 */
	public void run() {
		if (pollModel.isActive()) {
			pollModel.setActive(false);

			this.channel.retrieveMessageById(this.pollModel.getMessageID()).queue(success -> {
				this.pollModel.updateMessageEmbed(this.language, this.channel.getName(), success);

				Brain brain = App.Shmames.getStorageService().getBrain(message.getGuild().getId());
				brain.getActivePolls().remove(pollModel);
			});
		}

		this.cancel();
	}

	private void populatePollVoteOptions(Message message) {
		for (int i = 0; i < pollModel.getOptions().size(); i++) {
			String emoji = TextFormatService.NumberToLetter(i + 1);

			message.addReaction(Emoji.fromUnicode(emoji)).queue();
		}

		message.addReaction(Emoji.fromUnicode(TextFormatService.EMOJI_RED_X)).queue();
	}

	private void tryPinPollMessage(Message message) {
		Brain b = App.Shmames.getStorageService().getBrain(message.getGuild().getId());

		if (b.getSettingFor(BotSettingName.POLL_PIN).getAsBoolean()) {
			try {
				message.pin().queue();
			} catch (Exception ignored) {

			}
		}
	}
}
