package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.enums.LogType;
import com.hadenwatne.shmames.factories.EmbedFactory;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import com.hadenwatne.shmames.services.LoggingService;
import com.hadenwatne.shmames.services.PaginationService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.ArrayList;
import java.util.List;

public abstract class Command {
    private final CommandStructure commandStructure;
    private final boolean requiresGuild;
    private final List<MessageEmbed.Field> helpFields;

    Command(boolean requiresGuild) {
        this.commandStructure = this.buildCommandStructure();
        this.requiresGuild = requiresGuild;
        this.helpFields = new ArrayList<>();

        // Build our command's Help fields.
        String list = PaginationService.GenerateList(this.commandStructure.getAliases(), -1, false, false);
        list = list.length() == 0 ? "None" : list;

        this.helpFields.add(new MessageEmbed.Field("Aliases", list, true));
        this.helpFields.add(new MessageEmbed.Field("Server-only", this.requiresGuild ? "Yes" : "No", true));
        this.helpFields.add(new MessageEmbed.Field("Description", this.commandStructure.getDescription(), false));
        this.helpFields.add(new MessageEmbed.Field("Usage", this.commandStructure.getUsage(), true));
        this.helpFields.add(new MessageEmbed.Field("Examples", this.commandStructure.getExamples(), true));

        LoggingService.Log(LogType.SYSTEM, "\tLoaded "+this.commandStructure.getName());
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

    public boolean requiresGuild() {
        return this.requiresGuild;
    }

    public List<MessageEmbed.Field> getHelpFields() {
        return this.helpFields;
    }
}
