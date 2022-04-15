package com.hadenwatne.shmames;

public class App {
    public static Shmames Shmames;
    public static boolean IsDebug = false;
    public static String Version;

    /**
     * The entry point for the bot.
     * @param args Program launch arguments.
     */
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("-debug")) {
            IsDebug = true;
        }

        Package appPackage = App.class.getPackage();
        Version = appPackage.getImplementationVersion();

        Shmames = new Shmames();

        Shmames.startup(IsDebug);
    }
}
