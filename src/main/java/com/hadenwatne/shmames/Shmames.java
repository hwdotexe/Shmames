package com.hadenwatne.shmames;

import com.hadenwatne.botcore.Bot;
import com.hadenwatne.botcore.command.Execution;
import com.hadenwatne.shmames.commands.*;
import com.hadenwatne.shmames.listeners.ChatListener;
import com.hadenwatne.shmames.listeners.FirstJoinListener;
import com.hadenwatne.shmames.listeners.ReactListener;
import com.hadenwatne.shmames.music.MusicManager;
import com.hadenwatne.shmames.services.CacheService;
import com.hadenwatne.shmames.services.RandomService;
import com.hadenwatne.shmames.services.settings.SettingsService;

public class Shmames extends Bot {
	private MusicManager musicManager;
	private BrainController brainController;
	private SettingsService settingsService;

	@Override
	protected void afterInit() {
		RandomService.Init();
		CacheService.Init();

		settingsService = new SettingsService(this);
		brainController = new BrainController(this);

		this.registerListener(new ChatListener(this));
		this.registerListener(new FirstJoinListener(this));
		this.registerListener(new ReactListener(this));
	}

	@Override
	protected void registerCommands() {
		registerCommand(new Blame());
		registerCommand(new Cactpot());
		registerCommand(new Choose());
		registerCommand(new CringeThat());
		registerCommand(new Dev());
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

	public MusicManager getMusicManager() {
		return this.musicManager;
	}

	public BrainController getBrainController() {
		return brainController;
	}

	public SettingsService getSettingsService() {
		return settingsService;
	}
}
