package com.hadenwatne.shmames.services;

import com.hadenwatne.corvus.Corvus;
import com.hadenwatne.corvus.CorvusBuilder;
import com.hadenwatne.shmames.Shmames;
import com.hadenwatne.shmames.language.LanguageKey;
import com.hadenwatne.shmames.models.PollModel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.util.List;

public class PollService {
    public static CorvusBuilder BuildPoll(Shmames shmames,PollModel model) {
        CorvusBuilder builder = Corvus.info(shmames);

        builder.addBreadcrumbs("poll")
                .setTitle(model.isActive()
                        ? shmames.getLanguageProvider().getMessageFromKey(LanguageKey.POLL_TITLE.name())
                        : shmames.getLanguageProvider().getMessageFromKey(LanguageKey.POLL_TITLE_RESULTS.name()))
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
                .setRequiredRange(1, model.isMultiple()
                        ? model.getOptions().size()
                        : 1);

        for(String option : model.getOptions()) {
            menu = menu.addOption(option, Integer.toString(model.getOptions().indexOf(option)));
        }

        builder.addLayoutComponent(ActionRow.of(menu.build()));

        return builder;
    }

    public static void AddVote(PollModel model, User user, List<Integer> votes) {
        // Because Discord can reload the message, or a user can clear their cache,
        // any votes sent this way will override that user's previous votes.
        // This will prevent duplicate voting and allow the user to change their
        // selection if desired.
        model.getVotes().put(user.getIdLong(), votes);
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
