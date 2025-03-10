package com.hadenwatne.shmames;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class App {
    public static Shmames Shmames;
    public static Gson gson;
    public static boolean IsDebug = false;

    /**
     * The entry point for the bot.
     * @param args Program launch arguments.
     */
    public static void main(String[] args) {
        gson = new GsonBuilder().setPrettyPrinting().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").disableHtmlEscaping().create();

        if (args.length > 0 && args[0].equalsIgnoreCase("-debug")) {
            IsDebug = true;
        }

        Shmames = new Shmames();

        Shmames.startup(IsDebug);
    }
}
