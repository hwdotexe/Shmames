package com.hadenwatne.fornax.storage.models;

import java.util.HashMap;
import java.util.List;

public class BotCloudData {
    public String botName;
    public HashMap<String, List<String>> activatedCommands;

    public BotCloudData() {}

    public BotCloudData(String botName) {
        this.botName = botName;
        this.activatedCommands = new HashMap<>();
    }
}
