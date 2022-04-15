package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.factories.EmbedFactory;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class Command {

    /*
    Runtime:
    - Server
    - Language
    - Logic
    - Responses

    Inherit / One-time:
    - Command structure
    - Requires guild
     */

    public final CommandStructure commandStructure;
    public final boolean requiresGuild;

    Command(boolean requiresGuild) {
        this.commandStructure = this.buildCommandStructure();
        this.requiresGuild = requiresGuild;
    }

    protected abstract CommandStructure buildCommandStructure();

    protected EmbedBuilder response(EmbedType type) {
        return EmbedFactory.GetEmbed(type, this.commandStructure.getName());
    }

    protected EmbedBuilder response(EmbedType type, String subLevel) {
        return EmbedFactory.GetEmbed(type, this.commandStructure.getName(), subLevel);
    }

    public abstract EmbedBuilder run(Lang language, Brain brain, ShmamesCommandData commandData);

    public CommandStructure getCommandStructure() {
        return this.commandStructure;
    }
}
