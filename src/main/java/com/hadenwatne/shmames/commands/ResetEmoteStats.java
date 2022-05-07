package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.enums.BotSettingName;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.enums.Langs;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.services.ShmamesService;
import net.dv8tion.jda.api.EmbedBuilder;

public class ResetEmoteStats extends Command {
	public ResetEmoteStats() {
		super(true);
	}

	@Override
	protected CommandStructure buildCommandStructure() {
		return CommandBuilder.Create("resetemotestats", "Reset emote usage statistics.")
				.addAlias("reset emote stats")
				.build();
	}

	@Override
	public EmbedBuilder run (ExecutingCommand executingCommand) {
		Brain brain = executingCommand.getBrain();

		if(ShmamesService.CheckUserPermission(executingCommand.getServer(), brain.getSettingFor(BotSettingName.RESET_EMOTE_STATS), executingCommand.getAuthorMember())){
			brain.getEmoteStats().clear();

			return response(EmbedType.SUCCESS)
					.setDescription(executingCommand.getLanguage().getMsg(Langs.RESET_EMOTE_STATS));
		}else{
			return response(EmbedType.ERROR)
					.setDescription(executingCommand.getLanguage().getError(Errors.NO_PERMISSION_USER));
		}
	}
}
