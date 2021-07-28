package com.hadenwatne.shmames.commandbuilder;

public enum ParameterType {
    ANY, // String
    INTEGER, // Numbers only
    SELECTION, // Value must be selected from a list
    BOOLEAN,
    DISCORD_ROLE,
    DISCORD_CHANNEL,
    DISCORD_USER,
    DISCORD_EMOTE
}
