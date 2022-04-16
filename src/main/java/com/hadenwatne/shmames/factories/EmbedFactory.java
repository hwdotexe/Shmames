package com.hadenwatne.shmames.factories;

import com.hadenwatne.shmames.App;
import com.hadenwatne.shmames.enums.EmbedType;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.List;

public class EmbedFactory {
    private static final String NAVIGATION_INDICATOR = " » ";

    /**
     * Creates a basic EmbedBuilder with pre-built headers and standardized colors for further customization.
     * @param type The style of embed to create.
     * @param navigation Strings to be shown in the header to indicate where the embed applies.
     * @return An EmbedBuilder object.
     */
    public static EmbedBuilder GetEmbed(EmbedType type, String... navigation) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        StringBuilder navigationHeader = new StringBuilder();

        embedBuilder.setColor(type.getColor());

        navigationHeader.append(App.Shmames.getBotName());

        for(String nav : navigation) {
            navigationHeader.append(NAVIGATION_INDICATOR);
            navigationHeader.append(nav);
        }

        embedBuilder.setAuthor(navigationHeader.toString(), null, App.Shmames.getBotAvatarUrl());

        return embedBuilder;
    }
}
