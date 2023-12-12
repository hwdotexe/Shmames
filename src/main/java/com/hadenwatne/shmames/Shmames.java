package com.hadenwatne.shmames;

import com.hadenwatne.fornax.Bot;
import com.hadenwatne.shmames.commands.*;
import com.hadenwatne.shmames.language.LanguageProvider;
import com.hadenwatne.shmames.listeners.ChatListener;
import com.hadenwatne.shmames.listeners.FirstJoinListener;
import com.hadenwatne.shmames.listeners.ReactListener;
import com.hadenwatne.shmames.services.RandomService;
import com.hadenwatne.shmames.services.settings.BotSetting;
import com.hadenwatne.shmames.services.settings.SettingsService;
import com.hadenwatne.shmames.services.settings.types.BotSettingType;
import com.hadenwatne.shmames.tasks.SaveDataTask;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

public class Shmames extends Bot {
	private BrainController brainController;
	private SettingsService settingsService;
	private LanguageProvider languageProvider;

	@Override
	protected void afterInit() {
		RandomService.Init();

		this.languageProvider = new LanguageProvider(this);
		this.registerLanguageProvider(this.languageProvider);

		settingsService = new SettingsService(this);
		brainController = new BrainController(this);

		this.registerListener(new ChatListener(this));
		this.registerListener(new FirstJoinListener(this));
		this.registerListener(new ReactListener(this));

		new SaveDataTask(this);
	}

	@Override
	protected void registerCommands() {
		registerCommand(new Blame());
		registerCommand(new Cactpot());
		registerCommand(new Choose());
		registerCommand(new Dev(this));
		registerCommand(new EightBall());
		registerCommand(new FamilyCmd());
		registerCommand(new ForumWeapon());
		registerCommand(new Gacha());
		registerCommand(new Hangman());
		registerCommand(new Help());
		registerCommand(new ListCmd());
		registerCommand(new Modify());
		registerCommand(new Music());
		registerCommand(new Pin());
		registerCommand(new Poll());
		registerCommand(new React());
		registerCommand(new Report());
		registerCommand(new Roles());
		registerCommand(new Storytime());
		registerCommand(new Timer());
		registerCommand(new WhatShouldIDo());
		registerCommand(new When());
		registerCommand(new GIF(this));
		registerCommand(new Minesweeper());
		registerCommand(new Odds());
		registerCommand(new Roll());
		registerCommand(new Say());
		registerCommand(new Tally(this));
		registerCommand(new Wiki(this));
	}

	public BrainController getBrainController() {
		return brainController;
	}

	public SettingsService getSettingsService() {
		return settingsService;
	}

	public LanguageProvider getLanguageProvider() {
		return this.languageProvider;
	}

	public boolean checkPermission(Guild server, BotSetting setting, Member member) {
		if(server != null) {
			if (setting.getType() == BotSettingType.ROLE) {
				if (isDebugMode())
					return true;

				if (member != null) {
					// Always return true for administrators regardless of setting.
					if(member.hasPermission(Permission.ADMINISTRATOR)) {
						return true;
					}

					String roleString = setting.getAsString();

					// If the role requires administrator, make sure they are admin.
					if (roleString.equals("administrator")) {
						return member.hasPermission(Permission.ADMINISTRATOR);
					}

					Role role = setting.getAsRole(server);

					// Check if the user has the given role.
					if(server.getPublicRole().getIdLong() == role.getIdLong()) {
						return true;
					}

					return member.getRoles().contains(role);
				}
			}
		}

		return false;
	}
}
