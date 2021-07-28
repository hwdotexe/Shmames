package com.hadenwatne.shmames.commandbuilder;

import com.hadenwatne.shmames.commands.ICommand;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.regex.Pattern;

public class CommandBuilder {
    public static CommandStructure Create(String name) {
        return new CommandStructure(name);
    }

    public static CommandData BuildCommandData(ICommand command) {
        CommandStructure structure = command.getCommandStructure();
        CommandData data = new CommandData(structure.getName(), command.getDescription());

        for(CommandParameter p : structure.getParameters()) {
            OptionData option = new OptionData(MapParameterType(p.getType()), p.getName().toLowerCase(), p.getDescription())
                .setRequired(p.isRequired());

            if(p.getSelectionOptions().size() > 0) {
                for(String so : p.getSelectionOptions()) {
                    option.addChoice(so, so);
                }
            }

            data.addOptions(option);
        }

        return data;
    }

    public static Pattern BuildPattern(CommandStructure command) {
        StringBuilder sb = new StringBuilder();

        for(CommandParameter p : command.getParameters()) {
            if(sb.length() > 0) {
                sb.append("\\s");
            } else {
                sb.append("^");
            }

            switch(p.getType()) {
                case SELECTION:
                    StringBuilder psb = new StringBuilder();

                    sb.append("(");

                    for(String o : p.getSelectionOptions()) {
                        if(psb.length() > 0) {
                            psb.append("|");
                        }

                        psb.append("(");
                        psb.append(o);
                        psb.append(")");
                    }

                    sb.append(psb);
                    sb.append(")");
                    break;
                default:
                    sb.append(p.getPattern().pattern());
            }

            if(!p.isRequired()) {
                sb.append("?");
            }
        }

        sb.append("$");

        return Pattern.compile(sb.toString(), Pattern.CASE_INSENSITIVE);
    }

    private static OptionType MapParameterType(ParameterType type) {
        switch(type) {
            case INTEGER:
                return OptionType.INTEGER;
            case BOOLEAN:
                return OptionType.BOOLEAN;
            case DISCORD_ROLE:
                return OptionType.ROLE;
            case DISCORD_USER:
                return OptionType.USER;
            case DISCORD_EMOTE:
                return OptionType.MENTIONABLE;
            case DISCORD_CHANNEL:
                return OptionType.CHANNEL;
            default:
                return OptionType.STRING;
        }
    }
}
