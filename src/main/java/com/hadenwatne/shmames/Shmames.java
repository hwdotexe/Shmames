package com.hadenwatne.shmames;

import com.hadenwatne.fornax.Bot;
import com.hadenwatne.shmames.commands.*;
import com.hadenwatne.shmames.language.LanguageProvider;
import com.hadenwatne.shmames.listeners.ChatListener;
import com.hadenwatne.shmames.listeners.FirstJoinListener;
import com.hadenwatne.shmames.listeners.ReactListener;
import com.hadenwatne.shmames.services.RandomService;
import com.hadenwatne.shmames.services.settings.SettingsService;
import com.hadenwatne.shmames.tasks.SaveDataTask;

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
		registerCommand(new Enhance());
		registerCommand(new FamilyCmd());
		registerCommand(new ForumWeapon());
		registerCommand(new Gacha());
		registerCommand(new GIF());
		registerCommand(new Hangman());
		registerCommand(new Help());
		registerCommand(new IdiotThat());
		registerCommand(new ListCmd());
		registerCommand(new ListEmoteStats());
		registerCommand(new Minesweeper());
		registerCommand(new Modify());
		registerCommand(new Music());
		registerCommand(new Pin());
		registerCommand(new Poll());
		registerCommand(new React());
		registerCommand(new Report());
		registerCommand(new ResetEmoteStats());
		registerCommand(new Roles());
		registerCommand(new Roll());
		registerCommand(new Say());
		registerCommand(new Storytime());
		registerCommand(new Tally());
		registerCommand(new Thoughts());
		registerCommand(new Timer());
		registerCommand(new WhatAreTheOdds());
		registerCommand(new WhatShouldIDo());
		registerCommand(new When());
		registerCommand(new Wiki());
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
}
