package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.enums.Langs;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.services.ShmamesService;
import com.hadenwatne.shmames.enums.BotSettingName;

public class ResetEmoteStats implements ICommand {
	private final CommandStructure commandStructure;

	public ResetEmoteStats() {
		this.commandStructure = CommandBuilder.Create("resetemotestats", "Reset emote usage statistics.")
				.addAlias("reset emote stats")
				.build();
	}

	@Override
	public CommandStructure getCommandStructure() {
		return this.commandStructure;
	}

	@Override
	public String getExamples() {
		return "`resetemotestats`";
	}

	@Override
	public String run (Lang lang, Brain brain, ShmamesCommandData data) {
		if(ShmamesService.CheckUserPermission(data.getServer(), brain.getSettingFor(BotSettingName.RESET_EMOTE_STATS), data.getAuthor())){
			brain.getEmoteStats().clear();

			return lang.getMsg(Langs.RESET_EMOTE_STATS);
		}else{
			return lang.getError(Errors.NO_PERMISSION_USER, true);
		}
	}
	
	@Override
	public boolean requiresGuild() {
		return true;
	}
}
