package com.hadenwatne.shmames.tasks;

import com.hadenwatne.corvus.Corvus;
import com.hadenwatne.corvus.CorvusBuilder;
import com.hadenwatne.fornax.App;
import com.hadenwatne.shmames.Shmames;
import com.hadenwatne.shmames.models.PollModel;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.services.PollService;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.TimerTask;

public class PollTask extends TimerTask {
	private final PollModel pollModel;
	private final Shmames shmames;
	private final Brain brain;

	public PollTask(PollModel pollModel, Brain brain, Shmames shmames) {
		this.pollModel = pollModel;
		this.shmames = shmames;
		this.brain = brain;
	}

	public void run() {
		try {
			if (pollModel.isActive()) {
				pollModel.setActive(false);
				brain.getActivePolls().remove(pollModel);

				CorvusBuilder builder = PollService.BuildPoll(shmames, pollModel);

				Guild server = shmames.getJDA().getGuildById(brain.getGuildID());
				TextChannel channel = server.getTextChannelById(pollModel.getChannelID());

				channel.retrieveMessageById(pollModel.getMessageID()).queue(message -> message.editMessageEmbeds(Corvus.convert(builder).build()).setComponents().queue());
			}
		} catch (Exception e) {
			App.getLogger().LogException(e);
		} finally {
			this.cancel();
		}
	}
}