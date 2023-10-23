package com.hadenwatne.devtesting;

import com.hadenwatne.botcore.App;
import com.hadenwatne.botcore.Bot;
import com.hadenwatne.shmames.enums.LogType;

public class ExampleBot extends Bot {
    @Override
    protected void initialize() {
        App.getLogger().Log(LogType.SYSTEM, "initialize was called!");
    }

    @Override
    protected void registerCommands() {
        // Register commands here.
        this.registerCommand(null);
    }
}
