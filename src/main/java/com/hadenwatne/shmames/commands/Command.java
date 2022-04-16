package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.factories.EmbedFactory;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import com.hadenwatne.shmames.services.PaginationService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
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
    - Author
    - Channel/Hook

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

    public abstract EmbedBuilder run(ExecutingCommand executingCommand);

    public CommandStructure getCommandStructure() {
        return this.commandStructure;
    }

    // TODO load this at instantiation so we don't cause performance issues if called repeatedly
    public List<MessageEmbed.Field> getHelpFields() {
        List<MessageEmbed.Field> fields = new ArrayList<>();
        String list = PaginationService.GenerateList(this.commandStructure.getAliases(), -1, false, false);
        list = list.length() == 0 ? "None" : list;

        fields.add(new MessageEmbed.Field("Aliases", list, true));
        fields.add(new MessageEmbed.Field("Server-only", this.requiresGuild ? "Yes" : "No", true));
        fields.add(new MessageEmbed.Field("Description", this.commandStructure.getDescription(), false));
        fields.add(new MessageEmbed.Field("Usage", this.commandStructure.getUsage(), true));
        fields.add(new MessageEmbed.Field("Examples", this.commandStructure.getExamples(), true));

        return fields;
    }
}
