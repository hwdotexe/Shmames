package com.hadenwatne.shmames.commands;

import com.hadenwatne.botcore.command.Command;
import com.hadenwatne.botcore.command.builder.CommandBuilder;
import com.hadenwatne.botcore.command.builder.CommandStructure;
import com.hadenwatne.shmames.enums.BotSettingName;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.enums.ErrorKeys;
import com.hadenwatne.shmames.enums.LanguageKeys;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.services.ShmamesService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;

public class ResetEmoteStats extends Command {
	public ResetEmoteStats() {
		super(true);
	}

	@Override
	protected Permission[] configureRequiredBotPermissions() {
		return new Permission[]{Permission.MESSAGE_SEND, Permission.MESSAGE_SEND_IN_THREADS, Permission.MESSAGE_EMBED_LINKS};
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
					.setDescription(executingCommand.getLanguage().getMsg(LanguageKeys.RESET_EMOTE_STATS));
		}else{
			return response(EmbedType.ERROR)
					.setDescription(executingCommand.getLanguage().getError(ErrorKeys.NO_PERMISSION_USER));
		}
	}
}
