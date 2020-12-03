package com.hadenwatne.discordbot.commands;

import com.hadenwatne.discordbot.storage.Brain;
import com.hadenwatne.discordbot.storage.Lang;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.Nullable;

public class Tamagotchi implements ICommand {
    private Lang lang;
    private Brain brain;

    @Override
    public String getDescription() {
        return "Hatch and grow your very own Server Pet! What wonders wait in store?";
    }

    @Override
    public String getUsage() {
        return "tamagotchi";
    }

    @Override
    public String run(String args, User author, Message message) {
        /*
        Server Pet inspired by Tamagotchi

        Adult temperament is determined by how well the server takes care of the pet.

        - Egg hatching
        - Hunger meter, Happy meter, Discipline meter

        Hunger meter:
        - Snacks (sugar)
        - Meals (healthy)

        Happy meter:
        - Playing games
        - Feeding a snack

        Discipline meter:
        - "scold" when calls for attention but refuses to eat or play

        Will leave droppings around. Has a warmup, and players can potty train it during the duration.
        Gets sick if not cleaned or eats too many snacks. Medicine is used to heal.
        Will not play or eat when sick, and sickness can kill it.

        Baby > Child > Teenager > Adult > Senior

        Currency for purchasing different kinds of toys and meals. Earned by playing games.
         */

        /*
        JAMES VERSION
        ---

        Different kinds of creatures you can hatch:
        - Keanu Reeves
        - Rem Lazar
        - Catgirl
        - Waifu Body Pillow

        Earn currency by playing games like Hangman?
        Discipline meter affected by PRAISING or SCOLDING behavior for various random events.

        New kinds of random events that depend on the age:
        - Googled furry art
        - Didn't add black truffles to pasta sauce
        - Stock portfolio reported a loss
        - Tore up your cancer cure research notes
        - Doodled a mustache on your favorite costume mask
        - Sought a guru's training to build psychic abilities
        - Pooped outside the toilet
        - Demanded more play time

        Random events based on the type of creature:
        - Wasn't very breathtaking
        - Took kids to tour the twin towers
        - Accidentally said "owo" in a sentence
        - Got a suspicious stain

         */
        return null;
    }

    @Override
    public String[] getAliases() {
        return new String[]{"tamagotchi", "pet"};
    }

    @Override
    public void setRunContext(Lang lang, @Nullable Brain brain) {
        this.lang = lang;
        this.brain = brain;
    }

    @Override
    public boolean requiresGuild() {
        return true;
    }
}
