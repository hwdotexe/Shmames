package com.hadenwatne.devtesting;

import com.hadenwatne.botcore.App;
import com.hadenwatne.botcore.Bot;
import com.hadenwatne.shmames.enums.LogType;

public class ExampleBot extends Bot {
    @Override
    protected void afterInit() {
        this.registerListener(null);
        this.registerLanguageProvider(new LangPro());

        App.getLogger().Log(LogType.SYSTEM, "afterInit was called!");
    }

    @Override
    protected void registerCommands() {
        this.registerCommand(new ExampleCommand());
    }
}
