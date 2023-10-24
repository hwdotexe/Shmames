package com.hadenwatne.botcore.command;

import com.hadenwatne.botcore.App;
import com.hadenwatne.botcore.command.builder.CommandStructure;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.botcore.service.types.LogType;
import com.hadenwatne.shmames.factories.EmbedFactory;
import com.hadenwatne.shmames.services.PaginationService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.ArrayList;
import java.util.List;
public abstract class Command {
    private final CommandStructure commandStructure;
    private final Permission[] botPermissions;
    private final boolean requiresGuild;
    private final boolean isNSFW;
    private final boolean availableByDefault;
    private final boolean isPro;
    private final List<MessageEmbed.Field> helpFields;

    public Command(boolean requiresGuild) {
        this.commandStructure = this.buildCommandStructure();
        this.botPermissions = this.configureRequiredBotPermissions();
        this.requiresGuild = requiresGuild;
        this.isNSFW = false;
        this.availableByDefault = true;
        this.isPro = false;
        this.helpFields = new ArrayList<>();

        configureCommand();
    }

    public Command(boolean requiresGuild, boolean isNSFW) {
        this.commandStructure = this.buildCommandStructure();
        this.botPermissions = this.configureRequiredBotPermissions();
        this.requiresGuild = requiresGuild;
        this.isNSFW = isNSFW;
        this.availableByDefault = true;
        this.isPro = false;
        this.helpFields = new ArrayList<>();

        configureCommand();
    }

    public Command(boolean requiresGuild, boolean isNSFW, boolean availableByDefault, boolean isPro) {
        this.commandStructure = this.buildCommandStructure();
        this.botPermissions = this.configureRequiredBotPermissions();
        this.requiresGuild = requiresGuild;
        this.isNSFW = isNSFW;
        this.availableByDefault = availableByDefault;
        this.isPro = isPro;
        this.helpFields = new ArrayList<>();

        configureCommand();
    }

    private void configureCommand() {
        // Build our command's Help fields.
        String list = PaginationService.GenerateList(this.commandStructure.getAliases(), -1, false, false);
        list = list.length() == 0 ? "None" : list;

        this.helpFields.add(new MessageEmbed.Field("Aliases", list, true));
        this.helpFields.add(new MessageEmbed.Field("Server-only", this.requiresGuild ? "Yes" : "No", true));
        this.helpFields.add(new MessageEmbed.Field("Description", this.commandStructure.getDescription(), false));
        this.helpFields.add(new MessageEmbed.Field("Usage", this.commandStructure.getUsage(), true));
        this.helpFields.add(new MessageEmbed.Field("Examples", this.commandStructure.getExamples(), true));

        App.getLogger().Log(LogType.SYSTEM, "\tLoaded " + this.commandStructure.getName());
    }

    protected abstract CommandStructure buildCommandStructure();

    protected abstract Permission[] configureRequiredBotPermissions();

    protected EmbedBuilder response(EmbedType type) {
        return EmbedFactory.GetEmbed(type, this.commandStructure.getName());
    }

    protected EmbedBuilder response(EmbedType type, String subLevel) {
        return EmbedFactory.GetEmbed(type, this.commandStructure.getName(), subLevel);
    }

    public abstract EmbedBuilder run(Execution execution);

    public CommandStructure getCommandStructure() {
        return this.commandStructure;
    }

    public Permission[] getRequiredPermissions() {
        return this.botPermissions;
    }

    public boolean requiresGuild() {
        return this.requiresGuild;
    }

    public boolean isNSFW() {
        return this.isNSFW;
    }

    public List<MessageEmbed.Field> getHelpFields() {
        return this.helpFields;
    }

    public boolean isAvailableByDefault() {
        return availableByDefault;
    }

    public boolean isPro() {
        return isPro;
    }
}