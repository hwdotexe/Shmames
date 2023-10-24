package com.hadenwatne.botcore;

import com.hadenwatne.botcore.type.LogType;
import com.hadenwatne.botcore.service.LoggingService;

import java.util.ServiceLoader;

public class App {
    private static Bot _bot;
    private static boolean _debugMode = false;
    private static LoggingService _loggingService;

    public static void main(String[] args) {
        _loggingService = new LoggingService();

        if (args.length > 0 && args[0].equalsIgnoreCase("-debug")) {
            _debugMode = true;
            _loggingService.Log(LogType.SYSTEM, "Launching in Debug Mode");
        }

        detectImplementingBot();
    }

    public static LoggingService getLogger() {
        return _loggingService;
    }

    /**
     * Provided as a convenience for implementing classes that need to access these methods and services.
     * @return The loaded Bot object.
     */
    public static Bot getBot() {
        return _bot;
    }

    static boolean isDebugMode() {
        return _debugMode;
    }

    private static void detectImplementingBot() {
        ServiceLoader<Bot> loader = ServiceLoader.load(Bot.class);
        Bot loadedBot = loader.iterator().next();

        if (loadedBot != null) {
            _bot = loadedBot;
        } else {
            _loggingService.Log(LogType.ERROR, "No classes found implementing " + Bot.class.getCanonicalName() + "! Check your pom.xml");
        }
    }
}