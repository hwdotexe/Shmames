package com.hadenwatne.shmames.listeners;

import com.hadenwatne.shmames.App;
import com.hadenwatne.shmames.commands.Command;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.enums.ErrorKeys;
import com.hadenwatne.shmames.factories.EmbedFactory;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Language;
import com.hadenwatne.botcore.service.LoggingService;
import com.hadenwatne.shmames.services.MessageService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
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
        Language language = App.Shmames.getLanguageService().getLangFor(brain);
        ExecutingCommand executingCommand = new ExecutingCommand(language, brain);

        executingCommand.setCommandName(command.getCommandStructure().getName());
        executingCommand.setInteractionHook(hook);

        // Check that the bot has the necessary Discord permissions to process this command.
        if(executingCommand.getServer() != null) {
            Guild server = executingCommand.getServer();
            StringBuilder noPerms = new StringBuilder();

            for (Permission p : command.getRequiredPermissions()) {
                if (!server.getSelfMember().hasPermission(hook.getInteraction().getGuildChannel(), p)) {
                    if (noPerms.length() > 0) {
                        noPerms.append(System.lineSeparator());
                    }

                    noPerms.append(p.getName());
                }
            }

            if (noPerms.length() > 0) {
                EmbedBuilder embed = EmbedFactory.GetEmbed(EmbedType.ERROR, ErrorKeys.PERMISSION_MISSING.name())
                        .setDescription(language.getError(ErrorKeys.PERMISSION_MISSING, new String[]{App.Shmames.getBotName(), noPerms.toString()}));

                try {
                    MessageService.ReplyToMessage(hook, embed, false);
                } catch (InsufficientPermissionException e) {
                    MessageService.ReplyToMessage(hook, language.getError(ErrorKeys.PERMISSION_MISSING, new String[]{App.Shmames.getBotName(), noPerms.toString()}), false);
                } catch (Exception e) {
                    LoggingService.LogException(e);
                }

                return;
            }
        }

        App.Shmames.getCommandHandler().HandleCommand(command, executingCommand, commandText);
    }
}
