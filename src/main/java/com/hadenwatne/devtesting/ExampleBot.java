package com.hadenwatne.devtesting;

import com.hadenwatne.fornax.App;
import com.hadenwatne.fornax.Bot;
import com.hadenwatne.fornax.service.types.LogType;

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
