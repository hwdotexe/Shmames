package com.hadenwatne.shmames.tasks;

import com.hadenwatne.corvus.Corvus;
import com.hadenwatne.corvus.CorvusBuilder;
import com.hadenwatne.shmames.Shmames;
import com.hadenwatne.shmames.language.Language;
import com.hadenwatne.shmames.language.LanguageKey;
import com.hadenwatne.shmames.models.AlarmModel;
import com.hadenwatne.shmames.models.data.Brain;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.TimerTask;

public class AlarmTask extends TimerTask {
	public final AlarmModel model;
	private final Shmames shmames;
	private final Brain brain;

	public AlarmTask(AlarmModel model, Brain brain, Shmames shmames) {
		this.model = model;
		this.brain = brain;
		this.shmames = shmames;
	}

	public void run() {
		Guild server = shmames.getJDA().getGuildById(brain.getGuildID());
		TextChannel channel = server.getTextChannelById(model.getChannelID());
		CorvusBuilder response = getAlarmMessage(server);

		channel.retrieveMessageById(model.getMessageID()).queue(message -> {
			message.replyEmbeds(Corvus.convert(response).build()).queue();
		});

		brain.getTimers().remove(model);
	}

	private CorvusBuilder getAlarmMessage(Guild server) {
		CorvusBuilder builder = Corvus.info(shmames);
		Language language = shmames.getLanguageProvider().getLanguageForBrain(brain);
		Member member = server.retrieveMemberById(model.authorID).complete();

		builder.setDescription(shmames.getLanguageProvider().getMessageFromKey(language, LanguageKey.TIMER_ALERT.name(), member.getAsMention()));

		if (!model.getUserMessage().isEmpty()) {
			builder.addField("Memo", model.getUserMessage(), false);
		}

		return builder;
	}
}