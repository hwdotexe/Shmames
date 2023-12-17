package com.hadenwatne.shmames.services;

import com.hadenwatne.corvus.Corvus;
import com.hadenwatne.corvus.CorvusBuilder;
import com.hadenwatne.fornax.command.Execution;
import com.hadenwatne.shmames.Shmames;
import com.hadenwatne.shmames.language.LanguageKey;
import com.hadenwatne.shmames.models.PollModel;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

public class PollService {
    public static CorvusBuilder BuildPoll(Shmames shmames, Execution execution, PollModel model) {
        CorvusBuilder builder = Corvus.info(shmames);

        builder.addBreadcrumbs("poll")
                .setTitle(model.isActive()
                        ? execution.getLanguageProvider().getMessageFromKey(execution, LanguageKey.POLL_TITLE.name())
                        : execution.getLanguageProvider().getMessageFromKey(execution, LanguageKey.POLL_TITLE_RESULTS.name()))
                .setFooter("Expire" + (model.isActive()
                        ? "s on " + TextFormatService.GetFriendlyDateTime(model.getExpires())
                        : "d"))
                .addField("Topic", model.getQuestion(), false);

        // Build options and vote results
        StringBuilder voteReadout = new StringBuilder();
        int totalVotes = countTotalVotes(model);

        for(int i=0; i<model.getOptions().size(); i++) {
            int votesForThisItem = countVotesForOption(model, i);
            double percentageOfTotal = (double) votesForThisItem / (double) totalVotes;
            String percentage = Math.round(percentageOfTotal * 100) + "%";

            if (!voteReadout.isEmpty()) {
                voteReadout.append(System.lineSeparator());
            }

            voteReadout.append("**")
                    .append(percentage)
                    .append("**")
                    .append(" • ")
                    .append(model.getOptions().get(i));
        }

        builder.addField("Options", voteReadout.toString(), false);

        StringSelectMenu.Builder menu = StringSelectMenu.create("pollDropdown")
                .setPlaceholder("Cast your vote")
                .setRequiredRange(1, model.getOptions().size());

        for(String option : model.getOptions()) {
            menu = menu.addOption(option, Integer.toString(model.getOptions().indexOf(option)));
        }

        builder.addLayoutComponent(ActionRow.of(menu.build()));

        return builder;
    }

    private static int countTotalVotes(PollModel model) {
        int totalVotes = 0;

        for(long user : model.getVotes().keySet()) {
            totalVotes += model.getVotes().get(user).size();
        }

        return totalVotes;
    }

    private static int countVotesForOption(PollModel model, int option) {
        int optionVotes = 0;

        for(long user : model.getVotes().keySet()) {
            for(int vote : model.getVotes().get(user)) {
                if(vote == option) {
                    optionVotes += 1;
                }
            }
        }

        return optionVotes;
    }
}
