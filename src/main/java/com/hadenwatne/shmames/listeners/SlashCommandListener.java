package com.hadenwatne.shmames.listeners;

import com.hadenwatne.shmames.App;
import com.hadenwatne.shmames.commands.Command;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.factories.EmbedFactory;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import com.hadenwatne.shmames.services.MessageService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class SlashCommandListener extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String commandText = normalizeCommandText(event);
        Command command = App.Shmames.getCommandHandler().PreProcessCommand(commandText);

        // Ensure we own this command before continuing.
        if(command != null) {
            Brain brain = null;

            if(event.isFromGuild()) {
                brain = App.Shmames.getStorageService().getBrain(event.getGuild().getId());
            }

            event.deferReply().queue();
            handleCommand(command, event.getHook(), commandText, brain);
        }
    }

    private String normalizeCommandText(SlashCommandInteractionEvent event) {
        StringBuilder sb = new StringBuilder();

        sb.append(event.getName());

        if(event.getSubcommandGroup() != null) {
            sb.append(" ");
            sb.append(event.getSubcommandGroup());
        }

        if(event.getSubcommandName() != null) {
            sb.append(" ");
            sb.append(event.getSubcommandName());
        }

        for(OptionMapping option : event.getOptions()) {
            sb.append(" ");
            sb.append(option.getAsString());
        }

        return sb.toString();
    }

    private void handleCommand(Command command, InteractionHook hook, String commandText, Brain brain) {
        Lang lang = App.Shmames.getLanguageService().getLangFor(brain);
        ExecutingCommand executingCommand = new ExecutingCommand(lang, brain);

        if(command != null) {
            executingCommand.setCommandName(command.getCommandStructure().getName());
            executingCommand.setInteractionHook(hook);

            App.Shmames.getCommandHandler().HandleCommand(command, executingCommand, commandText);
        } else {
            EmbedBuilder embed = EmbedFactory.GetEmbed(EmbedType.ERROR, Errors.COMMAND_NOT_FOUND.name())
                    .setDescription(lang.getError(Errors.COMMAND_NOT_FOUND));

            MessageService.ReplyToMessage(hook, embed);
        }
    }
}
